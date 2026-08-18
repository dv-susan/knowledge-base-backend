package com.knowledge.base.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j配置类（保留，ChatLanguageModel由ModelProvider按需创建）
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class LangChain4jConfig {
    // ChatLanguageModel bean 创建已移至 ModelProvider，
    // 因为 OpenAiChatModel 构建时强制要求 API Key 不能为空。
    // ModelProvider 会在收到请求时按需构建，从而允许在未配置 API Key 时正常启动。
}
