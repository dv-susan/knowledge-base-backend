package com.knowledge.base.foundation.websocket;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Resource
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Resource
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单消息代理，用于向客户端推送消息
        registry.enableSimpleBroker("/topic", "/queue");
        // 客户端发送消息的目标前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 用户消息前缀（用于 convertAndSendToUser）
        registry.setUserDestinationPrefix("/user/");
    }

    /**
     * 配置STOMP端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，允许跨域
        // CustomHandshakeHandler 确保从 HandshakeInterceptor 设置的 Principal
        // 被正确应用到 WebSocket 会话，使得 convertAndSendToUser 能路由消息
        registry.addEndpoint("/ws/notification")
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .addInterceptors(webSocketHandshakeInterceptor)
                .withSockJS(); // 启用SockJS支持
    }

    /**
     * 注册认证拦截器，在 STOMP CONNECT 时从 JWT 提取用户身份
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}
