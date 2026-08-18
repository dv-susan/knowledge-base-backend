package com.knowledge.base.ai.controller;

import com.knowledge.base.ai.config.ModelProvider;
import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.service.AiChatService;
import com.knowledge.base.ai.service.AiConversationService;
import com.knowledge.base.ai.vo.ChatResponseVO;
import com.knowledge.base.ai.vo.ModelVO;
import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * AI对话控制器
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "AI对话", description = "AI对话相关接口")
public class AiChatController {

    private final AiChatService aiChatService;
    private final AiConversationService conversationService;
    private final ModelProvider modelProvider;

    /**
     * 获取可用AI模型列表
     *
     * @return 模型列表
     */
    @GetMapping("/models")
    @Operation(summary = "获取AI模型列表", description = "获取当前可用的AI大模型列表")
    public Result<List<ModelVO>> getModels() {
        List<ModelVO> models = modelProvider.getAvailableModels();
        return Result.success(models);
    }

    /**
     * AI对话
     *
     * @param requestDTO 对话请求
     * @param request    HTTP请求
     * @return 对话响应
     */
    @PostMapping
    @Operation(summary = "AI对话", description = "与AI进行对话")
    public Result<ChatResponseVO> chat(@Validated @RequestBody ChatRequestDTO requestDTO,
                                        HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        ChatResponseVO response = aiChatService.chat(requestDTO, userId);
        return Result.success(response);
    }

    /**
     * AI流式对话
     *
     * @param requestDTO 对话请求
     * @param request    HTTP请求
     * @return SSE事件流
     */
    @PostMapping("/stream")
    @Operation(summary = "AI流式对话", description = "与AI进行流式对话")
    public SseEmitter chatStream(@Validated @RequestBody ChatRequestDTO requestDTO,
                                  HttpServletRequest request) {
        Long userId = UserContextUtil.getUserIdFromHeader(request);
        return aiChatService.chatStream(requestDTO, userId);
    }

}
