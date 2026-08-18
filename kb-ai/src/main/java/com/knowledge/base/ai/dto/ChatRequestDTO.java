package com.knowledge.base.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI对话请求DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI对话请求参数")
public class ChatRequestDTO {

    /**
     * 对话ID（首次对话为空）
     */
    @Schema(description = "对话ID")
    private Long conversationId;

    /**
     * 用户消息
     */
    @Schema(description = "用户消息")
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * AI模型名称（qianwen | deepseek）
     */
    @Schema(description = "AI模型名称：qwen（通义千问）| deepseek（DeepSeek）")
    private String model;

    /**
     * 系统提示词
     */
    @Schema(description = "系统提示词")
    private String systemPrompt;

    /**
     * 历史消息
     */
    @Schema(description = "历史消息")
    private List<MessageDTO> history;

    /**
     * 流式响应标志
     */
    @Schema(description = "是否流式响应")
    @Builder.Default
    private Boolean stream = false;

    /**
     * 最大Token数
     */
    @Schema(description = "最大Token数")
    private Integer maxTokens;

    /**
     * 温度参数
     */
    @Schema(description = "温度参数")
    private Double temperature;

    /**
     * 是否启用知识库检索增强（RAG）
     */
    @Schema(description = "是否启用知识库检索增强")
    @Builder.Default
    private boolean enableRag = false;

    /**
     * 是否启用知识图谱增强（KAG）
     */
    @Schema(description = "是否启用知识图谱增强")
    @Builder.Default
    private boolean enableKAG = false;

    /**
     * 消息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "消息信息")
    public static class MessageDTO {

        /**
         * 角色类型
         */
        @Schema(description = "角色类型")
        private String role;

        /**
         * 消息内容
         */
        @Schema(description = "消息内容")
        private String content;
    }
}
