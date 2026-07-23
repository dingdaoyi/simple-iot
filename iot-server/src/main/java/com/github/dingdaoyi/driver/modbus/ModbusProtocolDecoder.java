package com.github.dingdaoyi.driver.modbus;

import com.github.dingdaoyi.iot.proto.ProtocolFactory;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.proto.model.*;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Modbus 协议解码器
 * ponytail: 透传解码器，不做报文解析。
 * ModbusPollingService 轮询时已直接把寄存器值映射为 DeviceData，
 * 这里只负责让 ProtocolFactory 能找到 decoder，让 messageUp 流程不中断。
 */
@Slf4j
@Component
public class ModbusProtocolDecoder implements ProtocolDecoder, ApplicationListener<ApplicationReadyEvent> {

    public static final String PROTO_KEY = "modbus-tcp";

    @Override
    public String protocolKey() {
        return PROTO_KEY;
    }

    @Override
    public DecodeResult decode(DeviceRequest request, TslModel tslModel) throws ProtocolException {
        DecodeResult result = new DecodeResult();
        result.setMessageId(0); // ponytail: Modbus has no message ID, set default
        String json = new String(request.getData());
        var obj = cn.hutool.json.JSONUtil.parseObj(json);
        var params = obj.getJSONObject("params");
        if (params != null) {
            for (var entry : params.entrySet()) {
                String identifier = entry.getKey();
                var item = (cn.hutool.json.JSONObject) entry.getValue();
                Object value = item.get("value");
                int code = item.getInt("dataType", 3);
                DataTypeEnum dt = fromCode(code);
                result.getDataList().add(new DeviceData(identifier, dt, value));
            }
        }
        return result;
    }

    // ponytail: DataTypeEnum has no fromCode, inline it
    private static DataTypeEnum fromCode(int code) {
        for (DataTypeEnum dt : DataTypeEnum.values()) {
            if (dt.getValue() == code) return dt;
        }
        return DataTypeEnum.DOUBLE;
    }

    @Override
    public EncoderResult encode(EncoderMessage message, TslModel tslModel) throws ProtocolException {
        // ponytail: Modbus 写寄存器, add when 需要下行控制
        throw new ProtocolException(message.getDeviceKey(), ExceptionType.SYSTEM_ERROR, null, "Modbus write not supported");
    }

    @Override
    public void responseError(DeviceConnection connection, ProtocolException e) {
        log.warn("Modbus protocol error: {}", e.getMessage());
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ProtocolFactory.DECODERS.put(PROTO_KEY, this);
        log.info("Modbus protocol decoder registered: {}", PROTO_KEY);
    }
}
