package com.knowledge.base.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * RAG自动配置
 *
 * <p>当 rag.enabled=true 时自动扫描并加载RAG组件。
 * 当 rag.enabled=false 时，RAG Beans不会被创建，聊天功能降级为纯LLM模式。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "rag.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.knowledge.base.ai.rag")
public class RagAutoConfiguration {

    public RagAutoConfiguration() {
        log.info("✅ RAG功能已启用，正在加载RAG组件...");
    }
}
