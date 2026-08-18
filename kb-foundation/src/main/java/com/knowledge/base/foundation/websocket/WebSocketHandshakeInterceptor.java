package com.knowledge.base.foundation.websocket;

import com.knowledge.base.common.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Principal;
import java.util.Map;

/**
 * WebSocket 握手拦截器
 *
 * <p>在 SockJS HTTP 握手阶段从 URL 查询参数中提取 JWT Token，
 * 验证通过后设置会话 Principal，确保后续
 * {@code convertAndSendToUser} 能正确路由到该用户。</p>
 *
 * <p>STOMP CONNECT 帧中的 Authorization 头发送较晚（在握手完成之后），
 * 无法追溯更新已建立的 WebSocket 会话的 Principal。
 * 因此需要在本拦截器中完成认证，而不是仅依赖 ChannelInterceptor。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                    ServerHttpResponse response,
                                    WebSocketHandler wsHandler,
                                    Map<String, Object> attributes) {
        // 从 URL 查询参数中读取 token
        String token = null;
        if (request.getURI().getQuery() != null) {
            String[] queryParams = request.getURI().getQuery().split("&");
            for (String param : queryParams) {
                String[] kv = param.split("=", 2);
                if ("token".equals(kv[0]) && kv.length > 1) {
                    token = kv[1];
                    break;
                }
            }
        }

        if (token != null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    Long userId = jwtUtil.getUserId(token);
                    if (userId != null) {
                        String userIdStr = String.valueOf(userId);
                        // 关键：在 attributes 中设置 Principal，Spring 会将其作为会话的 Principal
                        attributes.put("principal", new Principal() {
                            @Override
                            public String getName() {
                                return userIdStr;
                            }
                        });
                        log.debug("WebSocket 握手认证成功：userId={}", userId);
                        return true;
                    }
                } else {
                    log.warn("WebSocket 握手认证失败：Token 无效或已过期");
                }
            } catch (Exception e) {
                log.warn("WebSocket 握手认证异常：{}", e.getMessage());
            }
        } else {
            log.debug("WebSocket 握手未携带 token 参数，以匿名身份连接");
        }

        // 即使没有 token 也允许连接（匿名用户可接收广播消息）
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                                ServerHttpResponse response,
                                WebSocketHandler wsHandler,
                                Exception exception) {
        // no-op
    }
}
