package com.knowledge.base.ai.service;

import com.knowledge.base.ai.dto.FeedbackDTO;
import com.knowledge.base.ai.entity.AiFeedback;

import java.util.List;

/**
 * AI反馈Service接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface AiFeedbackService {

    /**
     * 提交反馈
     *
     * @param feedbackDTO 反馈信息
     * @param userId      用户ID
     * @return 是否成功
     */
    Boolean submitFeedback(FeedbackDTO feedbackDTO, Long userId);

    /**
     * 获取用户反馈列表
     *
     * @param userId 用户ID
     * @return 反馈列表
     */
    List<AiFeedback> getUserFeedbacks(Long userId);
}
