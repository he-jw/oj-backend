package com.jingweihe.oj.judge.strategy.impl;

import cn.hutool.json.JSONUtil;
import com.jingweihe.oj.judge.strategy.JudgeContext;
import com.jingweihe.oj.judge.strategy.JudgeStrategy;
import com.jingweihe.oj.judge.strategy.JudgeStrategyConfig;
import com.jingweihe.oj.model.dto.question.JudgeConfig;
import com.jingweihe.oj.model.dto.questionsubmit.JudgeInfo;
import com.jingweihe.oj.model.entity.Question;
import com.jingweihe.oj.model.enums.JudgeInfoMessageEnum;
import com.jingweihe.oj.model.enums.QuestionSubmitLanguageEnum;

import java.util.List;

/**
 * JAVA语言的判题策略
 */
@JudgeStrategyConfig(language = "java")
public class JavaLanguageJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();

        // 1）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        // 1.1 先判断沙箱执行的结果输出数量和预期输出数量是否相等
        if (inputList.size() != outputList.size()){
            return judgeInfoResponse;
        }
        // 1.2 依次判断每一项的输出和预期输出是否相等
        for (int i = 0; i < outputList.size(); i++) {
            if (!outputList.get(i).equals(inputList.get(i))){
                return judgeInfoResponse;
            }
        }
        // 1.3 判断题目的限制是否符合需求
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);

        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);

        Long timeLimit = judgeConfig.getTimeLimit();
        Long memoryLimit = judgeConfig.getMemoryLimit();
        if (memory > memoryLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // Java程序本身需要额外执行10秒钟
        long JAVA_PROGRAM_TIME_COST = 10000L;
        if ((time - JAVA_PROGRAM_TIME_COST) > timeLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
