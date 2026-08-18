package com.knowledge.base.userauth.config;

import com.knowledge.base.common.config.CustomAccessDeniedHandler;
import com.knowledge.base.common.config.CustomAuthenticationEntryPoint;
import com.knowledge.base.userauth.filter.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import jakarta.annotation.Resource;

/**
 * Spring Security配置类
 *
 * <p>配置说明：</p>
 * <ul>
 *   <li>禁用CSRF：使用JWT Token不需要CSRF保护</li>
 *   <li>无状态会话：不使用Session，完全依赖JWT Token</li>
 *   <li>JWT认证：在用户名密码认证过滤器之前添加JWT过滤器</li>
 *   <li>CORS配置：由网关统一处理，业务服务禁用CORS</li>
 * </ul>
 *
 * <p>安全策略：</p>
 * <ul>
 *   <li>登录、注册接口：完全开放</li>
 *   <li>API文档接口：完全开放（开发环境）</li>
 *   <li>其他业务接口：需要JWT认证</li>
 *   <li>OPTIONS请求：完全开放（支持CORS预检）</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
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
     *
     * <p>过滤器链执行顺序：</p>
     * <ol>
     *   <li>JwtAuthenticationFilter：解析Token，设置用户上下文</li>
     *   <li>UsernamePasswordAuthenticationFilter：用户名密码认证</li>
     *   <li>ExceptionTranslationFilter：异常处理</li>
     *   <li>FilterSecurityInterceptor：权限校验</li>
     * </ol>
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("==================== 配置Spring Security ====================");

        http
                // 禁用CSRF保护（使用JWT不需要CSRF）
                .csrf(AbstractHttpConfigurer::disable)

                // 配置无状态会话管理（不使用Session）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 禁用CORS（由网关统一处理）
                .cors(AbstractHttpConfigurer::disable)

                // 添加JWT认证过滤器（在用户名密码认证过滤器之前）
                .addFilterBefore(jwtAuthenticationFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 登录、注册、邮箱验证、密码重置接口：完全开放
                        .requestMatchers("/auth/login", "/auth/register", "/auth/verify-email",
                                "/auth/password/reset/**", "/auth/auth/**").permitAll()
                        .requestMatchers("/teams/tree").permitAll()

                        // Feign内部调用：token验证接口、角色用户查询、审核员ID查询
                        .requestMatchers("/auth/validate", "/auth/users/by-role", "/auth/reviewer-ids").permitAll()

                        // /me接口：需要认证（但不需要特定角色）
                        .requestMatchers("/auth/me").authenticated()

                        // 测试端点：用于诊断403问题
                        .requestMatchers("/auth/debug/**", "/auth/test/**").permitAll()

                        // 公开API：完全开放
                        .requestMatchers("/public/**").permitAll()

                        // WebSocket：完全开放
                        .requestMatchers("/ws/**").permitAll()

                        // 健康检查和监控：完全开放
                        .requestMatchers("/actuator/**").permitAll()

                        // API文档：完全开放（开发环境）
                        .requestMatchers("/doc.html", "/swagger-resources/**",
                                "/v3/api-docs/**", "/webjars/**").permitAll()

                        // 测试端点：完全开放
                        .requestMatchers("/test", "/test-error").permitAll()

                        // OPTIONS预检请求：完全开放（支持CORS）
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 其他所有请求：需要认证
                        .anyRequest().authenticated()
                )

                // 配置异常处理：返回JSON格式的401/403响应
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        log.info("Spring Security配置完成：JWT认证模式，无状态会话");
        log.info("==================== Spring Security 配置完毕 ====================");
        return http.build();
    }

    /**
     * 配置密码编码器
     *
     * <p>使用BCrypt算法，强度为10</p>
     * <p>BCrypt特点：</p>
     * <ul>
     *   <li>自动加盐：每次加密结果都不同</li>
     *   <li>强度可调：默认10，范围4-31</li>
     *   <li>单向加密：不可逆向解密</li>
     * </ul>
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
