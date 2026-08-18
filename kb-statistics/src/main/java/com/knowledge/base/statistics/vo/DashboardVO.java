package com.knowledge.base.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 仪表盘综合数据VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "仪表盘综合数据")
public class DashboardVO {

    @Schema(description = "数据概览")
    private OverviewVO overview;

    @Schema(description = "文档趋势（近7日）")
    private List<TrendVO> documentTrend;

    @Schema(description = "分类分布")
    private List<CategoryDistributionVO> categoryDistribution;

    @Schema(description = "热门文档")
    private List<HotDocumentVO> hotDocuments;

    @Schema(description = "活跃用户")
    private List<ActiveUserVO> activeUsers;
}
