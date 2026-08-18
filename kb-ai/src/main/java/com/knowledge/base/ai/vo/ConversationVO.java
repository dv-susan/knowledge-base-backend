package com.knowledge.base.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话信息")
public class ConversationVO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 对话标题
     */
    @Schema(description = "对话标题")
    private String title;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String model;

    /**
     * Token使用量
     */
    @Schema(description = "Token使用量")
    private Integer tokensUsed;

    /**
     * 消息数量
     */
    @Schema(description = "消息数量")
    private Integer messageCount;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 消息列表
     */
    @Schema(description = "消息列表")
    private List<MessageVO> messages;
}
