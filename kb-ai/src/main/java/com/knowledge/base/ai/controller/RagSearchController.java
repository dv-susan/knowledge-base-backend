package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.dto.RagSearchRequestDTO;
import com.knowledge.base.ai.rag.service.RagRetrievalService;
import com.knowledge.base.ai.vo.RagSearchResultVO;
import com.knowledge.base.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RAG检索控制器
 *
 * <p>提供独立的RAG检索接口（不带LLM生成），用于调试和纯搜索场景。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/rag/search")
@RequiredArgsConstructor
@Tag(name = "RAG检索", description = "知识库RAG检索接口")
public class RagSearchController {

    private final RagRetrievalService ragRetrievalService;

    /**
     * RAG检索
     */
    @PostMapping
    @Operation(summary = "RAG检索", description = "基于RAG的知识库检索，不生成回答")
    public Result<List<RagSearchResultVO>> search(@Valid @RequestBody RagSearchRequestDTO requestDTO) {
        log.info("RAG检索请求：query={}, topK={}", requestDTO.getQuery(), requestDTO.getTopK());
        List<RagSearchResultVO> results = ragRetrievalService.retrieve(
                requestDTO.getQuery(), requestDTO.getTopK(), requestDTO.isEnableRerank());
        return Result.success(results);
    }
}
