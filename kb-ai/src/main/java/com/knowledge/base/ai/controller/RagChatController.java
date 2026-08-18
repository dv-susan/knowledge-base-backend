package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.rag.service.RagChatService;
import com.knowledge.base.ai.vo.ChatResponseVO;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * RAG对话控制器
 *
 * <p>提供基于知识库检索增强的AI对话接口（同步 + SSE流式）。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/rag/chat")
@RequiredArgsConstructor
@Tag(name = "RAG对话", description = "基于知识库的AI对话接口")
public class RagChatController {

    private final RagChatService ragChatService;

    /**
     * RAG对话（同步）
     *
     * @param requestDTO 对话请求
     * @param request    HTTP请求
     * @return 对话响应（含引用来源）
     */
    @PostMapping
    @Operation(summary = "RAG对话", description = "基于知识库检索增强的AI对话")
    public Result<ChatResponseVO> chat(@Valid @RequestBody ChatRequestDTO requestDTO,
                                        HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        log.info("RAG对话请求：userId={}, contentLength={}", userId, requestDTO.getContent().length());
        ChatResponseVO response = ragChatService.chatWithContext(requestDTO, userId);
        return Result.success(response);
    }

    /**
     * RAG对话（SSE流式）
     *
     * @param requestDTO 对话请求
     * @param request    HTTP请求
     * @return SSE事件流
     */
    @PostMapping("/stream")
    @Operation(summary = "RAG流式对话", description = "基于知识库检索增强的AI流式对话")
    public SseEmitter chatStream(@Valid @RequestBody ChatRequestDTO requestDTO,
                                  HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        log.info("RAG流式对话请求：userId={}, contentLength={}", userId, requestDTO.getContent().length());
        return ragChatService.chatWithContextStream(requestDTO, userId);
    }

}
