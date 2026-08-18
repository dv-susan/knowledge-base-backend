package com.knowledge.base.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * AI建议配置属性
 *
 * <p>从 application.yml 中读取 ai.suggestions 配置。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai.suggestions")
public class AiSuggestionProperties {

    /**
     * 快捷问题列表
     */
    private List<String> items = new ArrayList<>();
}
