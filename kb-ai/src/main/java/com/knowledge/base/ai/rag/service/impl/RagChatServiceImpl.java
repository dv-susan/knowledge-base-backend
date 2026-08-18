package com.knowledge.base.ai.rag.service.impl;

import com.knowledge.base.ai.config.ModelProvider;
import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.entity.Conversation;
import com.knowledge.base.ai.entity.Message;
import com.knowledge.base.ai.mapper.MessageMapper;
import com.knowledge.base.ai.config.RagProperties;
import com.knowledge.base.ai.rag.service.RagChatService;
import com.knowledge.base.ai.rag.service.RagRetrievalService;
import com.knowledge.base.ai.vo.CitationVO;
import com.knowledge.base.ai.vo.RagSearchResultVO;
import com.knowledge.base.ai.service.AiConversationService;
import com.knowledge.base.ai.vo.ChatResponseVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RAG对话服务实现
 *
 * <p>实现知识库问答的核心流程：
 * <ol>
 *   <li>从ES检索相关文档块</li>
 *   <li>构建RAG Prompt（参考资料 + 用户问题）</li>
 *   <li>调用LLM生成回答</li>
 *   <li>解析引用标注 [1] [2]</li>
 *   <li>持久化消息到MySQL</li>
 * </ol>
 * 内置优雅降级：检索失败或RAG未启用时回退到普通LLM对话。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagChatServiceImpl implements RagChatService {

    private final RagRetrievalService ragRetrievalService;
    private final ModelProvider modelProvider;
    private final RagProperties ragProperties;
    private final MessageMapper messageMapper;
    private final AiConversationService conversationService;

    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    /** {@inheritDoc} */
    @Override
    public ChatResponseVO chatWithContext(ChatRequestDTO requestDTO, Long userId) {
        // 1. 创建或验证对话
        Long conversationId = prepareConversation(requestDTO, userId);

        // 2. 检索相关知识
        List<RagSearchResultVO> context = safeRetrieve(requestDTO.getContent());

        // 3. 构建RAG Prompt
        String prompt = buildRagPrompt(requestDTO.getContent(), context);
        String modelName = requestDTO.getModel() != null ? requestDTO.getModel() : modelProvider.getDefaultModelName();

        // 4. 构建对话上下文（历史消息），实现记忆功能
        ChatLanguageModel chatModel = modelProvider.getModel(modelName);
        List<ChatMessage> chatMessages = buildChatHistory(conversationId);

        // 5. 持久化用户消息
        Message userMsgEntity = Message.builder()
                .conversationId(conversationId)
                .role("user")
                .content(requestDTO.getContent())
                .tokens(estimateTokens(requestDTO.getContent()))
                .build();
        messageMapper.insert(userMsgEntity);

        // 6. 将 RAG Prompt 加入上下文并调用 LLM
        chatMessages.add(UserMessage.from(prompt));
        Response<AiMessage> response = chatModel.generate(chatMessages);
        String responseContent = response.content().text();

        // 7. 构建引用列表
        List<CitationVO> citations = buildCitations(context, responseContent);

        Message aiMsgEntity = Message.builder()
                .conversationId(conversationId)
                .role("assistant")
                .content(responseContent)
                .tokens(estimateTokens(responseContent))
                .build();
        messageMapper.insert(aiMsgEntity);

        conversationService.updateTokens(conversationId,
                userMsgEntity.getTokens() + aiMsgEntity.getTokens());

        Conversation conv = conversationService.getById(conversationId);

        return ChatResponseVO.builder()
                .conversationId(conversationId)
                .messageId(aiMsgEntity.getId())
                .content(responseContent)
                .tokens(userMsgEntity.getTokens() + aiMsgEntity.getTokens())
                .title(conv != null ? conv.getTitle() : "新对话")
                .citations(citations)
                .fromKnowledgeBase(!context.isEmpty())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public SseEmitter chatWithContextStream(ChatRequestDTO requestDTO, Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        try {
            Long conversationId = prepareConversation(requestDTO, userId);
            List<RagSearchResultVO> context = safeRetrieve(requestDTO.getContent());
            String prompt = buildRagPrompt(requestDTO.getContent(), context);
            String modelName = requestDTO.getModel() != null ? requestDTO.getModel() : modelProvider.getDefaultModelName();

            CompletableFuture.runAsync(() -> {
                try {
                    // 使用流式模型实现 token-by-token SSE 输出
                    StreamingChatLanguageModel streamingModel = modelProvider.getStreamingModel(modelName);

                    // 构建对话上下文（历史消息），实现记忆功能
                    List<ChatMessage> chatMessages = buildChatHistory(conversationId);

                    // 先保存用户消息（在流式开始前，且与检索到的context/output无关）
                    Message userMsgEntity = Message.builder()
                            .conversationId(conversationId)
                            .role("user")
                            .content(requestDTO.getContent())
                            .tokens(estimateTokens(requestDTO.getContent()))
                            .build();
                    messageMapper.insert(userMsgEntity);

                    // 将 RAG Prompt 加入上下文
                    chatMessages.add(UserMessage.from(prompt));

                    // 使用 StringBuilder 累积完整响应用于最终持久化
                    StringBuilder fullResponseBuilder = new StringBuilder();

                    streamingModel.generate(chatMessages, new StreamingResponseHandler<AiMessage>() {
                        @Override
                        public void onNext(String token) {
                            fullResponseBuilder.append(token);
                            try {
                                emitter.send(SseEmitter.event()
                                        .name("message")
                                        .data(token));
                            } catch (IOException e) {
                                log.warn("发送流式token失败（客户端可能已断开）: {}", e.getMessage());
                            }
                        }

                        @Override
                        public void onComplete(Response<AiMessage> response) {
                            try {
                                String fullResponse = fullResponseBuilder.toString();

                                List<CitationVO> citations = buildCitations(context, fullResponse);

                                Message aiMsgEntity = Message.builder()
                                        .conversationId(conversationId)
                                        .role("assistant")
                                        .content(fullResponse)
                                        .tokens(estimateTokens(fullResponse))
                                        .build();
                                messageMapper.insert(aiMsgEntity);

                                AtomicInteger totalTokens = new AtomicInteger(userMsgEntity.getTokens());
                                totalTokens.addAndGet(aiMsgEntity.getTokens());
                                conversationService.updateTokens(conversationId, totalTokens.get());

                                emitter.send(SseEmitter.event()
                                        .name("done")
                                        .data(ChatResponseVO.builder()
                                                .conversationId(conversationId)
                                                .messageId(aiMsgEntity.getId())
                                                .content(fullResponse)
                                                .tokens(totalTokens.get())
                                                .citations(citations)
                                                .fromKnowledgeBase(!context.isEmpty())
                                                .build()));
                                emitter.complete();
                            } catch (IOException e) {
                                log.warn("发送完成事件失败（客户端可能已断开）: {}", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(Throwable error) {
                            log.error("RAG流式对话失败：{}", error.getMessage(), error);
                            try {
                                emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data(error.getMessage() != null ? error.getMessage() : "AI服务调用失败"));
                                emitter.complete();
                            } catch (IOException ioException) {
                                log.error("发送错误事件失败：{}", ioException.getMessage());
                            }
                        }
                    });

                } catch (Exception e) {
                    log.error("RAG流式对话失败：{}", e.getMessage(), e);
                    try {
                        emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                        emitter.complete();
                    } catch (IOException ioException) {
                        log.error("发送错误事件失败：{}", ioException.getMessage());
                    }
                }
            }, asyncTaskExecutor);

        } catch (Exception e) {
            log.error("创建SSE发射器失败：{}", e.getMessage(), e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 安全检索：捕获异常返回空列表，不中断主流程
     */
    private List<RagSearchResultVO> safeRetrieve(String query) {
        try {
            return ragRetrievalService.retrieve(query,
                    ragProperties.getRetrieval().getDefaultTopK(),
                    ragProperties.getRerank().isEnabled());
        } catch (Exception e) {
            log.warn("RAG检索失败，降级为纯LLM回答：{}", e.getMessage());
            return List.of();
        }
    }

    /**
     * 构建RAG Prompt
     */
    private String buildRagPrompt(String query, List<RagSearchResultVO> context) {
        if (context.isEmpty()) {
            return query;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("你是一个企业知识库助手。请仅根据以下参考资料回答用户的问题。\n");
        sb.append("如果参考资料中没有相关信息，请说明【根据现有知识库暂时无法回答此问题】，绝对不要编造内容。\n");
        sb.append("回答时，请在引用资料处的末尾标注引用编号，如 [1]、[2]。\n");
        sb.append("回答应当专业、准确、简洁。\n\n");

        sb.append("=== 参考资料 ===\n");
        for (int i = 0; i < context.size(); i++) {
            RagSearchResultVO chunk = context.get(i);
            sb.append(String.format("[%d] 来源文档：%s", i + 1, chunk.getDocumentTitle()));
            if (chunk.getHeading() != null && !chunk.getHeading().isEmpty()) {
                sb.append(" | 章节：").append(chunk.getHeading());
            }
            sb.append("\n").append(chunk.getContent()).append("\n\n");
        }

        sb.append("=== 用户问题 ===\n");
        sb.append(query);
        return sb.toString();
    }

    /**
     * 构建引用列表
     */
    private List<CitationVO> buildCitations(List<RagSearchResultVO> context, String response) {
        if (context.isEmpty()) {
            return List.of();
        }
        List<CitationVO> citations = new ArrayList<>();
        for (int i = 0; i < context.size(); i++) {
            RagSearchResultVO chunk = context.get(i);
            String excerpt = chunk.getContent();
            if (excerpt != null && excerpt.length() > 100) {
                excerpt = excerpt.substring(0, 100) + "...";
            }
            citations.add(CitationVO.builder()
                    .index(i + 1)
                    .documentId(chunk.getDocumentId())
                    .documentTitle(chunk.getDocumentTitle())
                    .excerpt(excerpt)
                    .relevanceScore(chunk.getScore())
                    .build());
        }
        return citations;
    }

    /**
     * 准备对话（创建新对话或验证已有对话）
     */
    private Long prepareConversation(ChatRequestDTO requestDTO, Long userId) {
        ChatRequestDTO createDTO = ChatRequestDTO.builder()
                .content(requestDTO.getContent())
                .model(requestDTO.getModel())
                .systemPrompt(requestDTO.getSystemPrompt())
                .build();

        if (requestDTO.getConversationId() == null) {
            return conversationService.createConversation(createDTO, userId);
        }
        Conversation existing = conversationService.getById(requestDTO.getConversationId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            return conversationService.createConversation(createDTO, userId);
        }
        return existing.getId();
    }

    /**
     * 从 MySQL 中加载最近 N 条对话记录，构建 LangChain4j 消息上下文
     * <p>实现对话记忆功能，让 AI 能理解本轮对话的历史（最多保留最近 20 条消息）</p>
     */
    private List<ChatMessage> buildChatHistory(Long conversationId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getConversationId, conversationId)
                .orderByDesc(Message::getCreatedAt)
                .last("LIMIT 20");
        List<Message> historyMessages = messageMapper.selectList(queryWrapper);
        // 按时间正序排列
        Collections.reverse(historyMessages);

        List<ChatMessage> chatMessages = new ArrayList<>();
        for (Message msg : historyMessages) {
            if ("user".equals(msg.getRole())) {
                chatMessages.add(UserMessage.from(msg.getContent()));
            } else if ("assistant".equals(msg.getRole())) {
                chatMessages.add(AiMessage.from(msg.getContent()));
            }
        }
        return chatMessages;
    }

    private int estimateTokens(String text) {
        if (text == null || text.isEmpty()) return 0;
        int chineseChars = text.replaceAll("[^\\u4e00-\\u9fa5]", "").length();
        int otherChars = text.length() - chineseChars;
        return chineseChars + (otherChars / 4);
    }
}
