package com.github.dingdaoyi.driver.tcp;

import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import com.github.dingdaoyi.driver.tcp.core.DeviceKeyParserLoader;
import com.github.dingdaoyi.driver.tcp.core.TcpServerManager;
import com.github.dingdaoyi.driver.tcp.core.TcpChannelManager;
import com.github.dingdaoyi.driver.tcp.core.TcpAuthProcessor;
import com.github.dingdaoyi.core.driver.DeviceTransport;
import com.github.dingdaoyi.entity.Driver;
import com.github.dingdaoyi.service.DriverService;
import com.github.dingdaoyi.service.DeviceService;
import com.github.dingdaoyi.iot.IotDataProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @author dingyunwei
 */
@Slf4j
@Component
public class TcpDeviceTransport implements DeviceTransport {
    @Resource
    private DriverService driverService;
    @Resource
    private DeviceService deviceService;
    @Resource
    private IotDataProcessor dataProcessor;

    private final TcpServerManager serverManager = new TcpServerManager();
    private final TcpChannelManager channelManager = new TcpChannelManager();
    private TcpAuthProcessor authProcessor;

    private volatile boolean running = false;

    @Override
    public void start() {
        running = true;
        authProcessor = new TcpAuthProcessor(deviceService, dataProcessor);
        // 查询所有TCP驱动
        List<Driver> tcpDrivers = driverService.lambdaQuery()
                .eq(Driver::getType, com.github.dingdaoyi.core.driver.DriverTypeEnum.TCP)
                .list();
        for (Driver driver : tcpDrivers) {
            Integer port = driver.getPort();
            if (port == null) {
                log.warn("驱动[{}]端口未配置，跳过启动", driver.getName());
                continue;
            }
            String driverName = driver.getName();
            DeviceKeyParser parser = DeviceKeyParserLoader.getParser(driverName);
            if (parser == null) {
                log.warn("未找到驱动[{}]的设备号解析器，端口:{}", driverName, port);
                continue;
            }
            serverManager.startServer(port, new TcpInboundHandler(port, driverName, parser));
        }
    }

    @Override
    public void stop() {
        running = false;
        serverManager.stopAll();
    }

    @Override
    public boolean isRunning() { return running; }
    @Override
    public String getType() { return "TCP"; }
    @Override
    public String getName() { return "TCP统一驱动"; }

    // Netty Handler
    @ChannelHandler.Sharable
    private class TcpInboundHandler extends ChannelInboundHandlerAdapter {
        private final int port;
        private final String driverName;
        private final DeviceKeyParser parser;
        public TcpInboundHandler(int port, String driverName, DeviceKeyParser parser) {
            this.port = port;
            this.driverName = driverName;
            this.parser = parser;
        }
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            channelManager.register(ctx.channel(), port, driverName, ctx.channel().remoteAddress());
        }
        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            channelManager.unregister(ctx.channel());
        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf buf = (ByteBuf) msg;
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            buf.release();
            TcpChannelManager.ChannelContext context = channelManager.getContext(ctx.channel());
            authProcessor.process(ctx.channel(), data, context, parser);
        }
    }
} 