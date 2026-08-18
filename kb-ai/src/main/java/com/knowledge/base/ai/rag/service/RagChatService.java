package com.knowledge.base.ai.rag.service;

import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.vo.ChatResponseVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * RAG对话服务接口
 *
 * <p>支持同步和SSE流式两种模式，
 * 均内置检索→Prompt构建→LLM生成流程和优雅降级机制。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface RagChatService {

    /**
     * 基于知识库的对话（同步）
     *
     * @param requestDTO 对话请求
     * @param userId     用户ID
     * @return 对话响应（含引用来源）
     */
    ChatResponseVO chatWithContext(ChatRequestDTO requestDTO, Long userId);

    /**
     * 基于知识库的对话（SSE流式）
     *
     * @param requestDTO 对话请求
     * @param userId     用户ID
     * @return SSE发射器
     */
    SseEmitter chatWithContextStream(ChatRequestDTO requestDTO, Long userId);
}
