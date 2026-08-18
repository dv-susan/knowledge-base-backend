package com.knowledge.base.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档处理DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文档处理参数")
public class DocumentProcessDTO {

    /**
     * 文档内容
     */
    @Schema(description = "文档内容")
    @NotBlank(message = "文档内容不能为空")
    private String content;

    /**
     * 文档标题
     */
    @Schema(description = "文档标题")
    private String title;

    /**
     * 处理类型（summary/outline/expansion/optimization/example）
     */
    @Schema(description = "处理类型")
    @NotBlank(message = "处理类型不能为空")
    private String processType;

    /**
     * 处理参数
     */
    @Schema(description = "处理参数")
    private ProcessParams processParams;

    /**
     * 处理参数
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "处理参数")
    public static class ProcessParams {

        /**
         * 摘要长度
         */
        @Schema(description = "摘要长度")
        private Integer summaryLength;

        /**
         * 大纲层级
         */
        @Schema(description = "大纲层级")
        private Integer outlineLevel;

        /**
         * 扩展类型
         */
        @Schema(description = "扩展类型")
        private String expansionType;

        /**
         * 优化目标
         */
        @Schema(description = "优化目标")
        private String optimizationTarget;

        /**
         * 示例类型
         */
        @Schema(description = "示例类型")
        private String exampleType;
    }
}
