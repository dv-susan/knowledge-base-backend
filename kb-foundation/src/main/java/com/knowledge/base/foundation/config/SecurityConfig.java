package com.knowledge.base.foundation.config;

import com.knowledge.base.common.config.CustomAccessDeniedHandler;
import com.knowledge.base.common.config.CustomAuthenticationEntryPoint;
import com.knowledge.base.foundation.filter.JwtAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 基础服务Security配置
 *
 * <p>配置说明：</p>
 * <ul>
 *   <li>禁用CSRF：使用JWT Token不需要CSRF保护</li>
 *   <li>无状态会话：不使用Session，完全依赖JWT Token</li>
 *   <li>禁用CORS：由网关统一处理，业务服务禁用CORS</li>
 *   <li>WebSocket端点：HTTP层放行，认证由STOMP层WebSocketAuthInterceptor处理</li>
 * </ul>
 *
 * <p>WebSocket认证机制：</p>
 * <ol>
 *   <li>客户端通过SockJS连接 {@code /ws/notification}</li>
 *   <li>STOMP CONNECT帧携带 {@code Authorization: Bearer <token>}</li>
 *   <li>{@link WebSocketAuthInterceptor} 在STOMP层验证JWT并设置Principal</li>
 *   <li>服务端通过 {@code convertAndSendToUser(userId, ...)} 进行点对点推送</li>
 *   <li>匿名连接（无Token）仅可接收广播消息（/topic/**）</li>
 * </ol>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    private CustomAuthenticationEntryPoint authEntryPoint;

    @Resource
    private CustomAccessDeniedHandler accessDeniedHandler;

    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护（使用JWT不需要）
                .csrf(AbstractHttpConfigurer::disable)

                // 配置无状态会话（不使用Session）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 禁用CORS（由网关统一处理）
                .cors(AbstractHttpConfigurer::disable)

                // 添加JWT认证过滤器
                .addFilterBefore(jwtAuthenticationFilter,
                        org.springframework.security.web.authentication
                                .UsernamePasswordAuthenticationFilter.class)

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // ===== WebSocket端点：HTTP层完全放行 =====
                        // WebSocket认证由STOMP层的WebSocketAuthInterceptor处理
                        // SockJS的HTTP握手请求（/ws/notification/info等）也需要放行
                        .requestMatchers("/ws/**").permitAll()

                        // ===== API文档和监控：完全开放 =====
                        .requestMatchers("/doc.html", "/swagger-resources/**",
                                "/v3/api-docs/**", "/webjars/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // ===== OPTIONS预检请求：完全开放 =====
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ===== 通知接口：需要认证 =====
                        .requestMatchers("/notifications/**").authenticated()

                        // ===== 公开配置接口：无需认证（登录页/注册页需要读取） =====
                        .requestMatchers("/config/public").permitAll()

                        // ===== 其他请求：需要认证 =====
                        .anyRequest().authenticated()
                )

                // 配置异常处理：返回JSON格式的401/403响应
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }
}
