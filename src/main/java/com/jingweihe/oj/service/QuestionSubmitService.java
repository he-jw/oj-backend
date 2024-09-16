package com.jingweihe.oj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jingweihe.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.jingweihe.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.jingweihe.oj.model.entity.QuestionSubmit;
import com.jingweihe.oj.model.entity.User;
import com.jingweihe.oj.model.vo.QuestionSubmitVO;

/**
* @author 86182
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-09-15 18:06:25
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param request
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

}
