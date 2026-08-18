package com.knowledge.base.graph.entity.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

/**
 * 知识实体图谱节点
 *
 * <p>从文档中LLM抽取的技术栈、API、配置项、核心概念等实体。
 * 映射 Neo4j KnowledgeEntity 标签。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node(primaryLabel = "KnowledgeEntity")
public class KnowledgeEntityNode {

    /** 实体名称（唯一标识） */
    @Id
    private String name;

    /** 实体类型 */
    @Property("type")
    private String type;

    /** 实体描述 */
    @Property("description")
    private String description;

    /** 别名列表 */
    @Property("aliases")
    private List<String> aliases;

    /** 首次出现的时间 */
    @Property("createdAt")
    private String createdAt;

    /** 最后更新时间 */
    @Property("updatedAt")
    private String updatedAt;

    /**
     * 实体类型枚举
     */
    public static final class EntityType {
        public static final String TECH_STACK = "TECH_STACK";
        public static final String API = "API";
        public static final String CONFIG = "CONFIG";
        public static final String CONCEPT = "CONCEPT";
        public static final String TOOL = "TOOL";
        public static final String PROCESS = "PROCESS";

        private EntityType() {}
    }
}
