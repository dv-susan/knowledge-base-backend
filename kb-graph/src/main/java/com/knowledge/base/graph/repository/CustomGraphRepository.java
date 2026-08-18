package com.knowledge.base.graph.repository;

import com.knowledge.base.graph.dto.*;

import java.util.List;

/**
 * 自定义图谱查询Repository
 *
 * <p>使用 Neo4jClient 执行复杂 Cypher 查询：
 * 多跳路径分析、社区检测、图谱数据导出等。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface CustomGraphRepository {

    /**
     * 查询两实体间的所有最短路径
     *
     * @param source   源实体名称
     * @param target   目标实体名称
     * @param maxDepth 最大深度
     * @return 路径列表
     */
    List<GraphPathResultDTO> findShortestPaths(String source, String target, int maxDepth);

    /**
     * 从实体出发进行多跳遍历
     *
     * @param entityName 起始实体名称
     * @param maxHops    最大跳数
     * @param limit      返回节点数限制
     * @return 遍历到的实体列表（含关系）
     */
    List<TraverseResultDTO> traverseFromEntity(String entityName, int maxHops, int limit);

    /**
     * 获取图谱统计信息
     *
     * @return 图统计信息
     */
    GraphStatsDTO getGraphStatistics();

    /**
     * 社区检测（简化版：基于连通分量）
     *
     * @param minCommunitySize 最小社区关系数
     * @return 社区成员列表
     */
    List<CommunityMemberDTO> detectCommunities(int minCommunitySize);

    /**
     * 按关键词搜索并返回子图
     *
     * @param keyword  关键词
     * @param maxNodes 最大节点数
     * @return 子图数据
     */
    SubgraphResultDTO searchSubgraph(String keyword, int maxNodes);

    /**
     * 批量 MERGE 实体节点
     *
     * @param entities 实体属性列表
     */
    void mergeEntities(List<EntityMergeDTO> entities);

    /**
     * 批量 MERGE 实体关系
     *
     * @param relations 关系列表
     */
    void mergeRelations(List<RelationMergeDTO> relations);

    /**
     * 连接分块与实体（MENTIONS 关系）
     *
     * @param chunkEntityMappings 映射列表
     */
    void connectChunksToEntities(List<ChunkEntityMappingDTO> chunkEntityMappings);

    /**
     * 创建文档节点（含分类信息）
     */
    void createDocumentNode(DocumentPropsDTO docProps);

    /**
     * 创建分块节点
     */
    void createChunkNode(ChunkPropsDTO chunkProps);

    /**
     * 创建文档到分块的 HAS_CHUNK 关系
     */
    void createHasChunkRelation(Long docId, String chunkId, int chunkIndex);
}
