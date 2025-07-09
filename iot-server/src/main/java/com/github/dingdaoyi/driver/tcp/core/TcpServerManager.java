package com.github.dingdaoyi.driver.tcp.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingyunwei
 */
@Slf4j
public class TcpServerManager {
    private final Map<Integer, Channel> portChannelMap = new ConcurrentHashMap<>();

    public void startServer(int port, ChannelInboundHandlerAdapter handler) {
        Thread.startVirtualThread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(handler);
                            }
                        });
                ChannelFuture f = b.bind(port).sync();
                portChannelMap.put(port, f.channel());
                log.info("TCP端口监听启动: {}", port);
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("TCP端口[{}]启动失败", port, e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
    }

    public void stopAll() {
        portChannelMap.values().forEach(Channel::close);
        portChannelMap.clear();
    }
} 