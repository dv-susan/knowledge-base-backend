package com.knowledge.base.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI模型信息VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI模型信息")
public class ModelVO {

    /**
     * 模型标识
     */
    @Schema(description = "模型标识")
    private String key;

    /**
     * 模型显示名称
     */
    @Schema(description = "模型显示名称")
    private String displayName;

    /**
     * 模型描述
     */
    @Schema(description = "模型描述")
    private String description;

    /**
     * 是否默认模型
     */
    @Schema(description = "是否默认模型")
    private Boolean isDefault;
}
