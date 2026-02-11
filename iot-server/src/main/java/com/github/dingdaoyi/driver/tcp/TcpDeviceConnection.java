package com.github.dingdaoyi.driver.tcp;

import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.ExceptionType;
import com.github.dingdaoyi.proto.model.ProtocolException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.tio.utils.json.JsonUtil;

import java.util.Map;

/**
 * TCP 设备连接实现
 *
 * @author dingyunwei
 */
@Slf4j
public record TcpDeviceConnection(String deviceKey, String productKey, Channel channel) implements DeviceConnection {

    @Override
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    @Override
    public void disconnect() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
    }

    @Override
    public void sendMessage(Map<String, Object> metadata, byte[] message) throws ProtocolException {
        if (!isConnected()) {
            log.error("通道断开,{}",deviceKey);
        }
        try {
            ByteBuf buffer = channel.alloc().buffer(message.length);
            buffer.writeBytes(message);
            channel.writeAndFlush(buffer);
        } catch (Exception e) {
            log.error("TCP发送消息失败: deviceKey={}", deviceKey, e);
        }
    }
}
