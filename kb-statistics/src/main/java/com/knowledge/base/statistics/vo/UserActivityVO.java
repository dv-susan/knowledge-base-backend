package com.knowledge.base.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户活跃度VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户活跃度")
public class UserActivityVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "创建文档数")
    private Long documentCount;

    @Schema(description = "评论数")
    private Long commentCount;

    @Schema(description = "浏览数")
    private Long viewCount;

    @Schema(description = "活跃度评分")
    private Double activityScore;
}
