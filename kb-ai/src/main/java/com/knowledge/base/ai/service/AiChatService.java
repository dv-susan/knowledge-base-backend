package com.knowledge.base.ai.service;

import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.vo.ChatResponseVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI对话服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface AiChatService {

    /**
     * AI对话（非流式）
     *
     * @param requestDTO 对话请求
     * @param userId     用户ID
     * @return 对话响应
     */
    ChatResponseVO chat(ChatRequestDTO requestDTO, Long userId);

    /**
     * AI对话（流式）
     *
     * @param requestDTO 对话请求
     * @param userId     用户ID
     * @return SSE事件发射器
     */
    SseEmitter chatStream(ChatRequestDTO requestDTO, Long userId);

    /**
     * 获取对话历史
     *
     * @param conversationId 对话ID
     * @param userId         用户ID
     * @return 对话历史
     */
    String getConversationHistory(Long conversationId, Long userId);

    /**
     * 生成对话标题
     *
     * @param firstMessage 首条消息内容
     * @return 对话标题
     */
    String generateTitle(String firstMessage);
}
