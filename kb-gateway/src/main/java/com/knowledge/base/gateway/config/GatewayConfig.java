package com.knowledge.base.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

/**
 * 网关配置类
 *
 * <p>配置网关全局过滤器，路由配置在 application.yml 中</p>
 * <p>CORS 配置由 application.yml 中的 spring.cloud.gateway.globalcors 统一管理，
 *    避免 CorsWebFilter 与 globalcors 产生冲突导致 SockJS 跨域问题</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class GatewayConfig {

    /**
     * 全局认证过滤器
     *
     * @return AuthFilter
     */
    @Bean
    public GlobalFilter authFilter() {
        return new AuthFilter();
    }

    /**
     * 认证过滤器
     */
    public static class AuthFilter implements GlobalFilter, Ordered {

        @Override
        public Mono<Void> filter(
                org.springframework.web.server.ServerWebExchange exchange,
                org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
            // 这里可以添加JWT验证逻辑
            // 目前放行所有请求
            return chain.filter(exchange);
        }

        @Override
        public int getOrder() {
            return -100; // 高优先级
        }
    }
}
