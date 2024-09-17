package com.jingweihe.oj.judge;

import cn.hutool.json.JSONUtil;
import com.jingweihe.oj.common.ErrorCode;
import com.jingweihe.oj.exception.BusinessException;
import com.jingweihe.oj.judge.codesandbox.CodeSandBox;
import com.jingweihe.oj.judge.codesandbox.CodeSandBoxFactory;
import com.jingweihe.oj.judge.codesandbox.CodeSandBoxProxy;
import com.jingweihe.oj.judge.codesandbox.mode.ExecuteCodeRequest;
import com.jingweihe.oj.judge.codesandbox.mode.ExecuteCodeResponse;
import com.jingweihe.oj.judge.strategy.JudgeContext;
import com.jingweihe.oj.judge.strategy.JudgeExecutor;
import com.jingweihe.oj.judge.strategy.JudgeManager;
import com.jingweihe.oj.model.dto.question.JudgeCase;
import com.jingweihe.oj.model.dto.questionsubmit.JudgeInfo;
import com.jingweihe.oj.model.entity.Question;
import com.jingweihe.oj.model.entity.QuestionSubmit;
import com.jingweihe.oj.model.enums.QuestionSubmitStatusEnum;
import com.jingweihe.oj.service.QuestionService;
import com.jingweihe.oj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Resource
    private JudgeExecutor judgeExecutor;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交Id，获取到对相应的题目，提交信息（包含代码、编程语言等）
        // 1.1 获取题目提交信息
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"提交信息不存在");
        }
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        Long questionId = questionSubmit.getQuestionId();
        Integer statusBefore = questionSubmit.getStatus();
        // 1.1.1 如果题目提交状态不为等待中，就不用重复执行了
        if (!Objects.equals(QuestionSubmitStatusEnum.WAITING.getValue(), statusBefore)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 1.1.2 更改题目提交的状态为“判题中"，防止判题过程中重复执行，也能让用户及时看到状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean updateStatus = questionSubmitService.updateById(questionSubmitUpdate);
        if (!updateStatus) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        // 1.2 获取题目信息
        Question question = questionService.getById(questionId);
        if (question == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        }
        String judgeCaseStr = question.getJudgeCase();
        List<String> inputList = JSONUtil.toList(judgeCaseStr, JudgeCase.class)
                .stream()
                .map(JudgeCase::getInput)
                .collect(Collectors.toList());
        // 1.3 构建调用代码沙箱的请求参数
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(inputList);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage(language);

        // 2）调用沙箱，获取到执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        CodeSandBoxProxy codeSandBoxProxy = new CodeSandBoxProxy(codeSandBox);
        ExecuteCodeResponse executeCodeResponse = codeSandBoxProxy.executeCode(executeCodeRequest);
        // 2.1 解析执行结果
        List<String> outputList = executeCodeResponse.getOutputList();
        Integer status = executeCodeResponse.getStatus();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();

        // 3）构建判题请求参数，执行判题
        // 3.1 构建参数
        JudgeContext judgeContext = JudgeContext.builder()
                .judgeInfo(judgeInfo)
                .outputList(outputList)
                .inputList(inputList)
                .question(question)
                .questionSubmit(questionSubmit)
                .build();
        // 3.2 执行判题
        JudgeInfo judgeInfoResponse = judgeExecutor.doJudge(judgeContext);
        // 4.修改数据库中的判题结果
        QuestionSubmit questionSubmitUpdate1 = new QuestionSubmit();
        questionSubmitUpdate1.setId(questionSubmitId);
        questionSubmitUpdate1.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoResponse));
        questionSubmitUpdate1.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate1);
        if (!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 5.返回
        return questionSubmitService.getById(questionSubmitId);
    }
}
