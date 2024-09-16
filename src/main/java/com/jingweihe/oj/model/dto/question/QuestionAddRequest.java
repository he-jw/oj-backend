package com.jingweihe.oj.model.dto.question;

import cn.hutool.json.JSONUtil;
import com.jingweihe.oj.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 */
@Data
public class QuestionAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 判题用例（json 数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;

    private static final long serialVersionUID = 1L;

    /**
     * 请求对象转实体对象
     *
     * @param questionAddRequest
     * @return question
     */
    public static Question requestToObj(QuestionAddRequest questionAddRequest) {
        if (questionAddRequest == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCases = questionAddRequest.getJudgeCase();
        if (judgeCases != null && !judgeCases.isEmpty()) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCases));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        return question;
    }
}
