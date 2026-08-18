package com.knowledge.base.ai.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

/**
 * 内部服务 Feign 调用配置
 *
 * <p>为 kb-ai → kb-document 的内部调用添加 X-User-Id 头，
 * 绕过 JwtAuthenticationFilter 的 Token 校验。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
public class InternalFeignConfig {

    @Bean
    public RequestInterceptor internalAuthInterceptor() {
        return template -> template.header("X-User-Id", "0");
    }
}
