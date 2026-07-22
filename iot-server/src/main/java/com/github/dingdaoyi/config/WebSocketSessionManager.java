package com.github.dingdaoyi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话管理 + 广播
 * ponytail: in-memory session set, no cluster broadcast. Redis pub/sub for multi-node.
 */
@Component
@Slf4j
public class WebSocketSessionManager extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("WebSocket 连接: {} (当前 {} 个)", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("WebSocket 断开: {} (剩余 {} 个)", session.getId(), sessions.size());
    }

    /**
     * 广播消息给所有连接的客户端
     */
    public void broadcast(String channel, Object data) {
        if (sessions == null || sessions.isEmpty()) return;
        String payload = "{\"channel\":\"" + channel + "\",\"data\":" + cn.hutool.json.JSONUtil.toJsonStr(data) + "}";
        TextMessage message = new TextMessage(payload);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    log.warn("WebSocket 发送失败: {}", e.getMessage());
                }
            }
        }
    }
}
