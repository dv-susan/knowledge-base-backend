package com.knowledge.base.ai.rag.kag.graph;

import java.util.List;

/**
 * 图谱构建服务接口
 *
 * <p>编排知识图谱构建流水线：拉取文档 → 分块 → 抽取实体关系 → 去重合并 → 写入Neo4j</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface GraphBuildService {

    /**
     * 为单个文档构建知识图谱
     *
     * @param docId 文档ID
     * @return 构建的实体数
     */
    int buildForDocument(Long docId);

    /**
     * 从图谱中删除指定文档的所有节点和关系
     *
     * @param docId 文档ID
     */
    void deleteForDocument(Long docId);

    /**
     * 全量重建所有已发布文档的知识图谱
     *
     * @return 处理的文档数
     */
    int buildAll();

    /**
     * 批量构建指定文档列表的知识图谱
     *
     * @param docIds 文档ID列表
     * @return 处理的文档数
     */
    int buildBatch(List<Long> docIds);

    /**
     * 异步发布单个文档图谱构建任务
     *
     * @param docId 文档ID
     * @return 任务ID
     */
    String publishBuildTask(Long docId);

    /**
     * 异步发布批量图谱构建任务
     *
     * @param docIds 文档ID列表
     * @return 任务ID
     */
    String publishBuildBatchTask(List<Long> docIds);

    /**
     * 异步发布全量图谱构建任务
     *
     * @return 任务ID
     */
    String publishBuildAllTask();

    /**
     * 异步发布图谱删除任务
     *
     * @param docId 文档ID
     * @return 任务ID
     */
    String publishDeleteTask(Long docId);
}
