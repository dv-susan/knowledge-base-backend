package com.knowledge.base.ai.rag.kag.chat;

import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.vo.ChatResponseVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * KAG 增强对话服务接口
 *
 * <p>融合 RAG（文本检索）和 KAG（知识图谱推理）进行增强对话。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface KAGChatService {

    /**
     * KAG 增强的同步对话
     *
     * @param requestDTO 对话请求（需设置 enableKAG=true）
     * @param userId     当前用户ID
     * @return 含图谱上下文的对话响应
     */
    ChatResponseVO chatWithKnowledgeGraph(ChatRequestDTO requestDTO, Long userId);

    /**
     * KAG 增强的流式对话（SSE）
     *
     * @param requestDTO 对话请求
     * @param userId     当前用户ID
     * @return SSE 事件流
     */
    SseEmitter chatWithKnowledgeGraphStream(ChatRequestDTO requestDTO, Long userId);
}
