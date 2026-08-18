package com.knowledge.base.ai.service.impl;

import com.knowledge.base.ai.dto.FeedbackDTO;
import com.knowledge.base.ai.entity.AiFeedback;
import com.knowledge.base.ai.mapper.AiFeedbackMapper;
import com.knowledge.base.ai.service.AiFeedbackService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI反馈Service实现
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class AiFeedbackServiceImpl implements AiFeedbackService {

    @Resource
    private AiFeedbackMapper feedbackMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitFeedback(FeedbackDTO feedbackDTO, Long userId) {
        AiFeedback feedback = new AiFeedback();
        BeanUtils.copyProperties(feedbackDTO, feedback);
        feedback.setUserId(userId);
        return feedbackMapper.insert(feedback) > 0;
    }

    /** {@inheritDoc} */
    @Override
    public List<AiFeedback> getUserFeedbacks(Long userId) {
        return feedbackMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiFeedback>()
                .eq(AiFeedback::getUserId, userId)
                .orderByDesc(AiFeedback::getCreatedAt)
        );
    }
}
