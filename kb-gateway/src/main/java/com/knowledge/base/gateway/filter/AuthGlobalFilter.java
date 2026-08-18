package com.knowledge.base.gateway.filter;

import com.knowledge.base.common.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import jakarta.annotation.Resource;

/**
 * 全局认证过滤器
 *
 * <p>在网关层解析JWT Token，将userId通过X-User-Id请求头传递给下游微服务，
 * 避免下游服务重复解析Token。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 跳过公开接口
        if (shouldSkip(path, request.getMethod().name())) {
            return chain.filter(exchange);
        }

        // 提取并解析Token
        String token = extractToken(request);
        if (token == null) {
            return chain.filter(exchange);
        }

        try {
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            if (userId != null) {
                // 将userId注入请求头传递给下游
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", String.valueOf(userId))
                        .build();
                log.debug("AuthGlobalFilter: token parsed OK, userId={}, path={}", userId, path);
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }
        } catch (Exception e) {
            log.debug("AuthGlobalFilter: token parse failed for path={}: {}", path, e.getMessage());
        }

        return chain.filter(exchange);
    }

    private boolean shouldSkip(String path, String method) {
        if ("OPTIONS".equals(method)) return true;
        return path.startsWith("/api/user/") ||
               path.startsWith("/auth/") ||
               path.startsWith("/api/auth/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/ws/") ||
               path.startsWith("/doc.html") ||
               path.startsWith("/swagger-resources/") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/webjars/");
    }

    private String extractToken(ServerHttpRequest request) {
        // 优先从 Authorization header 读取
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 回退：从 cookie 读取（用于 <video>/<audio>/fetch() 等不走拦截器的请求）
        var cookies = request.getCookies();
        if (cookies.containsKey("access_token")) {
            var cookie = cookies.getFirst("access_token");
            if (cookie != null && StringUtils.hasText(cookie.getValue())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -200; // 在GlobalLogFilter之前执行
    }
}
