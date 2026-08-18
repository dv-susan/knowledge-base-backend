package com.knowledge.base.file.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 文件服务安全配置
 *
 * <p>配置说明：</p>
 * <ul>
 *   <li>禁用CSRF：支持文件上传不需要CSRF保护</li>
 *   <li>无状态会话：完全依赖网关的认证</li>
 *   <li>所有接口允许访问：认证由网关统一处理</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * 配置安全过滤链
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护（文件上传不需要）
                .csrf(AbstractHttpConfigurer::disable)

                // 配置无状态会话
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 禁用CORS（由网关统一处理）
                .cors(AbstractHttpConfigurer::disable)

                // 所有请求都允许访问（认证由网关统一处理）
                .authorizeHttpRequests(auth -> auth
                        // 健康检查和监控：完全开放
                        .requestMatchers("/actuator/**").permitAll()

                        // API文档：完全开放（开发环境）
                        .requestMatchers("/doc.html", "/swagger-resources/**",
                                "/v3/api-docs/**", "/webjars/**", "/swagger-ui/**").permitAll()

                        // 文件上传和下载接口：完全开放（由网关控制访问权限）
                        .requestMatchers("/files/**").permitAll()

                        // 其他所有请求：允许访问（由网关统一处理认证）
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
