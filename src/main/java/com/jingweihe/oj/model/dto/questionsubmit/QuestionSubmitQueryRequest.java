package com.jingweihe.oj.model.dto.questionsubmit;

import com.jingweihe.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询题目提交请求
 *
 *
 *
 */
@Data
@EqualsAndHashCode(callSuper = true) //继承父类时去重写EqualsAndHashCode方法
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {


    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态
     */
    private Integer status;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
