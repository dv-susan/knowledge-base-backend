package com.knowledge.base.search.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索结果VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索结果")
public class SearchResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID")
    private Long id;

    /**
     * 文档标题（高亮）
     */
    @Schema(description = "文档标题")
    private String title;

    /**
     * 文档摘要（高亮）
     */
    @Schema(description = "文档摘要")
    private String summary;

    /**
     * 高亮片段
     */
    @Schema(description = "高亮片段")
    private List<String> highlights;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 标签名称列表
     */
    @Schema(description = "标签名称列表")
    private List<String> tagNames;

    /**
     * 创建者名称
     */
    @Schema(description = "创建者名称")
    private String creatorName;

    /**
     * 团队名称
     */
    @Schema(description = "团队名称")
    private String teamName;

    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数")
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @Schema(description = "点赞次数")
    private Integer likeCount;

    /**
     * 评论次数
     */
    @Schema(description = "评论次数")
    private Integer commentCount;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private String publishAt;

    /**
     * 相关度得分
     */
    @Schema(description = "相关度得分")
    private Float score;

    /**
     * BM25得分（混合搜索模式）
     */
    @Schema(description = "BM25得分")
    private Double bm25Score;

    /**
     * 向量相似度得分（混合搜索模式）
     */
    @Schema(description = "向量相似度得分")
    private Double vectorScore;

    /**
     * LLM重排序得分（混合搜索模式）
     */
    @Schema(description = "LLM重排序得分")
    private Double rerankScore;

    /**
     * 文档块结果列表（混合搜索模式）
     */
    @Schema(description = "文档块结果列表")
    private List<ChunkResult> chunks;

    /**
     * 文档块结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "文档块检索结果")
    public static class ChunkResult implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "块ID")
        private String chunkId;

        @Schema(description = "块文本内容")
        private String content;

        @Schema(description = "所属章节标题")
        private String heading;

        @Schema(description = "融合/重排序得分")
        private double score;

        @Schema(description = "BM25得分")
        private double bm25Score;

        @Schema(description = "向量相似度得分")
        private double vectorScore;
    }
}
