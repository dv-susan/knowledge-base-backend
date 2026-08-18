package com.knowledge.base.foundation.filter;

import com.knowledge.base.common.result.Result;
import com.knowledge.base.common.utils.UserContextUtil;
import com.knowledge.base.foundation.dto.TokenValidateVO;
import com.knowledge.base.foundation.feign.UserAuthFeignClient;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器（基础服务专用）
 *
 * <p>将JWT Token验证委托给 kb-user-auth 微服务（通过Feign调用），
 * 实现认证逻辑的集中管理，避免在各微服务中重复实现JWT解析和角色查询。</p>
 *
 * <p>认证流程：</p>
 * <ol>
 *   <li>从请求头提取 Bearer Token</li>
 *   <li>通过 Feign 调用 kb-user-auth 的 /auth/validate 验证Token</li>
 *   <li>kb-user-auth 负责：JWT解析、黑名单检查、用户信息查询、角色查询</li>
 *   <li>本过滤器根据返回结果设置 Spring Security 上下文和 ThreadLocal</li>
 * </ol>
 *
 * <p>与WebSocket认证的关系：</p>
 * <ul>
 *   <li>REST API请求 → 由本过滤器处理（Feign调用 kb-user-auth 验证Token）</li>
 *   <li>WebSocket /ws/** → 跳过本过滤器（由WebSocketAuthInterceptor在STOMP层校验）</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private UserAuthFeignClient userAuthFeignClient;

    @Value("${security.jwt.exclude-paths}")
    private String[] excludePaths;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String bearerToken = request.getHeader("Authorization");
        boolean authenticated = false;

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            try {
                // === 通过Feign调用kb-user-auth验证Token ===
                Result<TokenValidateVO> result = userAuthFeignClient
                        .validateToken(bearerToken, null);
                TokenValidateVO validateVO = result != null ? result.getData() : null;

                if (validateVO != null && Boolean.TRUE.equals(validateVO.getValid())) {
                    log.debug("Token验证成功: userId={}, roles={}, uri={}",
                            validateVO.getUserId(), validateVO.getRoles(), requestUri);

                    setSecurityContext(validateVO, bearerToken, request);
                    UserContextUtil.setUserId(validateVO.getUserId());

                    if (validateVO.getUsername() != null) {
                        UserContextUtil.setUsername(validateVO.getUsername());
                    }
                    if (validateVO.getAvatar() != null) {
                        UserContextUtil.setAvatar(validateVO.getAvatar());
                    }
                    authenticated = true;
                } else {
                    log.debug("Token验证失败：Token无效或已过期，uri={}", requestUri);
                }
            } catch (Exception e) {
                log.error("Feign调用 kb-user-auth 验证Token异常: uri={}, error={}",
                        requestUri, e.getMessage());
                // Feign降级时 valid=false，走兜底逻辑
            }
        }

        // Fallback: X-User-Id header（网关已验证JWT后设置，兜底方案）
        if (!authenticated) {
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                try {
                    Long userId = Long.parseLong(userIdHeader);
                    log.debug("使用X-User-Id header认证: userId={}, uri={}", userId, requestUri);
                    UserContextUtil.setUserId(userId);
                    if (bearerToken != null) {
                        UserContextUtil.setToken(bearerToken);
                    }
                    // X-User-Id header的请求授予默认ROLE_USER
                    setDefaultSecurityContext(userId, bearerToken, request);
                } catch (NumberFormatException e) {
                    log.warn("X-User-Id header 格式无效: {}", userIdHeader);
                }
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContextUtil.clear();
        }
    }

    /**
     * 根据Feign返回的Token验证结果设置Spring Security上下文
     */
    private void setSecurityContext(TokenValidateVO validateVO, String token,
                                     HttpServletRequest request) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        List<String> roles = validateVO.getRoles();
        if (roles != null) {
            for (String role : roles) {
                // Spring Security要求角色以 ROLE_ 开头
                String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                authorities.add(new SimpleGrantedAuthority(roleWithPrefix));
            }
        }
        // 兜底：确保至少有一个角色
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        validateVO.getUserId(), token, authorities);
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 设置默认的Security上下文（用于X-User-Id header兜底场景）
     */
    private void setDefaultSecurityContext(Long userId, String token,
                                            HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, token,
                        Collections.singletonList(
                                new SimpleGrantedAuthority("ROLE_USER")));
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 跳过不需要认证的路径
     *
     * <p>通过 {@code security.jwt.exclude-paths} 配置排除路径，
     * 支持 Ant 风格路径匹配。</p>
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        for (String pattern : excludePaths) {
            if (pathMatcher.match(pattern.trim(), path)) {
                return true;
            }
        }
        return false;
    }
}
