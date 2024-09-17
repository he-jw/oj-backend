package com.jingweihe.oj.judge.strategy;

import com.jingweihe.oj.judge.strategy.impl.DefaultJudgeStrategy;
import com.jingweihe.oj.judge.strategy.impl.JavaLanguageJudgeStrategy;
import com.jingweihe.oj.model.dto.questionsubmit.JudgeInfo;
import com.jingweihe.oj.model.entity.QuestionSubmit;
import com.jingweihe.oj.model.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
@Deprecated
public class JudgeManager {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (QuestionSubmitLanguageEnum.JAVA.getValue().equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
