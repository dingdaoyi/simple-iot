package com.github.dingdaoyi.driver.tcp.core;

import io.netty.channel.Channel;
import lombok.Data;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingyunwei
 */
public class TcpChannelManager {
    private final Map<Channel, ChannelContext> channelContextMap = new ConcurrentHashMap<>();

    public void register(Channel channel, int port, String driverName, SocketAddress remoteAddress) {
        ChannelContext context = new ChannelContext();
        context.setPort(port);
        context.setDriverName(driverName);
        context.setRemoteAddress(remoteAddress);
        channelContextMap.put(channel, context);
    }

    public void unregister(Channel channel) {
        channelContextMap.remove(channel);
    }

    public ChannelContext getContext(Channel channel) {
        return channelContextMap.get(channel);
    }

    @Data
    public static class ChannelContext {
        private boolean authenticated = false;
        private String deviceKey;
        private int port;
        private String driverName;
        private SocketAddress remoteAddress;
        private List<byte[]> cache = new ArrayList<>();
    }
} 