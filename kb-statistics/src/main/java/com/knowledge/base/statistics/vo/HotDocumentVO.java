package com.knowledge.base.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 热门文档VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "热门文档")
public class HotDocumentVO {

    @Schema(description = "文档ID")
    private Long documentId;

    @Schema(description = "文档标题")
    private String title;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "浏览次数")
    private Long viewCount;

    @Schema(description = "点赞数")
    private Long likeCount;

    @Schema(description = "收藏数")
    private Long favoriteCount;

    @Schema(description = "评论数")
    private Long commentCount;

    @Schema(description = "文档摘要")
    private String summary;

    @Schema(description = "创建时间")
    private String createdAt;

    @Schema(description = "统计数值")
    private Long statisticsValue;
}
