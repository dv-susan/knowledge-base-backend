package com.knowledge.base.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统计查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统计查询参数")
public class StatisticsQueryDTO implements Serializable {

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
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    private Long categoryId;

    /**
     * 团队ID
     */
    @Schema(description = "团队ID")
    private Long teamId;

    /**
     * 文档状态
     */
    @Schema(description = "文档状态")
    private Integer documentStatus;

    /**
     * 是否包含子分类
     */
    @Schema(description = "是否包含子分类")
    private Boolean includeChildren;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortBy;

    /**
     * 排序方向
     */
    @Schema(description = "排序方向")
    private String sortOrder;

    /**
     * 数量限制
     */
    @Schema(description = "数量限制")
    private Integer limit;
}
