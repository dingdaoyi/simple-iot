package com.github.dingdaoyi.iot.proto.script;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dingdaoyi.iot.proto.impl.ScriptProtocolInitialize.ScriptType;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import com.github.dingdaoyi.proto.model.DeviceEventData;
import com.github.dingdaoyi.proto.model.DeviceRequest;
import com.github.dingdaoyi.proto.model.DeviceServiceResData;
import com.github.dingdaoyi.proto.model.EncoderMessage;
import com.github.dingdaoyi.proto.model.EncoderResult;
import com.github.dingdaoyi.proto.model.ExceptionType;
import com.github.dingdaoyi.proto.model.ProtocolException;
import com.github.dingdaoyi.proto.model.tsl.EventTypeEnum;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 脚本协议解析器
 * 支持动态脚本语言解析物联网设备数据
 *
 * 支持的脚本语言：
 * - JavaScript (通过 GraalVM)
 * - Groovy
 * - Python (需要额外配置 Jython)
 *
 * @author dingyunwei
 */
@Slf4j
public class ScriptProtocolDecoder implements ProtocolDecoder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final String protocolKey;
    private final String protocolName;
    private final String scriptContent;
    private final ScriptType scriptType;

    private Context context;
    private Value decodeFunction;
    private Value encodeFunction;

    public ScriptProtocolDecoder(String protocolKey, String protocolName,
                                  String scriptContent, ScriptType scriptType) {
        this.protocolKey = protocolKey;
        this.protocolName = protocolName;
        this.scriptContent = scriptContent;
        this.scriptType = scriptType;
        initEngine();
    }

    /**
     * 初始化脚本引擎
     */
    private void initEngine() {
        try {
            switch (scriptType) {
                case JAVASCRIPT -> initJavaScriptEngine();
                case GROOVY -> initGroovyEngine();
                default -> {
                    log.warn("暂不支持的脚本类型: {}，使用 JavaScript 代替", scriptType);
                    initJavaScriptEngine();
                }
            }
        } catch (Exception e) {
            log.error("脚本引擎初始化失败: {} ({})", protocolKey, scriptType.getDisplayName(), e);
        }
    }

    /**
     * 初始化 JavaScript 引擎 (使用 GraalVM)
     */
    private void initJavaScriptEngine() {
        try {
            // 不开放 HostAccess，脚本协议只通过 JSON 结构与平台交互，降低脚本误访问宿主环境的风险。
            context = Context.newBuilder("js")
                    .allowAllAccess(false)
                    .option("engine.WarnInterpreterOnly", "false")
                    .build();

            // 支持 exports.decode/exports.encode 以及 module.exports = { decode, encode } 两种常见写法。
            String wrappedScript = """
                    (function() {
                      const exports = {};
                      const module = { exports };
                    """ + scriptContent + """

                      return module.exports || exports;
                    })()
                    """;

            Value result = context.eval("js", wrappedScript);

            if (result != null && result.hasMembers()) {
                Value decodeFn = result.getMember("decode");
                Value encodeFn = result.getMember("encode");

                if (decodeFn != null && decodeFn.canExecute()) {
                    decodeFunction = decodeFn;
                    log.info("JavaScript 脚本加载成功 - decode 函数: {}", protocolKey);
                }

                if (encodeFn != null && encodeFn.canExecute()) {
                    encodeFunction = encodeFn;
                    log.info("JavaScript 脚本加载成功 - encode 函数: {}", protocolKey);
                }

                if (decodeFunction == null && encodeFunction == null) {
                    log.warn("JavaScript 脚本未定义 decode 或 encode 函数: {}", protocolKey);
                }
            }

            log.info("JavaScript 引擎初始化成功: {}", protocolKey);

        } catch (Exception e) {
            log.error("JavaScript 引擎初始化失败: {}", protocolKey, e);
        }
    }

    /**
     * 初始化 Groovy 引擎
     */
    private void initGroovyEngine() {
        log.warn("Groovy 引擎暂未完全实现，使用 JavaScript 兼容模式: {}", protocolKey);
        initJavaScriptEngine();
    }

    @Override
    public String protocolKey() {
        return protocolKey;
    }

    @Override
    public DecodeResult decode(DeviceRequest request, TslModel tslModel) throws ProtocolException {
        if (context == null) {
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.SYSTEM_ERROR, -1,
                    "脚本引擎未初始化: " + scriptType.getDisplayName());
        }
        if (decodeFunction == null || !decodeFunction.canExecute()) {
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.SYSTEM_ERROR, -1,
                    "脚本协议未定义 decode(request, tslModel) 函数: " + protocolKey);
        }

        try {
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("deviceKey", request.getDeviceKey());
            inputData.put("productKey", request.getProductKey());
            inputData.put("protoKey", request.getProtoKey());
            inputData.put("messageType", request.getMessageType() == null ? null : request.getMessageType().name());
            inputData.put("data", request.getData() != null ? new String(request.getData(), StandardCharsets.UTF_8) : "");

            Value result = decodeFunction.execute(
                    toJavaScriptValue(inputData),
                    toJavaScriptValue(tslModel)
            );

            if (result == null || result.isNull()) {
                throw new ProtocolException(request.getDeviceKey(), ExceptionType.INVALID_PARAM, -1,
                        "脚本未返回解析结果");
            }

            return parseScriptResult(result, request);

        } catch (ProtocolException e) {
            throw e;
        } catch (PolyglotException e) {
            log.error("脚本执行失败: {}, 错误: {}", protocolKey, e.getMessage(), e);
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.SYSTEM_ERROR, -1,
                    "脚本执行失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("脚本执行上下文构建失败: {}", protocolKey, e);
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.SYSTEM_ERROR, -1,
                    "脚本执行上下文构建失败: " + e.getMessage());
        }
    }

    /**
     * 解析脚本返回结果
     */
    private DecodeResult parseScriptResult(Value result, DeviceRequest request) throws ProtocolException {
        DecodeResult decodeResult = new DecodeResult();

        try {
            if (!result.hasMembers()) {
                throw new IllegalArgumentException("decode 返回值必须是对象");
            }

            Value messageIdValue = result.getMember("messageId");
            if (hasValue(messageIdValue)) {
                decodeResult.setMessageId(toInteger(messageIdValue));
            }

            Value rawDataValue = firstMember(result, "rawData", "rowData");
            if (hasValue(rawDataValue)) {
                decodeResult.setRowData(String.valueOf(convertValue(rawDataValue)));
            }

            Value dataListValue = result.getMember("dataList");
            if (hasValue(dataListValue) && dataListValue.hasArrayElements()) {
                long length = dataListValue.getArraySize();
                for (long i = 0; i < length; i++) {
                    Value item = dataListValue.getArrayElement(i);
                    if (item != null && item.hasMembers()) {
                        decodeResult.getDataList().add(parseDeviceData(item));
                    }
                }
            }

            Value eventDataValue = result.getMember("eventData");
            if (hasValue(eventDataValue) && eventDataValue.hasMembers()) {
                decodeResult.setEventData(parseEventData(eventDataValue));
            }

            Value serviceResValue = result.getMember("serviceResData");
            if (hasValue(serviceResValue) && serviceResValue.hasMembers()) {
                decodeResult.setServiceResData(parseServiceResData(serviceResValue));
            }

        } catch (Exception e) {
            log.error("解析脚本结果失败: {}", protocolKey, e);
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.INVALID_PARAM, -1,
                    "解析脚本结果失败: " + e.getMessage());
        }

        return decodeResult;
    }

    private DeviceData parseDeviceData(Value item) {
        String identifier = asString(firstMember(item, "identifier", "id", "code"));
        Value valueValue = item.getMember("value");
        Object value = hasValue(valueValue) ? convertValue(valueValue) : null;
        String typeStr = asString(firstMember(item, "type", "dataType"));
        return new DeviceData(identifier, parseDataType(typeStr), value);
    }

    private DeviceEventData parseEventData(Value eventDataValue) {
        String identifier = asString(firstMember(eventDataValue, "eventIdentifier", "identifier", "id", "code"));
        EventTypeEnum eventType = parseEventType(firstMember(eventDataValue, "eventType", "type", "level"));
        DeviceEventData eventData = new DeviceEventData(identifier, eventType);

        Value paramsValue = firstMember(eventDataValue, "params", "dataList");
        if (hasValue(paramsValue) && paramsValue.hasArrayElements()) {
            long length = paramsValue.getArraySize();
            for (long i = 0; i < length; i++) {
                Value item = paramsValue.getArrayElement(i);
                if (item != null && item.hasMembers()) {
                    eventData.getParams().add(parseDeviceData(item));
                }
            }
        }
        return eventData;
    }

    private DeviceServiceResData parseServiceResData(Value serviceResValue) {
        String identifier = asString(firstMember(serviceResValue, "serviceIdentifier", "identifier", "id", "code"));
        DeviceServiceResData serviceResData = new DeviceServiceResData(identifier);

        Value resultDataValue = firstMember(serviceResValue, "resultData", "results", "params", "dataList");
        if (hasValue(resultDataValue) && resultDataValue.hasArrayElements()) {
            long length = resultDataValue.getArraySize();
            for (long i = 0; i < length; i++) {
                Value item = resultDataValue.getArrayElement(i);
                if (item != null && item.hasMembers()) {
                    serviceResData.getResultData().add(parseDeviceData(item));
                }
            }
        }
        return serviceResData;
    }

    /**
     * 转换 Value 为 Java 对象
     */
    private Object convertValue(Value value) {
        if (!hasValue(value)) {
            return null;
        }
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (value.isNumber()) {
            double doubleValue = value.asDouble();
            if (Math.rint(doubleValue) == doubleValue && value.fitsInLong()) {
                return value.asLong();
            }
            return doubleValue;
        }
        if (value.isString()) {
            return value.asString();
        }
        if (value.hasArrayElements()) {
            long size = value.getArraySize();
            Object[] array = new Object[(int) size];
            for (int i = 0; i < size; i++) {
                array[i] = convertValue(value.getArrayElement(i));
            }
            return array;
        }
        if (value.hasMembers()) {
            Map<String, Object> map = new HashMap<>();
            for (String key : value.getMemberKeys()) {
                map.put(key, convertValue(value.getMember(key)));
            }
            return map;
        }
        return value.toString();
    }

    private Value toJavaScriptValue(Object value) throws JsonProcessingException {
        if (value == null) {
            return context.eval("js", "null");
        }
        return context.eval("js", "JSON.parse(" + OBJECT_MAPPER.writeValueAsString(OBJECT_MAPPER.writeValueAsString(value)) + ")");
    }

    private boolean hasValue(Value value) {
        return value != null && !value.isNull();
    }

    private Value firstMember(Value value, String... names) {
        if (value == null || !value.hasMembers()) {
            return null;
        }
        for (String name : names) {
            Value member = value.getMember(name);
            if (hasValue(member)) {
                return member;
            }
        }
        return null;
    }

    private String asString(Value value) {
        if (!hasValue(value)) {
            return null;
        }
        Object converted = convertValue(value);
        return converted == null ? null : String.valueOf(converted);
    }

    private Integer toInteger(Value value) {
        if (!hasValue(value)) {
            return null;
        }
        if (value.isNumber()) {
            return value.asInt();
        }
        return Integer.parseInt(value.asString());
    }

    private EventTypeEnum parseEventType(Value eventTypeValue) {
        if (!hasValue(eventTypeValue)) {
            return EventTypeEnum.INFO;
        }
        if (eventTypeValue.isNumber()) {
            return EventTypeEnum.of(eventTypeValue.asInt());
        }
        String value = eventTypeValue.asString();
        if (value == null || value.isBlank()) {
            return EventTypeEnum.INFO;
        }
        try {
            return EventTypeEnum.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            try {
                return EventTypeEnum.of(Integer.parseInt(value));
            } catch (NumberFormatException ignored) {
                return EventTypeEnum.INFO;
            }
        }
    }

    /**
     * 解析数据类型
     */
    private DataTypeEnum parseDataType(String typeStr) {
        if (typeStr == null || typeStr.isEmpty()) {
            return DataTypeEnum.TEXT;
        }

        return switch (typeStr.toUpperCase()) {
            case "INT", "INTEGER" -> DataTypeEnum.INT;
            case "FLOAT" -> DataTypeEnum.FLOAT;
            case "DOUBLE" -> DataTypeEnum.DOUBLE;
            case "BOOLEAN", "BOOL" -> DataTypeEnum.BOOL;
            case "ENUM" -> DataTypeEnum.ENUM;
            case "DATE", "DATETIME", "TIMESTAMP" -> DataTypeEnum.DATE;
            case "STRUCT", "OBJECT", "JSON" -> DataTypeEnum.STRUCT;
            default -> DataTypeEnum.TEXT;
        };
    }

    @Override
    public EncoderResult encode(EncoderMessage message, TslModel tslModel) throws ProtocolException {
        if (context == null) {
            throw new ProtocolException("encode", ExceptionType.SYSTEM_ERROR, -1,
                    "脚本引擎未初始化: " + scriptType.getDisplayName());
        }
        if (encodeFunction == null || !encodeFunction.canExecute()) {
            throw new ProtocolException("encode", ExceptionType.SYSTEM_ERROR, -1,
                    "脚本协议未定义 encode(message, tslModel) 函数: " + protocolKey);
        }

        try {
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("identifier", message.getIdentifier());
            inputData.put("deviceKey", message.getDeviceKey());
            inputData.put("productKey", message.getProductKey());
            inputData.put("params", message.getParams());

            Value result = encodeFunction.execute(
                    toJavaScriptValue(inputData),
                    toJavaScriptValue(tslModel)
            );

            return parseEncodeResult(result);

        } catch (ProtocolException e) {
            throw e;
        } catch (PolyglotException e) {
            log.error("脚本编码失败: {}, 错误: {}", protocolKey, e.getMessage(), e);
            throw new ProtocolException("encode", ExceptionType.SYSTEM_ERROR, -1,
                    "脚本编码失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("脚本编码结果解析失败: {}", protocolKey, e);
            throw new ProtocolException("encode", ExceptionType.INVALID_PARAM, -1,
                    "脚本编码结果解析失败: " + e.getMessage());
        }
    }

    private EncoderResult parseEncodeResult(Value result) throws ProtocolException {
        if (result == null || result.isNull() || !result.hasMembers()) {
            throw new ProtocolException("encode", ExceptionType.INVALID_PARAM, -1, "encode 返回值必须是对象");
        }

        EncoderResult encoderResult = new EncoderResult();

        Value messageIdValue = result.getMember("messageId");
        if (hasValue(messageIdValue)) {
            encoderResult.setMessageId(toInteger(messageIdValue));
        }

        Value needReplyValue = result.getMember("needReply");
        if (hasValue(needReplyValue)) {
            encoderResult.setNeedReply(needReplyValue.asBoolean());
        }

        Value messageValue = result.getMember("message");
        if (hasValue(messageValue)) {
            Object message = convertValue(messageValue);
            if (message instanceof byte[] bytes) {
                encoderResult.setMessage(bytes);
            } else if (message instanceof Object[] array) {
                byte[] bytes = new byte[array.length];
                for (int i = 0; i < array.length; i++) {
                    bytes[i] = ((Number) array[i]).byteValue();
                }
                encoderResult.setMessage(bytes);
            } else {
                encoderResult.setMessage(String.valueOf(message).getBytes(StandardCharsets.UTF_8));
            }
        }

        Value metadataValue = result.getMember("metadata");
        if (hasValue(metadataValue) && metadataValue.hasMembers()) {
            Map<String, Object> metadata = new HashMap<>();
            for (String key : metadataValue.getMemberKeys()) {
                metadata.put(key, convertValue(metadataValue.getMember(key)));
            }
            encoderResult.setMetadata(metadata);
        }

        return encoderResult;
    }

    @Override
    public void responseError(DeviceConnection connection, ProtocolException e) {
        log.error("协议错误响应: {} - {}", protocolKey, e.getMessage());
        // 可以在这里实现错误响应的脚本处理
    }

    /**
     * 关闭引擎，释放资源
     */
    public void close() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    public ScriptType getScriptType() {
        return scriptType;
    }

    public String getProtocolName() {
        return protocolName;
    }
}
