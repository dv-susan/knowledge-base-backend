package com.knowledge.base.graph.entity.relationship;

import com.knowledge.base.graph.entity.node.KnowledgeEntityNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * 分块提及实体关系
 *
 * <p>DocumentChunk -[MENTIONS]-> KnowledgeEntity</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelationshipProperties
public class MentionsRelation {

    @RelationshipId
    private Long id;

    /** 提及置信度（0.0-1.0） */
    @Property("confidence")
    private Double confidence;

    /** 来源分块ID */
    @Property("chunkId")
    private String chunkId;

    @TargetNode
    private KnowledgeEntityNode entity;
}
