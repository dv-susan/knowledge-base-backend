package com.knowledge.base.graph.service;

import com.knowledge.base.graph.vo.*;

import java.util.List;

/**
 * 知识图谱Service接口
 *
 * <p>提供知识图谱相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface GraphService {

    /**
     * 获取节点列表
     *
     * @param type 节点类型
     * @return 节点列表
     */
    List<GraphNodeVO> getNodes(String type);

    /**
     * 获取边列表
     *
     * @param sourceType 源节点类型
     * @param targetType 目标节点类型
     * @return 边列表
     */
    List<GraphEdgeVO> getEdges(String sourceType, String targetType);

    /**
     * 获取节点关系
     *
     * @param nodeId 节点ID
     * @return 关系列表
     */
    List<GraphRelationVO> getNodeRelations(String nodeId);

    /**
     * 图谱搜索
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    List<GraphNodeVO> searchGraph(String keyword);

    /**
     * 路径分析
     *
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @param maxDepth 最大深度
     * @return 路径列表
     */
    List<GraphPathVO> analyzePath(String sourceId, String targetId, Integer maxDepth);

    /**
     * 社区检测
     *
     * @param algorithm 算法类型
     * @return 社区列表
     */
    List<GraphCommunityVO> detectCommunity(String algorithm);

    /**
     * 获取完整图谱数据
     *
     * @param type 节点类型
     * @return 图谱数据
     */
    GraphDataVO getGraphData(String type);

    /**
     * 删除指定文档的知识图谱节点
     * <p>删除 KnowledgeDocument 及其所有 DocumentChunk 子节点和关系，保留 KnowledgeEntity（实体可跨文档共享）。</p>
     *
     * @param docId 文档ID
     */
    void deleteByDocId(Long docId);

    /**
     * 清理脏图谱节点
     * <p>删除所有 docId 不在给定白名单中的 KnowledgeDocument 节点及其关联的 DocumentChunk 子节点。</p>
     *
     * @param validDocIds 有效的文档ID列表（来自MySQL）
     * @return 删除的节点数量
     */
    int cleanupGhostNodes(List<Long> validDocIds);

    /**
     * 清除所有图谱 Redis 缓存
     *
     * <p>知识图谱重建后由 kb-ai 调用，使所有 @Cacheable 缓存失效，
     * 下次查询时强制从 Neo4j 重新加载最新数据。</p>
     */
    void evictAllCaches();
}
