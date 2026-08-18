package com.knowledge.base.search.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG混合搜索结果项（匹配 kb-ai 的 RagSearchResultVO）
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagSearchItemVO {

    /** 块ID */
    private String chunkId;

    /** 来源文档ID */
    private Long documentId;

    /** 来源文档标题 */
    private String documentTitle;

    /** 块文本内容 */
    private String content;

    /** 所属章节标题 */
    private String heading;

    /** 融合/重排序得分 */
    private double score;

    /** BM25得分 */
    private double bm25Score;

    /** 向量相似度得分 */
    private double vectorScore;

    /** 文档发布时间 */
    private String publishTime;
}
