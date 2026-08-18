package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.dto.ReindexRequestDTO;
import com.knowledge.base.ai.rag.service.ReindexService;
import com.knowledge.base.ai.vo.ReindexProgressVO;
import com.knowledge.base.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * RAG重建索引控制器
 *
 * <p>提供文档索引管理接口：单文档索引、批量索引、全量重建、进度查询。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/rag/reindex")
@RequiredArgsConstructor
@Tag(name = "RAG索引管理", description = "知识库RAG索引管理接口")
public class RagReindexController {

    private final ReindexService reindexService;

    /**
     * 重建单个文档
     */
    @PostMapping("/{docId}")
    @Operation(summary = "重建单个文档索引", description = "重新分块、嵌入并索引指定文档")
    public Result<String> reindexByDoc(@PathVariable Long docId) {
        log.info("重建文档索引：documentId={}", docId);
        String taskId = reindexService.reindexByDocId(docId);
        return Result.success(taskId);
    }

    /**
     * 批量重建文档
     */
    @PostMapping("/batch")
    @Operation(summary = "批量重建文档索引", description = "重新分块、嵌入并索引指定的多个文档")
    public Result<String> reindexBatch(@Valid @RequestBody ReindexRequestDTO dto) {
        log.info("批量重建文档索引：count={}", dto.getDocumentIds().size());
        String taskId = reindexService.reindexBatch(dto.getDocumentIds());
        return Result.success(taskId);
    }

    /**
     * 重建所有文档
     */
    @PostMapping("/all")
    @Operation(summary = "重建全部文档索引", description = "重新分块、嵌入并索引所有已发布文档")
    public Result<String> reindexAll() {
        log.info("全量重建文档索引");
        String taskId = reindexService.reindexAll();
        return Result.success(taskId);
    }

    /**
     * 删除单个文档的向量索引
     */
    @DeleteMapping("/{docId}")
    @Operation(summary = "删除单个文档向量索引", description = "从ES向量库中删除指定文档的所有chunk")
    public Result<String> deleteByDoc(@PathVariable Long docId) {
        log.info("删除文档向量索引：documentId={}", docId);
        String taskId = reindexService.deleteByDocId(docId);
        return Result.success(taskId);
    }

    /**
     * 查询重建进度
     */
    @GetMapping("/progress/{taskId}")
    @Operation(summary = "查询重建进度", description = "根据任务ID查询索引重建进度")
    public Result<ReindexProgressVO> getProgress(@PathVariable String taskId) {
        ReindexProgressVO progress = reindexService.getProgress(taskId);
        return Result.success(progress);
    }
}
