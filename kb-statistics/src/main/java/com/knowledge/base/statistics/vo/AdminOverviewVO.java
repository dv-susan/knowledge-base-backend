package com.knowledge.base.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理后台概览VO
 *
 * <p>用于管理后台仪表盘，提供关键系统指标的综合概览</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "管理后台概览数据")
public class AdminOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "总文档数")
    private Long totalDocuments;

    @Schema(description = "待审核文档数")
    private Long pendingReviews;

    @Schema(description = "系统健康度（0-100）")
    private Double systemHealth;

    @Schema(description = "总角色数")
    private Long totalRoles;

    @Schema(description = "总分类数")
    private Long totalCategories;

    @Schema(description = "总团队数")
    private Long totalTeams;

    @Schema(description = "总评论数")
    private Long totalComments;

    @Schema(description = "总点赞数")
    private Long totalLikes;

    @Schema(description = "总收藏数")
    private Long totalFavorites;

    @Schema(description = "总浏览数")
    private Long totalViews;

    @Schema(description = "AI智能搜索次数")
    private Long aiSearchCount;

    @Schema(description = "AI问答次数")
    private Long aiQaCount;
}
