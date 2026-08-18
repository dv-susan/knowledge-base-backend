package com.knowledge.base.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * KAG自动配置
 *
 * <p>当 kag.enabled=true 时自动扫描并加载KAG组件。
 * 当 kag.enabled=false 时，KAG Beans不会被创建，问答功能降级为纯RAG模式。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "kag.enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.knowledge.base.ai.rag.kag")
public class KAGAutoConfiguration {

    public KAGAutoConfiguration() {
        log.info("KAG knowledge graph enhancement enabled, loading KAG components...");
    }
}
