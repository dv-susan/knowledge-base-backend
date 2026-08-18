package com.knowledge.base.ai.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 嵌入模型配置
 *
 * <p>创建OpenAiEmbeddingModel Bean，使用通义千问 text-embedding-v3 模型。
 * 复用已有的 Qwen API Key 和 Base URL 配置，
 * 遵循与 ModelProvider 相同的 OpenAi 兼容接口模式。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class EmbeddingConfig {

    @Value("${qwen.api-key}")
    private String qwenApiKey;

    @Value("${qwen.base-url}")
    private String qwenBaseUrl;

    @Value("${rag.embedding.model:text-embedding-v3}")
    private String embeddingModel;

    /**
     * 创建Qwen EmbeddingModel Bean
     *
     * <p>仅在 rag.enabled=true 且配置了 API Key 时创建。
     * 使用 @ConditionalOnExpression 确保 API Key 非空时才注册 Bean，
     * 避免返回 null 导致依赖注入失败。</p>
     */
    @Bean
    @ConditionalOnProperty(name = "rag.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnExpression("'${qwen.api-key:}' != ''")
    public EmbeddingModel embeddingModel() {
        log.info("✅ 创建 EmbeddingModel：model={}, provider=qwen", embeddingModel);
        return OpenAiEmbeddingModel.builder()
                .apiKey(qwenApiKey)
                .baseUrl(qwenBaseUrl)
                .modelName(embeddingModel)
                .build();
    }

    @PostConstruct
    public void init() {
        if (qwenApiKey != null && !qwenApiKey.isEmpty()) {
            log.info("✅ RAG嵌入模型已就绪：model={}, dimension=1024", embeddingModel);
        } else {
            log.warn("⚠️ RAG嵌入模型不可用：QWEN_API_KEY 未配置");
        }
    }
}
