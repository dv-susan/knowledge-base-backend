package com.knowledge.base.graph.entity.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

/**
 * 知识文档图谱节点
 *
 * <p>映射 Neo4j KnowledgeDocument 标签，存储文档元数据。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node(primaryLabel = "KnowledgeDocument")
public class KnowledgeDocumentNode {

    /** 文档ID（来自kb-document MySQL） */
    @Id
    private Long docId;

    /** 文档标题 */
    @Property("title")
    private String title;

    /** 文档摘要 */
    @Property("summary")
    private String summary;

    /** 分类ID */
    @Property("categoryId")
    private Long categoryId;

    /** 作者ID */
    @Property("authorId")
    private Long authorId;

    /** 作者名 */
    @Property("authorName")
    private String authorName;

    /** 文档状态 0-草稿 1-已发布 2-已归档 */
    @Property("status")
    private Integer status;

    /** 发布时间 */
    @Property("publishTime")
    private String publishTime;

    /** 文档类型 1-文章 2-文件 */
    @Property("documentType")
    private Integer documentType;

    /** 标签（逗号分隔） */
    @Property("tags")
    private String tags;

    /** 创建时间 */
    @Property("createdAt")
    private String createdAt;

    /** 更新时间 */
    @Property("updatedAt")
    private String updatedAt;
}
