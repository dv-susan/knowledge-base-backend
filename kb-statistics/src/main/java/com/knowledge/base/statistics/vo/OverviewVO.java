package com.knowledge.base.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据概览VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "数据概览信息")
public class OverviewVO {

    @Schema(description = "文档总数")
    private Long totalDocuments;

    @Schema(description = "用户总数")
    private Long totalUsers;

    @Schema(description = "今日新增文档")
    private Long todayDocuments;

    @Schema(description = "今日新增用户")
    private Long todayUsers;

    @Schema(description = "总浏览次数")
    private Long totalViews;

    @Schema(description = "今日浏览次数")
    private Long todayViews;

    @Schema(description = "总点赞数")
    private Long totalLikes;

    @Schema(description = "总收藏数")
    private Long totalFavorites;

    @Schema(description = "总评论数")
    private Long totalComments;

    @Schema(description = "待审核文档数")
    private Long pendingReviews;

    @Schema(description = "AI智能搜索次数")
    private Long aiSearchCount;

    @Schema(description = "AI问答次数")
    private Long aiQaCount;

    @Schema(description = "活跃用户数（近30天）")
    private Long activeUserCount;
}
