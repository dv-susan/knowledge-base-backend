package com.knowledge.base.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI反馈实体类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ai_feedback")
public class AiFeedback {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 对话ID
     */
    @TableField("conversation_id")
    private Long conversationId;

    /**
     * 消息ID
     */
    @TableField("message_id")
    private Long messageId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 反馈类型（positive/negative）
     */
    @TableField("feedback_type")
    private String feedbackType;

    /**
     * 反馈内容
     */
    @TableField("feedback_content")
    private String feedbackContent;

    /**
     * 评分（1-5分）
     */
    @TableField("rating")
    private Integer rating;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
