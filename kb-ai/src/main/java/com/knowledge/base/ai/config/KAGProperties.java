package com.knowledge.base.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * KAG（知识图谱增强生成）配置属性
 *
 * <p>集中管理所有KAG相关配置参数，支持通过 application.yml 或环境变量覆盖。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "kag")
public class KAGProperties {

    /** 是否启用KAG功能 */
    private boolean enabled = true;

    /** 实体抽取配置 */
    private Extraction extraction = new Extraction();

    /** 图谱构建配置 */
    private Graph graph = new Graph();

    /** 图谱检索配置 */
    private Retrieval retrieval = new Retrieval();

    /** 混合融合配置 */
    private Fusion fusion = new Fusion();

    /** 异步构建配置 */
    private Async async = new Async();

    @Data
    public static class Extraction {
        /** 每批处理的文本块数 */
        private int batchSize = 5;
        /** 每个文本块最多抽取的实体数 */
        private int maxEntitiesPerChunk = 10;
        /** 每个文本块最多抽取的关系数 */
        private int maxRelationsPerChunk = 15;
        /** 抽取使用的LLM模型 */
        private String model = "qwen";
        /** 抽取重试次数 */
        private int maxRetries = 2;
        /** 实体相似度合并阈值 */
        private double similarityThreshold = 0.85;
    }

    @Data
    public static class Graph {
        /** 图谱数据库名称 */
        private String database = "neo4j";
        /** 是否在构建时先清空旧数据 */
        private boolean clearBeforeBuild = false;
        /** 批量写入大小 */
        private int batchWriteSize = 50;
    }

    @Data
    public static class Retrieval {
        /** 图谱遍历最大跳数 */
        private int maxHops = 2;
        /** 每跳最大展开节点数 */
        private int maxEntitiesPerQuery = 10;
        /** 每个实体关联的最大文本块数 */
        private int maxChunksPerEntity = 3;
        /** 检索超时（秒） */
        private int timeoutSeconds = 10;
    }

    @Data
    public static class Fusion {
        /** 是否启用LLM重排序 */
        private boolean rerankEnabled = true;
        /** RAG结果和KAG结果的融合权重比（RAG:KAG） */
        private double ragWeight = 0.5;
        private double kagWeight = 0.5;
    }

    @Data
    public static class Async {
        /** 是否启用异步构建 */
        private boolean enabled = true;
        /** 每批处理的文档数 */
        private int buildBatchSize = 5;
        /** 最大重试次数 */
        private int maxRetries = 3;
    }
}
