package com.knowledge.base.graph.entity.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 文档分块图谱节点
 *
 * <p>映射 Neo4j DocumentChunk 标签，将文本块作为节点存储，
 * 与 KnowledgeEntity 通过 MENTIONS 关系连接。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node(primaryLabel = "DocumentChunk")
public class DocumentChunkNode {

    /** 块唯一ID */
    @Id
    private String chunkId;

    /** 来源文档ID */
    @Property("docId")
    private Long docId;

    /** 块文本内容 */
    @Property("content")
    private String content;

    /** 所属章节标题 */
    @Property("heading")
    private String heading;

    /** 块在文档中的序号 */
    @Property("chunkIndex")
    private Integer chunkIndex;

    /** 文档总分块数 */
    @Property("totalChunks")
    private Integer totalChunks;

    /** 分类ID */
    @Property("categoryId")
    private Long categoryId;

    /** 创建时间 */
    @Property("createdAt")
    private String createdAt;
}
