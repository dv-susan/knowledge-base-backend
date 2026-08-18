package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.rag.kag.chat.KAGChatService;
import com.knowledge.base.ai.rag.kag.retrieval.KAGRetrievalService;
import com.knowledge.base.ai.rag.kag.retrieval.GraphContext;
import com.knowledge.base.ai.vo.ChatResponseVO;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * KAG 知识图谱增强对话控制器
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/kag")
@Tag(name = "KAG知识图谱对话", description = "基于Neo4j知识图谱的增强对话和检索接口")
public class KAGChatController {

    @Resource
    private KAGChatService kagChatService;

    @Resource
    private KAGRetrievalService kagRetrievalService;

    @PostMapping("/chat")
    @Operation(summary = "KAG增强对话", description = "融合RAG文本检索和KAG知识图谱推理的增强对话")
    public Result<ChatResponseVO> chat(@RequestBody @Valid ChatRequestDTO requestDTO,
                                        HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        ChatResponseVO response = kagChatService.chatWithKnowledgeGraph(requestDTO, userId);
        return Result.success("KAG对话完成", response);
    }

    @PostMapping("/chat/stream")
    @Operation(summary = "KAG增强流式对话", description = "基于知识图谱的增强流式对话（SSE）")
    public SseEmitter chatStream(@RequestBody @Valid ChatRequestDTO requestDTO,
                                  HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        return kagChatService.chatWithKnowledgeGraphStream(requestDTO, userId);
    }

    @PostMapping("/search")
    @Operation(summary = "KAG图谱检索", description = "从知识图谱中检索结构化知识和推理路径")
    public Result<GraphContext> search(
            @Parameter(description = "查询文本", required = true) @RequestParam String query,
            @Parameter(description = "最大实体数") @RequestParam(defaultValue = "10") int maxEntities,
            @Parameter(description = "最大跳数") @RequestParam(defaultValue = "2") int maxHops,
            @Parameter(description = "最大文本块数") @RequestParam(defaultValue = "15") int maxChunks) {

        GraphContext context = kagRetrievalService.retrieveGraphContext(
                query, maxEntities, maxHops, maxChunks);
        return Result.success(context);
    }
}
