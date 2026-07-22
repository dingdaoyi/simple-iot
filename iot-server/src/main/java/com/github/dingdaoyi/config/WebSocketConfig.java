package com.github.dingdaoyi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import jakarta.annotation.Resource;

/**
 * WebSocket 配置: /ws/iot endpoint
 * ponytail: single endpoint, channel-based routing in payload
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private WebSocketSessionManager sessionManager;

    @Resource
    private WebSocketAuthInterceptor authInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sessionManager, "/ws/iot")
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*");
    }
}
