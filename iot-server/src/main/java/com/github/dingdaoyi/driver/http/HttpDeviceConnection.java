package com.github.dingdaoyi.driver.http;

import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.core.base.BaseResult;
import com.github.dingdaoyi.core.enums.ResultCode;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.ExceptionType;
import com.github.dingdaoyi.proto.model.ProtocolException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP 设备连接实现
 * HTTP 是无状态协议，使用时需要传入 request-id 关联原始请求的响应对象
 *
 * @author dingyunwei
 */
@Slf4j
public class HttpDeviceConnection implements DeviceConnection, Closeable {

    private final String deviceKey;
    private final String productKey;
    private final HttpServletResponse response;
    private boolean success = true;

    public HttpDeviceConnection(String deviceKey, String productKey, HttpServletResponse response) {
        this.deviceKey = deviceKey;
        this.productKey = productKey;
        this.response = response;
    }

    @Override
    public boolean isConnected() {
        // HTTP 是无状态协议，检查响应是否仍可写入
        return response != null && !response.isCommitted();
    }

    @Override
    public void disconnect() {
        // HTTP 连接由 Servlet 容器管理，无需手动断开
        if (response != null && !response.isCommitted()) {
            try {
                response.flushBuffer();
            } catch (IOException e) {
                log.warn("刷新 HTTP 响应失败: deviceKey={}", deviceKey, e);
            }
        }
    }

    @Override
    public void sendMessage(Map<String, Object> metadata, byte[] message) throws ProtocolException {
        success = false;
        if (!isConnected()) {
            throw new ProtocolException(deviceKey, ExceptionType.DEVICE_NOT_CONNECTED);
        }
        try {
            response.setContentType(metadata.getOrDefault("contentType", "application/octet-stream").toString());
            response.setContentLength(message.length);
            response.getOutputStream().write(message);
            response.flushBuffer();
        } catch (IOException e) {
            log.error("HTTP发送消息失败: deviceKey={}", deviceKey, e);
        }
    }

    @Override
    public void close() throws IOException {
        if (success && isConnected()) {
            writeJson(BaseResult.success());
        }
    }

    // ==================== 统一响应方法 ====================

    /**
     * 写入 JSON 响应
     */
    public void writeJson(Object data) {
        if (!isConnected()) {
            return;
        }
        try {
            response.setContentType("application/json;charset=utf-8");
            String json = JSONUtil.toJsonStr(data);
            response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            response.flushBuffer();
        } catch (IOException e) {
            log.error("HTTP发送响应失败: deviceKey={}", deviceKey, e);
        }
    }

    /**
     * 写入成功响应
     */
    public void writeSuccess() {
        writeJson(BaseResult.success());
    }

    /**
     * 写入成功响应（带数据）
     */
    public void writeSuccess(Object data) {
        writeJson(BaseResult.success(data));
    }

    /**
     * 写入成功响应（带消息）
     */
    public void writeSuccess(String msg, Object data) {
        writeJson(BaseResult.success(msg, data));
    }

    /**
     * 写入失败响应
     */
    public void writeFail(ResultCode code, String msg) {
        writeJson(BaseResult.fail(code, msg));
    }

    /**
     * 写入失败响应（使用默认错误码）
     */
    public void writeFail(String msg) {
        writeJson(BaseResult.fail(msg));
    }

    /**
     * 写入 BaseResult 响应
     */
    public void writeBaseResult(BaseResult<?> result) {
        writeJson(result);
    }
}
