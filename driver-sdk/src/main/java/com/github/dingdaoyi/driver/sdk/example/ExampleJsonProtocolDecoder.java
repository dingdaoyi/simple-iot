package com.github.dingdaoyi.driver.sdk.example;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.proto.model.*;

import com.github.dingdaoyi.proto.model.tsl.TslModel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 示例: 自定义 JSON 协议解码器
 *
 * 协议格式: 设备上报 {"temperature":25.5,"humidity":60}
 * 解码后映射到物模型属性
 *
 * @author dingyunwei
 */
public class ExampleJsonProtocolDecoder implements ProtocolDecoder {

    @Override
    public String protocolKey() {
        return "example-json";
    }

    @Override
    public DecodeResult decode(DeviceRequest request, TslModel tslModel) throws ProtocolException {
        String json = new String(request.getData(), StandardCharsets.UTF_8);
        JSONObject obj;
        try {
            obj = JSONUtil.parseObj(json);
        } catch (Exception e) {
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.INVALID_PARAM, null, "JSON 解析失败: " + e.getMessage());
        }

        List<DeviceData> dataList = new ArrayList<>();
        for (String key : obj.keySet()) {
            dataList.add(new DeviceData(key, DataTypeEnum.DOUBLE, obj.get(key)));
        }

        DecodeResult result = new DecodeResult();
        result.setDataList(dataList);
        result.setRowData(json);
        return result;
    }

    @Override
    public EncoderResult encode(EncoderMessage message, TslModel tslModel) throws ProtocolException {
        String json = JSONUtil.toJsonStr(message.getParams());
        EncoderResult result = new EncoderResult();
        result.setMessage(json.getBytes(StandardCharsets.UTF_8));
        return result;
    }

    @Override
    public void responseError(DeviceConnection connection, ProtocolException e) {
        if (connection.isConnected()) {
            String errMsg = "{\"code\":" + e.getType().code + ",\"msg\":\"" + e.getMessage() + "\"}";
            try {
                connection.sendMessage(java.util.Map.of(), errMsg.getBytes(StandardCharsets.UTF_8));
            } catch (ProtocolException ignored) {}
        }
    }
}
