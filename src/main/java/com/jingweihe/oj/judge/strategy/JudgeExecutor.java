package com.jingweihe.oj.judge.strategy;

import com.jingweihe.oj.common.ErrorCode;
import com.jingweihe.oj.exception.BusinessException;
import com.jingweihe.oj.model.dto.questionsubmit.JudgeInfo;
import com.jingweihe.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class JudgeExecutor {

    @Resource
    private List<JudgeStrategy> judgeStrategyList;

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        if (language == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }
        // 根据注解获取策略
        for (JudgeStrategy judgeStrategy : judgeStrategyList) {
            if (judgeStrategy.getClass().isAnnotationPresent(JudgeStrategyConfig.class)) {
                JudgeStrategyConfig judgeStrategyConfig = judgeStrategy.getClass().getAnnotation(JudgeStrategyConfig.class);
                if (language.equals(judgeStrategyConfig.language())) {
                    return judgeStrategy.doJudge(judgeContext);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
