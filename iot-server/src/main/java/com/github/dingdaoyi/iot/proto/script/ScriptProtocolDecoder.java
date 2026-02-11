package com.github.dingdaoyi.iot.proto.script;

import com.github.dingdaoyi.iot.proto.impl.ScriptProtocolInitialize.ScriptType;
import com.github.dingdaoyi.proto.inter.DeviceConnection;
import com.github.dingdaoyi.proto.model.*;
import com.github.dingdaoyi.proto.inter.ProtocolDecoder;
import com.github.dingdaoyi.proto.model.tsl.TslModel;
import lombok.extern.slf4j.Slf4j;
import org.graalvm.polyglot.*;

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
            // 创建 GraalVM Context
            context = Context.newBuilder("js")
                    .allowAllAccess(true)
                    .build();

            // 创建 exports 对象
            Value exports = context.eval("js", "({ decode: null, encode: null })");

            // 执行脚本，将函数定义到 exports 对象中
            // 包装脚本：用 (function(exports) { ... })(exports) 形式
            String wrappedScript = "(function(exports) {\n" + scriptContent + "\n return exports;\n})(" + exports + ")";

            Value result = context.eval("js", wrappedScript);

            // 从返回的 exports 对象中获取函数
            if (result.hasMembers()) {
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
        try {
            context = Context.newBuilder("js")
                    .allowAllAccess(true)
                    .build();

            // Groovy 需要使用 JSR-223 API，这里先用 JavaScript 兼容模式
            log.warn("Groovy 引擎暂未完全实现，使用 JavaScript 兼容模式: {}", protocolKey);
            initJavaScriptEngine();

        } catch (Exception e) {
            log.error("Groovy 引擎初始化失败: {}", protocolKey, e);
        }
    }

    @Override
    public String protocolKey() {
        return protocolKey;
    }

    @Override
    public DecodeResult decode(DeviceRequest request, TslModel tslModel) throws ProtocolException {
        if (context == null) {
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.SYSTEM_ERROR,-1,
                "脚本引擎未初始化: " + scriptType.getDisplayName());
        }

        try {
            // 构建输入参数
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("deviceKey", request.getDeviceKey());
            inputData.put("messageType", request.getMessageType().name());
            inputData.put("data", request.getData() != null ? new String(request.getData()) : "");

            // 调用 decode 函数
            Value result;
            if (decodeFunction != null && decodeFunction.canExecute()) {
                result = decodeFunction.execute(
                        toValue(context, inputData),
                        toValue(context, tslModel)
                );
            } else {
                // 直接执行脚本
                result = context.eval("js", scriptContent);
            }

            if (result == null || result.isNull()) {
                throw new ProtocolException(request.getDeviceKey(), ExceptionType.INVALID_PARAM, -1,
                    "脚本未返回解析结果");
            }

            return parseScriptResult(result, request);

        } catch (PolyglotException e) {
            log.error("脚本执行失败: {}, 错误: {}", protocolKey, e.getMessage(), e);
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.SYSTEM_ERROR, -1,
                "脚本执行失败: " + e.getMessage());
        }
    }

    /**
     * 解析脚本返回结果
     */
    private DecodeResult parseScriptResult(Value result, DeviceRequest request) throws ProtocolException {
        DecodeResult decodeResult = new DecodeResult();

        try {
            // 检查结果是否是对象
            if (result.hasMembers()) {
                // 提取 messageId
                Value messageIdValue = result.getMember("messageId");
                if (!messageIdValue.isNull()) {
                    decodeResult.setMessageId(messageIdValue.asInt());
                }

                // 提取 rawData
                Value rawDataValue = result.getMember("rawData");
                if (!rawDataValue.isNull()) {
                    decodeResult.setRowData(rawDataValue.asString());
                }

                // 提取 dataList
                Value dataListValue = result.getMember("dataList");
                if (!dataListValue.isNull() && dataListValue.hasArrayElements()) {
                    long length = dataListValue.getArraySize();
                    for (long i = 0; i < length; i++) {
                        Value item = dataListValue.getArrayElement(i);
                        if (item.hasMembers()) {
                            String identifier = item.getMember("identifier").asString();
                            Value valueValue = item.getMember("value");
                            Object value = convertValue(valueValue);

                            String typeStr = "STRING";
                            Value typeValue = item.getMember("type");
                            if (!typeValue.isNull()) {
                                typeStr = typeValue.asString();
                            }

                            DataTypeEnum dataType = parseDataType(typeStr);
                            decodeResult.getDataList().add(new DeviceData(identifier, dataType, value));
                        }
                    }
                }

                // 提取 eventData (如果有)
                Value eventDataValue = result.getMember("eventData");
                if (!eventDataValue.isNull()) {
                    // 处理事件数据
                    log.debug("检测到事件数据");
                }

                // 提取 serviceResData (如果有)
                Value serviceResValue = result.getMember("serviceResData");
                if (!serviceResValue.isNull()) {
                    // 处理服务响应数据
                    log.debug("检测到服务响应数据");
                }
            }

        } catch (Exception e) {
            log.error("解析脚本结果失败: {}", protocolKey, e);
            throw new ProtocolException(request.getDeviceKey(), ExceptionType.INVALID_PARAM, -1,
                "解析脚本结果失败: " + e.getMessage());
        }

        return decodeResult;
    }

    /**
     * 转换 Value 为 Java 对象
     */
    private Object convertValue(Value value) {
        if (value.isNull()) {
            return null;
        }
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        if (value.isNumber()) {
            // 尝试返回合适的数值类型
            long longValue = value.asLong();
            if (longValue == value.asDouble()) {
                return longValue;
            }
            return value.asDouble();
        }
        if (value.isString()) {
            return value.asString();
        }
        if (value.hasArrayElements()) {
            // 处理数组
            long size = value.getArraySize();
            Object[] array = new Object[(int) size];
            for (int i = 0; i < size; i++) {
                array[i] = convertValue(value.getArrayElement(i));
            }
            return array;
        }
        if (value.hasMembers()) {
            // 处理对象
            Map<String, Object> map = new HashMap<>();
            for (String key : value.getMemberKeys()) {
                map.put(key, convertValue(value.getMember(key)));
            }
            return map;
        }
        return value.asString();
    }

    /**
     * 将 Java 对象转换为 Value
     */
    private Value toValue(Context context, Object obj) {
        if (obj == null) {
            return context.asValue(null);
        }
        if (obj instanceof Value) {
            return (Value) obj;
        }
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            return context.asValue(obj);
        }
        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                result.put(entry.getKey(), convertToJava(entry.getValue()));
            }
            return context.asValue(result);
        }
        return context.asValue(obj);
    }

    /**
     * 转换为纯 Java 对象
     */
    private Object convertToJava(Object obj) {
        if (obj == null || obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            return obj;
        }
        return obj;
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

        try {
            // 构建输入参数
            Map<String, Object> inputData = new HashMap<>();
            inputData.put("identifier", message.getIdentifier());
            inputData.put("params", message.getParams());

            // 调用 encode 函数
            Value result;
            if (encodeFunction != null && encodeFunction.canExecute()) {
                result = encodeFunction.execute(
                        toValue(context, inputData),
                        toValue(context, tslModel)
                );
            } else {
                // 直接执行脚本（需要脚本支持 encode 函数）
                result = context.eval("js", scriptContent);
            }

            EncoderResult encoderResult = new EncoderResult();

            if (result != null && !result.isNull() && result.hasMembers()) {
                // 提取 messageId
                Value messageIdValue = result.getMember("messageId");
                if (!messageIdValue.isNull()) {
                    encoderResult.setMessageId(messageIdValue.asInt());
                }

                // 提取 message
                Value messageValue = result.getMember("message");
                if (!messageValue.isNull()) {
                    if (messageValue.isString()) {
                        encoderResult.setMessage(messageValue.asString().getBytes());
                    }
                }

                // 提取 metadata
                Value metadataValue = result.getMember("metadata");
                if (!metadataValue.isNull() && metadataValue.hasMembers()) {
                    Map<String, Object> metadata = new HashMap<>();
                    for (String key : metadataValue.getMemberKeys()) {
                        metadata.put(key, convertValue(metadataValue.getMember(key)));
                    }
                    encoderResult.setMetadata(metadata);
                }
            }

            return encoderResult;

        } catch (PolyglotException e) {
            log.error("脚本编码失败: {}, 错误: {}", protocolKey, e.getMessage(), e);
            throw new ProtocolException("encode", ExceptionType.SYSTEM_ERROR, -1,
                "脚本编码失败: " + e.getMessage());
        }
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
