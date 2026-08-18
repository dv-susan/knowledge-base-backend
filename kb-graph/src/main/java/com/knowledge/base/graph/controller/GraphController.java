package com.knowledge.base.graph.controller;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.graph.service.GraphService;
import com.knowledge.base.graph.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识图谱Controller
 *
 * <p>按照阿里巴巴Java开发规范设计，提供知识图谱相关接口</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@Tag(name = "知识图谱", description = "知识图谱管理接口")
public class GraphController {

    @Resource
    private GraphService graphService;

    /**
     * 获取节点列表
     *
     * @param type 节点类型
     * @return 节点列表
     */
    @GetMapping("/nodes")
    @Operation(summary = "获取节点列表", description = "获取知识图谱节点列表")
    public Result<List<GraphNodeVO>> getNodes(
        @Parameter(description = "节点类型") @RequestParam(required = false) String type) {
        log.info("获取节点列表请求：type={}", type);

        List<GraphNodeVO> nodes = graphService.getNodes(type);
        return Result.success(nodes);
    }

    /**
     * 获取边列表
     *
     * @param sourceType 源节点类型
     * @param targetType 目标节点类型
     * @return 边列表
     */
    @GetMapping("/edges")
    @Operation(summary = "获取边列表", description = "获取知识图谱边列表")
    public Result<List<GraphEdgeVO>> getEdges(
        @Parameter(description = "源节点类型") @RequestParam(required = false) String sourceType,
        @Parameter(description = "目标节点类型") @RequestParam(required = false) String targetType) {
        log.info("获取边列表请求：sourceType={}, targetType={}", sourceType, targetType);

        List<GraphEdgeVO> edges = graphService.getEdges(sourceType, targetType);
        return Result.success(edges);
    }

    /**
     * 获取节点关系
     *
     * @param nodeId 节点ID
     * @return 关系列表
     */
    @GetMapping("/node/{nodeId}/relations")
    @Operation(summary = "获取节点关系", description = "获取节点的所有关系")
    public Result<List<GraphRelationVO>> getNodeRelations(
        @Parameter(description = "节点ID", required = true)
        @PathVariable String nodeId) {
        log.info("获取节点关系请求：nodeId={}", nodeId);

        List<GraphRelationVO> relations = graphService.getNodeRelations(nodeId);
        return Result.success(relations);
    }

    /**
     * 图谱搜索
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    @GetMapping("/search")
    @Operation(summary = "图谱搜索", description = "在知识图谱中搜索节点")
    public Result<List<GraphNodeVO>> searchGraph(
        @Parameter(description = "搜索关键词", required = true)
        @RequestParam String keyword) {
        log.info("图谱搜索请求：keyword={}", keyword);

        List<GraphNodeVO> nodes = graphService.searchGraph(keyword);
        return Result.success(nodes);
    }

    /**
     * 路径分析
     *
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @param maxDepth 最大深度
     * @return 路径列表
     */
    @GetMapping("/path")
    @Operation(summary = "路径分析", description = "分析两个节点之间的路径")
    public Result<List<GraphPathVO>> analyzePath(
        @Parameter(description = "源节点ID", required = true)
        @RequestParam String sourceId,
        @Parameter(description = "目标节点ID", required = true)
        @RequestParam String targetId,
        @Parameter(description = "最大深度")
        @RequestParam(defaultValue = "5") Integer maxDepth) {
        log.info("路径分析请求：sourceId={}, targetId={}, maxDepth={}", sourceId, targetId, maxDepth);

        List<GraphPathVO> paths = graphService.analyzePath(sourceId, targetId, maxDepth);
        return Result.success(paths);
    }

    /**
     * 社区检测
     *
     * @param algorithm 算法类型
     * @return 社区列表
     */
    @GetMapping("/community")
    @Operation(summary = "社区检测", description = "检测图谱中的社区结构")
    public Result<List<GraphCommunityVO>> detectCommunity(
        @Parameter(description = "算法类型")
        @RequestParam(defaultValue = "label_propagation") String algorithm) {
        log.info("社区检测请求：algorithm={}", algorithm);

        List<GraphCommunityVO> communities = graphService.detectCommunity(algorithm);
        return Result.success(communities);
    }

    /**
     * 获取完整图谱数据
     *
     * @param type 节点类型
     * @return 图谱数据
     */
    @GetMapping("/data")
    @Operation(summary = "获取完整图谱数据", description = "获取完整的知识图谱数据")
    public Result<GraphDataVO> getGraphData(
        @Parameter(description = "节点类型") @RequestParam(required = false) String type) {
        log.info("获取完整图谱数据请求：type={}", type);

        GraphDataVO graphData = graphService.getGraphData(type);
        return Result.success(graphData);
    }

    /**
     * 删除文档的图谱数据
     *
     * <p>删除文档对应的 KnowledgeDocument 节点及其 DocumentChunk 子节点，保留跨文档共享的 KnowledgeEntity。</p>
     *
     * @param docId 文档ID
     * @return 操作结果
     */
    @DeleteMapping("/document/{docId}")
    @Operation(summary = "删除文档图谱", description = "删除指定文档的知识图谱节点及关系")
    public Result<String> deleteDocumentGraph(
            @Parameter(description = "文档ID", required = true) @PathVariable Long docId) {
        log.info("删除文档图谱请求：docId={}", docId);
        graphService.deleteByDocId(docId);
        return Result.success("文档图谱数据已删除");
    }

    /**
     * 清理脏图谱节点
     *
     * <p>接收有效文档ID白名单，删除 Neo4j 中 docId 不在白名单内的 KnowledgeDocument 节点及关联子节点。</p>
     *
     * @param body 包含 validDocIds 的请求体
     * @return 清理结果
     */
    @PostMapping("/document/cleanup")
    @Operation(summary = "清理脏图谱节点", description = "删除 MySQL 中已不存在的文档对应的图谱节点")
    public Result<String> cleanupGhostNodes(@RequestBody Map<String, List<Long>> body) {
        List<Long> validDocIds = body.get("validDocIds");
        log.info("清理脏图谱节点请求：validDocIds.size={}", validDocIds != null ? validDocIds.size() : 0);
        int deleted = graphService.cleanupGhostNodes(validDocIds);
        return Result.success("已清理 " + deleted + " 个脏图谱节点");
    }

    /**
     * 清除图谱 Redis 缓存
     *
     * <p>由 kb-ai 在知识图谱重建完成后调用，使所有图谱查询缓存失效，
     * 确保前端下次查询时从 Neo4j 获取最新数据。</p>
     *
     * @return 操作结果
     */
    @PostMapping("/cache/evict")
    @Operation(summary = "清除图谱缓存", description = "清除所有知识图谱 Redis 缓存，强制从 Neo4j 重新加载")
    public Result<String> evictCache() {
        log.info("清除图谱缓存请求");
        graphService.evictAllCaches();
        return Result.success("图谱缓存已清除");
    }
}
