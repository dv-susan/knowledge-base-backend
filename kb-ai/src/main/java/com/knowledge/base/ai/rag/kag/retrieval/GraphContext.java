package com.knowledge.base.ai.rag.kag.retrieval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * KAG 图谱检索上下文
 *
 * <p>包含从知识图谱中检索到的结构化知识：
 * 匹配实体、推理路径、关联文本块等。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 匹配的知识实体列表 */
    private List<GraphEntity> matchedEntities;

    /** 图谱推理路径列表 */
    private List<GraphPath> reasoningPaths;

    /** 通过图谱关联到的文档文本块（去重后） */
    private List<GraphChunk> associatedChunks;

    /** 是否从图谱中找到了相关信息 */
    @Builder.Default
    private boolean hasResults = false;

    /**
     * 图谱实体（精简版）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphEntity implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String type;
        private String description;
        private int connectionCount;
    }

    /**
     * 图谱推理路径
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphPath implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 路径上的节点名称列表 */
        private List<String> nodes;
        /** 路径上的关系类型列表 */
        private List<String> relations;
        /** 跳数 */
        private int hops;
    }

    /**
     * 图谱关联的文本块
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphChunk implements Serializable {
        private static final long serialVersionUID = 1L;
        private String chunkId;
        private Long docId;
        private String docTitle;
        private String content;
        private String heading;
        /** 来源实体名 */
        private String entityName;
        /** 来源关系路径 */
        private String sourcePath;
    }
}
