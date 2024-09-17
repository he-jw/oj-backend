package com.jingweihe.oj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jingweihe.oj.common.ErrorCode;
import com.jingweihe.oj.constant.CommonConstant;
import com.jingweihe.oj.constant.UserConstant;
import com.jingweihe.oj.exception.BusinessException;
import com.jingweihe.oj.judge.JudgeService;
import com.jingweihe.oj.mapper.QuestionSubmitMapper;
import com.jingweihe.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.jingweihe.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.jingweihe.oj.model.entity.Question;
import com.jingweihe.oj.model.entity.QuestionSubmit;
import com.jingweihe.oj.model.entity.User;
import com.jingweihe.oj.model.enums.QuestionSubmitLanguageEnum;
import com.jingweihe.oj.model.enums.QuestionSubmitStatusEnum;
import com.jingweihe.oj.model.vo.QuestionSubmitVO;
import com.jingweihe.oj.service.QuestionService;
import com.jingweihe.oj.service.QuestionSubmitService;
import com.jingweihe.oj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 86182
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-09-15 18:06:25
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit> implements QuestionSubmitService{

    @Resource
    @Lazy
    private JudgeService judgeService;
    @Resource
    private QuestionService questionService;

    /**
     * 提交问题答案
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 效验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if(languageEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionSubmitAddRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 设置初始值
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setUserId(loginUser.getId());
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        judgeService.doJudge(questionSubmit.getId());
        return questionSubmit.getId();
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到MyBatis plus框架支持的查询QueryWrapper类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long userId = questionSubmitQueryRequest.getUserId();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.like(status!=null, "status", status);
        queryWrapper.like(questionId!=null, "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交userId和登录用户Id不同）提交的代码
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole()) && questionSubmitVO.getUserId() != loginUser.getId()){
            // 脱敏处理
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }

        // List<QuestionSubmitVO> questionSubmitVOList = new ArrayList<>();
        // for (QuestionSubmit questionSubmit : questionSubmitList) {
        //     QuestionSubmitVO questionSubmitVO = this.getQuestionSubmitVO(questionSubmit, request);
        //     questionSubmitVOList.add(questionSubmitVO);
        // }


        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList
                .stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());

        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




