package com.knowledge.base.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 趋势查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "趋势查询参数")
public class TrendQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 时间间隔（day、week、month）
     */
    @Schema(description = "时间间隔")
    private String interval;

    /**
     * 趋势类型（document、user、view、like、comment）
     */
    @Schema(description = "趋势类型")
    private String trendType;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 团队ID
     */
    @Schema(description = "团队ID")
    private Long teamId;

    /**
     * 是否包含子分类
     */
    @Schema(description = "是否包含子分类")
    private Boolean includeChildren;

    /**
     * 数据点数量
     */
    @Schema(description = "数据点数量")
    private Integer dataPoints;
}
