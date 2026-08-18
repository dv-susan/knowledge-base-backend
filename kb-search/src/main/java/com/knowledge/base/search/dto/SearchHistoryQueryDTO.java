package com.knowledge.base.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搜索历史查询DTO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索历史查询请求")
public class SearchHistoryQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 关键词（模糊查询）
     */
    @Schema(description = "关键词")
    private String keyword;

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
     * 页码
     */
    @Schema(description = "页码")
    private Integer current = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Integer size = 20;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String sortBy = "createdAt";

    /**
     * 排序方向
     */
    @Schema(description = "排序方向")
    private String sortOrder = "desc";
}
