package com.knowledge.base.foundation.websocket;

import com.knowledge.base.common.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * WebSocket STOMP 认证拦截器
 *
 * <p>在 STOMP CONNECT 阶段从 Authorization 头提取 JWT Token，
 * 验证通过后将 userId 设置为会话的 Principal，
 * 使得 {@code convertAndSendToUser} 能够正确路由消息。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    if (jwtUtil.validateToken(token)) {
                        Long userId = jwtUtil.getUserId(token);
                        if (userId != null) {
                            accessor.setUser(new Principal() {
                                @Override
                                public String getName() {
                                    return String.valueOf(userId);
                                }
                            });
                            log.debug("WebSocket 认证成功：userId={}", userId);
                        }
                    } else {
                        log.warn("WebSocket 认证失败：Token 无效或已过期");
                    }
                } catch (Exception e) {
                    log.warn("WebSocket 认证异常：{}", e.getMessage());
                }
            } else {
                log.debug("WebSocket CONNECT 未携带 Authorization 头，以匿名身份连接（仅可接收广播消息）");
            }
        }

        return message;
    }
}
