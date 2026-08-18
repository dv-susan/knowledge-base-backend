package com.knowledge.base.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 网关安全配置类
 *
 * <p>配置Spring Security允许CORS预检请求通过</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * 配置安全过滤器链
     *
     * @param http ServerHttpSecurity
     * @return SecurityWebFilterChain
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // 禁用 CSRF
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        // 禁用默认 CORS（使用我们自定义的 CorsWebFilter）
        http.cors(ServerHttpSecurity.CorsSpec::disable);

        // 允许所有请求通过（认证由 AuthFilter 处理）
        http.authorizeExchange(exchanges -> exchanges.anyExchange().permitAll());

        return http.build();
    }
}
