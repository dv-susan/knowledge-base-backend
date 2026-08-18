package com.knowledge.base.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG检索结果VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "RAG检索结果")
public class RagSearchResultVO {

    @Schema(description = "块ID")
    private String chunkId;

    @Schema(description = "来源文档ID")
    private Long documentId;

    @Schema(description = "来源文档标题")
    private String documentTitle;

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

    @Schema(description = "文档发布时间")
    private String publishTime;
}
