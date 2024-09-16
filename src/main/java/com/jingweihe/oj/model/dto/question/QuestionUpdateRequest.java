package com.jingweihe.oj.model.dto.question;

import cn.hutool.json.JSONUtil;
import com.jingweihe.oj.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
 *
 *
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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
     * @param questionUpdateRequest
     * @return question
     */
    public static Question requestToObj(QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        List<JudgeCase> judgeCases = questionUpdateRequest.getJudgeCase();
        if (judgeCases != null && !judgeCases.isEmpty()) {
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCases));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        return question;
    }
}
