package com.knowledge.base.ai.rag.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

/**
 * ES知识库文档块映射
 *
 * <p>对应 kb_chunk 索引，存储文档分块及其向量嵌入。
 * 支持 BM25 全文搜索 + kNN 向量搜索的混合检索。</p>
 *
 * <p><b>注意</b>：embedding 字段通过 low-level ElasticsearchClient 写入，
 * 因为 Spring Data Elasticsearch 5.x 的 @Field 对 dense_vector 支持有限。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "kb_chunk", createIndex = false)
public class KbChunkDoc {

    /** 块唯一ID（UUID） */
    @Id
    @Field(name = "chunk_id", type = FieldType.Keyword)
    private String chunkId;

    /** 来源文档ID */
    @Field(name = "document_id", type = FieldType.Long)
    private Long documentId;

    /** 来源文档标题 */
    @Field(name = "document_title", type = FieldType.Text)
    private String documentTitle;

    /** 块文本内容 */
    @Field(name = "content", type = FieldType.Text)
    private String content;

    /** 所属章节标题 */
    @Field(name = "heading", type = FieldType.Keyword)
    private String heading;

    /** 块在文档中的序号（从0开始） */
    @Field(name = "chunk_index", type = FieldType.Integer)
    private Integer chunkIndex;

    /** 文档总分块数 */
    @Field(name = "total_chunks", type = FieldType.Integer)
    private Integer totalChunks;

    /** 分类ID */
    @Field(name = "category_id", type = FieldType.Long)
    private Long categoryId;

    /** 作者ID */
    @Field(name = "author_id", type = FieldType.Long)
    private Long authorId;

    /** 团队ID */
    @Field(name = "team_id", type = FieldType.Long)
    private Long teamId;

    /** 文档状态 */
    @Field(name = "doc_status", type = FieldType.Integer)
    private Integer docStatus;

    /** 文档发布时间 */
    @Field(name = "publish_time", type = FieldType.Date)
    private String publishTime;

    /** 索引时间（仅存储用，不参与搜索映射） */
    private LocalDateTime indexedAt;

    // embedding 字段（dense_vector）通过 low-level ES client 处理
    // 不在此处声明 @Field 注解
}
