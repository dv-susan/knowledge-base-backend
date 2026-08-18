package com.knowledge.base.foundation.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * 自定义 WebSocket 握手处理器
 *
 * <p>覆盖 {@link #determineUser} 方法，从 attributes 中读取
 * {@link WebSocketHandshakeInterceptor} 设置的 Principal，
 * 确保 WebSocket 会话拥有正确的用户身份。</p>
 *
 * <p>Spring 的 {@link DefaultHandshakeHandler#determineUser} 默认
 * 只会从 ServerHttpRequest 获取 Principal；而通过 SockJS 建立的
 * WebSocket 连接没有 Servlet Filter 链设置 Principal。
 * 因此需要本类从握手 attributes 中读取并返回。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                       WebSocketHandler wsHandler,
                                       Map<String, Object> attributes) {
        // 从 attributes 中读取 HandshakeInterceptor 设置的 Principal
        Principal principal = (Principal) attributes.get("principal");
        if (principal != null) {
            log.debug("CustomHandshakeHandler.determineUser: 从 attributes 获取 Principal, name={}", principal.getName());
            return principal;
        }

        // 回退到默认行为
        Principal requestPrincipal = request.getPrincipal();
        if (requestPrincipal != null) {
            log.debug("CustomHandshakeHandler.determineUser: 从 request 获取 Principal, name={}", requestPrincipal.getName());
            return requestPrincipal;
        }

        log.debug("CustomHandshakeHandler.determineUser: 无 Principal，匿名连接");
        return super.determineUser(request, wsHandler, attributes);
    }
}
