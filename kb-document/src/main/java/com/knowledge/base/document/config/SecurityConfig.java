package com.knowledge.base.document.config;

import com.knowledge.base.common.config.CustomAccessDeniedHandler;
import com.knowledge.base.common.config.CustomAuthenticationEntryPoint;
import com.knowledge.base.document.filter.JwtAuthenticationFilter;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * 文档服务Security配置
 *
 * <p>配置说明：</p>
 * <ul>
 *   <li>禁用CSRF：使用JWT Token不需要CSRF保护</li>
 *   <li>无状态会话：不使用Session，完全依赖JWT Token</li>
 *   <li>禁用CORS：由网关统一处理，业务服务禁用CORS</li>
 * </ul>
 *
 * <p>接口访问控制：</p>
 * <ul>
 *   <li>分类接口：公开访问，不需要认证</li>
 *   <li>文档接口：需要认证</li>
 *   <li>OPTIONS请求：完全开放</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 公开分享访问：允许匿名查看分享链接
                        .requestMatchers("/share/**").permitAll()

                        // API文档：完全开放（开发环境）
                        .requestMatchers("/doc.html", "/swagger-resources/**",
                                "/v3/api-docs/**", "/webjars/**").permitAll()

                        // 健康检查和监控：完全开放
                        .requestMatchers("/actuator/**").permitAll()

                        // 测试端点：完全开放
                        .requestMatchers("/test", "/test-error").permitAll()

                        // OPTIONS预检请求：完全开放
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // 其他所有请求：需要认证，细粒度权限由方法注解控制
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
