package com.knowledge.base.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.ai.config.ModelProvider;
import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.entity.Conversation;
import com.knowledge.base.ai.entity.Message;
import com.knowledge.base.ai.mapper.ConversationMapper;
import com.knowledge.base.ai.mapper.MessageMapper;
import com.knowledge.base.ai.rag.service.RagChatService;
import com.knowledge.base.ai.service.AiChatService;
import com.knowledge.base.ai.service.AiConversationService;
import com.knowledge.base.ai.vo.ChatResponseVO;
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
import org.springframework.beans.factory.annotation.Autowired;
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
 * AI对话服务实现类
 *
 * <p>实现AI对话相关业务逻辑，支持RAG路由和优雅降级</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
        implements AiChatService {

    private final ModelProvider modelProvider;
    private final MessageMapper messageMapper;
    private final AiConversationService conversationService;

    @Autowired(required = false)
    private RagChatService ragChatService;

    @Resource
    private ThreadPoolTaskExecutor asyncTaskExecutor;

    /** {@inheritDoc} */
    @Override
    public ChatResponseVO chat(ChatRequestDTO requestDTO, Long userId) {
        // RAG路由：如果启用了知识库检索增强且RAG服务可用
        if (requestDTO.isEnableRag() && ragChatService != null) {
            try {
                return ragChatService.chatWithContext(requestDTO, userId);
            } catch (Exception e) {
                log.warn("RAG对话失败，降级为普通LLM对话：{}", e.getMessage());
                // 优雅降级：继续执行普通对话流程
            }
        }

        final String modelName = requestDTO.getModel() != null ? requestDTO.getModel() : modelProvider.getDefaultModelName();
        try {
            Long conversationId = requestDTO.getConversationId();
            if (conversationId == null) {
                conversationId = conversationService.createConversation(requestDTO, userId);
            } else {
                // 验证对话是否存在且属于该用户，否则创建新对话
                Conversation existingConv = conversationService.getById(conversationId);
                if (existingConv == null || !existingConv.getUserId().equals(userId)) {
                    log.warn("对话不存在或无权访问，创建新对话：conversationId={}, userId={}", conversationId, userId);
                    conversationId = conversationService.createConversation(requestDTO, userId);
                } else {
                    // 如果标题仍是默认的"新对话"，从第一条消息更新标题
                    updateConversationTitle(existingConv, requestDTO.getContent());
                }
            }

            // 根据请求中指定的模型选择对应的ChatLanguageModel
            ChatLanguageModel chatModel = modelProvider.getModel(requestDTO.getModel());

            log.info("发起AI请求：model={}, conversationId={}, contentLength={}",
                    modelName, conversationId, requestDTO.getContent().length());

            // 构建对话上下文（历史消息 + 当前消息），实现记忆功能
            List<ChatMessage> chatMessages = buildChatHistory(conversationId);

            // 保存用户消息
            Message userMsgEntity = Message.builder()
                    .conversationId(conversationId)
                    .role("user")
                    .content(requestDTO.getContent())
                    .tokens(estimateTokens(requestDTO.getContent()))
                    .build();
            messageMapper.insert(userMsgEntity);

            // 将当前用户消息加入上下文
            chatMessages.add(UserMessage.from(requestDTO.getContent()));

            Response<AiMessage> response = chatModel.generate(chatMessages);
            String responseContent = response.content().text();

            Message aiMsgEntity = Message.builder()
                    .conversationId(conversationId)
                    .role("assistant")
                    .content(responseContent)
                    .tokens(estimateTokens(responseContent))
                    .build();
            messageMapper.insert(aiMsgEntity);

            int totalTokens = userMsgEntity.getTokens() + aiMsgEntity.getTokens();
            conversationService.updateTokens(conversationId, totalTokens);

            Conversation conversation = conversationService.getById(conversationId);
            String title = conversation != null ? conversation.getTitle() : "新对话";

            log.info("AI对话完成：model={}, conversationId={}, tokens={}", modelName, conversationId, totalTokens);

            return ChatResponseVO.builder()
                    .conversationId(conversationId)
                    .messageId(aiMsgEntity.getId())
                    .content(responseContent)
                    .tokens(totalTokens)
                    .title(title)
                    .build();

        } catch (Exception e) {
            log.error("AI对话失败: {}", e.getMessage(), e);
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                Throwable cause = e.getCause();
                String causeMsg = (cause != null) ? cause.getMessage() : null;
                if (causeMsg != null && !causeMsg.isEmpty()) {
                    errorMsg = causeMsg;
                } else {
                    errorMsg = "AI服务调用失败（模型：" + modelName + "），请检查API Key配置和网络连接";
                }
            }
            throw new RuntimeException("AI对话失败: " + errorMsg);
        }
    }

    /** {@inheritDoc} */
    @Override
    public SseEmitter chatStream(ChatRequestDTO requestDTO, Long userId) {
        // RAG路由：如果启用了知识库检索增强且RAG服务可用
        if (requestDTO.isEnableRag() && ragChatService != null) {
            try {
                return ragChatService.chatWithContextStream(requestDTO, userId);
            } catch (Exception e) {
                log.warn("RAG流式对话失败，降级为普通LLM流式对话：{}", e.getMessage());
                // 优雅降级：继续执行普通流式对话
            }
        }

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        try {
            final Long conversationIdFinal = requestDTO.getConversationId();
            Long conversationId = conversationIdFinal;
            if (conversationId == null) {
                conversationId = conversationService.createConversation(requestDTO, userId);
            } else {
                // 验证对话是否存在且属于该用户，否则创建新对话
                Conversation existingConv = conversationService.getById(conversationId);
                if (existingConv == null || !existingConv.getUserId().equals(userId)) {
                    log.warn("对话不存在或无权访问，创建新对话：conversationId={}, userId={}", conversationId, userId);
                    conversationId = conversationService.createConversation(requestDTO, userId);
                } else {
                    // 如果标题仍是默认的"新对话"，从第一条消息更新标题
                    updateConversationTitle(existingConv, requestDTO.getContent());
                }
            }
            final Long finalConversationId = conversationId;

            // 在异步执行前确定模型名称
            final String modelName = requestDTO.getModel() != null ? requestDTO.getModel() : modelProvider.getDefaultModelName();

            CompletableFuture.runAsync(() -> {
                try {
                    // 使用流式模型实现 token-by-token SSE 输出
                    StreamingChatLanguageModel streamingModel = modelProvider.getStreamingModel(modelName);

                    log.info("发起AI流式请求：model={}, conversationId={}, contentLength={}",
                            modelName, finalConversationId, requestDTO.getContent().length());

                    // 构建对话上下文（历史消息），实现记忆功能
                    List<ChatMessage> chatMessages = buildChatHistory(finalConversationId);

                    // 先保存用户消息（在流式开始前）
                    Message userMsgEntity = Message.builder()
                            .conversationId(finalConversationId)
                            .role("user")
                            .content(requestDTO.getContent())
                            .tokens(estimateTokens(requestDTO.getContent()))
                            .build();
                    messageMapper.insert(userMsgEntity);

                    // 将当前用户消息加入上下文
                    chatMessages.add(UserMessage.from(requestDTO.getContent()));

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

                                Message aiMsgEntity = Message.builder()
                                        .conversationId(finalConversationId)
                                        .role("assistant")
                                        .content(fullResponse)
                                        .tokens(estimateTokens(fullResponse))
                                        .build();
                                messageMapper.insert(aiMsgEntity);

                                AtomicInteger totalTokens = new AtomicInteger(userMsgEntity.getTokens());
                                totalTokens.addAndGet(aiMsgEntity.getTokens());
                                conversationService.updateTokens(finalConversationId, totalTokens.get());

                                log.info("AI流式对话完成：model={}, conversationId={}", modelName, finalConversationId);

                                emitter.send(SseEmitter.event()
                                        .name("done")
                                        .data(ChatResponseVO.builder()
                                                .conversationId(finalConversationId)
                                                .messageId(aiMsgEntity.getId())
                                                .content(fullResponse)
                                                .tokens(totalTokens.get())
                                                .build()));
                                emitter.complete();
                            } catch (IOException e) {
                                log.warn("发送完成事件失败（客户端可能已断开）: {}", e.getMessage());
                            }
                        }

                        @Override
                        public void onError(Throwable error) {
                            log.error("流式对话失败[model={}]: {}", modelName, error.getMessage(), error);
                            try {
                                emitter.send(SseEmitter.event()
                                        .name("error")
                                        .data(error.getMessage() != null ? error.getMessage() : "AI服务调用失败"));
                                emitter.complete();
                            } catch (IOException ioException) {
                                log.error("发送错误事件失败: {}", ioException.getMessage());
                            }
                        }
                    });

                } catch (Exception e) {
                    // 从OpenAiHttpException中提取HTTP状态码
                    String httpCodeInfo = "";
                    Throwable root = e;
                    while (root != null) {
                        if (root instanceof dev.ai4j.openai4j.OpenAiHttpException oahe) {
                            try {
                                int code = (int) oahe.getClass().getMethod("code").invoke(oahe);
                                httpCodeInfo = " HTTP_" + code;
                            } catch (Exception ignored) {}
                            try {
                                String json = (String) oahe.getClass().getMethod("json").invoke(oahe);
                                if (json != null && !json.isEmpty()) {
                                    httpCodeInfo += " Response: " + json;
                                }
                            } catch (Exception ignored) {}
                            break;
                        }
                        root = root.getCause();
                    }
                    log.error("流式对话失败[model={}]:{}{}", modelName,
                            httpCodeInfo.isEmpty() ? "" : httpCodeInfo,
                            e.getMessage() != null ? " " + e.getMessage() : "", e);

                    String errorMsg = e.getMessage();
                    if (errorMsg == null || errorMsg.isEmpty()) {
                        Throwable cause = e.getCause();
                        String causeMsg = (cause != null) ? cause.getMessage() : null;
                        if (causeMsg != null && !causeMsg.isEmpty()) {
                            errorMsg = causeMsg;
                        } else {
                            String detail = "AI服务调用失败（模型：" + modelName + "）" + httpCodeInfo;
                            if (httpCodeInfo.contains("404")) {
                                detail += " - 请确认API Key有效且Base URL配置正确";
                            } else if (httpCodeInfo.contains("401") || httpCodeInfo.contains("403")) {
                                detail += " - 请确认API Key有效";
                            }
                            errorMsg = detail;
                        }
                    }
                    try {
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data(errorMsg));
                        emitter.complete();
                    } catch (IOException ioException) {
                        log.error("发送错误事件失败: {}", ioException.getMessage());
                    }
                }
            }, asyncTaskExecutor);

        } catch (Exception e) {
            log.error("创建SSE发射器失败: {}", e.getMessage(), e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /** {@inheritDoc} */
    @Override
    public String getConversationHistory(Long conversationId, Long userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getConversationId, conversationId)
                .orderByAsc(Message::getCreatedAt);

        List<Message> messages = messageMapper.selectList(queryWrapper);

        StringBuilder history = new StringBuilder();
        for (Message message : messages) {
            history.append(message.getRole()).append(": ").append(message.getContent()).append("\n");
        }

        return history.toString();
    }

    /** {@inheritDoc} */
    @Override
    public String generateTitle(String firstMessage) {
        try {
            String prompt = "请根据以下对话内容，生成一个简短的标题（不超过10个字）：\n" + firstMessage;
            UserMessage userMessage = UserMessage.from(prompt);
            // 标题生成使用默认模型
            ChatLanguageModel chatModel = modelProvider.getDefaultModel();
            Response<AiMessage> response = chatModel.generate(userMessage);
            return response.content().text().trim();
        } catch (Exception e) {
            log.error("生成对话标题失败: {}", e.getMessage(), e);
            return "新对话";
        }
    }

    /**
     * 如果对话标题仍是默认值（以"新对话"开头），从第一条消息内容更新标题
     */
    private void updateConversationTitle(Conversation conversation, String content) {
        String currentTitle = conversation.getTitle();
        if (currentTitle != null && currentTitle.startsWith("新对话") && content != null && !content.isEmpty()) {
            String newTitle = content.length() > 30 ? content.substring(0, 30) : content;
            conversation.setTitle(newTitle);
            conversation.setUpdatedAt(LocalDateTime.now());
            this.updateById(conversation);
            log.info("更新对话标题：conversationId={}, title={}", conversation.getId(), newTitle);
        }
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
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int chineseChars = text.replaceAll("[^\\u4e00-\\u9fa5]", "").length();
        int otherChars = text.length() - chineseChars;

        return chineseChars + (otherChars / 4);
    }
}
