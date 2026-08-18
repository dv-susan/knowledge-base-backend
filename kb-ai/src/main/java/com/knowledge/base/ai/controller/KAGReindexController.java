package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.rag.kag.graph.GraphBuildService;
import com.knowledge.base.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * KAG 图谱构建管理控制器
 *
 * <p>负责接收前端请求，将图谱构建/删除任务委托给 {@link GraphBuildService} 发布到 RabbitMQ。
 * 控制器本身不包含任何业务逻辑，仅做参数提取和服务层委托。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/kag/build")
@Tag(name = "KAG图谱管理", description = "知识图谱构建和索引管理接口")
public class KAGReindexController {

    @Resource
    private GraphBuildService graphBuildService;

    /**
     * 构建单个文档的知识图谱
     *
     * <p>发布异步任务到 RabbitMQ，若 MQ 不可用则同步执行。</p>
     *
     * @param docId 文档ID
     * @return 任务ID
     */
    @PostMapping("/{docId}")
    @Operation(summary = "构建单个文档图谱")
    public Result<String> buildByDoc(@PathVariable Long docId) {
        String taskId = graphBuildService.publishBuildTask(docId);
        return Result.success("图谱构建任务已提交", taskId);
    }

    /**
     * 批量构建指定文档的知识图谱
     *
     * <p>发布异步批处理任务到 RabbitMQ，若 MQ 不可用则同步执行。</p>
     *
     * @param docIds 文档ID列表
     * @return 任务ID
     */
    @PostMapping("/batch")
    @Operation(summary = "批量构建文档图谱")
    public Result<String> buildBatch(@RequestBody List<Long> docIds) {
        String taskId = graphBuildService.publishBuildBatchTask(docIds);
        return Result.success("批量图谱构建任务已提交", taskId);
    }

    /**
     * 全量构建所有已发布文档的知识图谱
     *
     * <p>发布异步全量构建任务到 RabbitMQ，若 MQ 不可用则同步执行。</p>
     *
     * @return 任务ID
     */
    @PostMapping("/all")
    @Operation(summary = "全量构建所有已发布文档图谱")
    public Result<String> buildAll() {
        String taskId = graphBuildService.publishBuildAllTask();
        return Result.success("全量图谱构建任务已提交", taskId);
    }

    /**
     * 删除指定文档的知识图谱节点和关系
     *
     * <p>发布异步删除任务到 RabbitMQ，若 MQ 不可用则同步执行。</p>
     *
     * @param docId 文档ID
     * @return 任务ID
     */
    @DeleteMapping("/{docId}")
    @Operation(summary = "删除文档图谱")
    public Result<String> deleteByDoc(@PathVariable Long docId) {
        String taskId = graphBuildService.publishDeleteTask(docId);
        return Result.success("图谱删除任务已提交", taskId);
    }
}
