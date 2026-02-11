/**
 * JSON 格式协议示例
 * 适用于大多数使用 JSON 格式的物联网设备
 *
 * 设备上报格式:
 * {
 *   "msgId": 123,
 *   "data": {
 *     "temperature": 25.5,
 *     "humidity": 60,
 *     "status": true
 *   },
 *   "timestamp": 1234567890
 * }
 *
 * 数据库配置示例：
 * - name: JSON 简单协议
 * - proto_type: 3 (JAVASCRIPT)
 * - proto_key: json-simple
 * - mark: <脚本内容>
 */

// ==================== 解码函数 ====================
/**
 * 解码设备上报的 JSON 数据
 */
exports.decode = function(request, tslModel) {
    try {
        // 解析 JSON 数据
        const payload = JSON.parse(request.data);
        const deviceKey = request.deviceKey;
        const messageType = request.messageType;

        const result = {
            messageId: payload.msgId || 0,
            rawData: request.data,
            dataList: []
        };

        // 根据消息类型处理
        switch (messageType) {
            case 'PROPERTY':
                // 属性上报
                if (payload.data) {
                    for (const key in payload.data) {
                        const value = payload.data[key];
                        const type = getValueType(value);

                        result.dataList.push({
                            identifier: key,
                            type: type,
                            value: value
                        });
                    }
                }
                break;

            case 'EVENT':
                // 事件上报
                if (payload.event) {
                    result.eventData = {
                        eventIdentifier: payload.event,
                        eventType: payload.eventType || 'INFO',
                        params: []
                    };

                    if (payload.params) {
                        for (const key in payload.params) {
                            const value = payload.params[key];
                            const type = getValueType(value);

                            result.dataList.push({
                                identifier: key,
                                type: type,
                                value: value
                            });

                            result.eventData.params.push({
                                identifier: key,
                                type: type,
                                value: value
                            });
                        }
                    }
                }
                break;

            case 'SERVICE_RES':
                // 服务响应
                if (payload.serviceId) {
                    result.serviceResData = {
                        serviceIdentifier: payload.serviceId,
                        resultData: []
                    };

                    if (payload.output) {
                        for (const key in payload.output) {
                            const value = payload.output[key];
                            const type = getValueType(value);

                            result.dataList.push({
                                identifier: key,
                                type: type,
                                value: value
                            });

                            result.serviceResData.resultData.push({
                                identifier: key,
                                type: type,
                                value: value
                            });
                        }
                    }
                }
                break;
        }

        return result;

    } catch (e) {
        // JSON 解析失败
        return {
            messageId: 0,
            rawData: request.data,
            dataList: []
        };
    }
};

// ==================== 编码函数 ====================
/**
 * 编码下发给设备的 JSON 命令
 */
exports.encode = function(message, tslModel) {
    const msgId = generateMessageId();
    const timestamp = Date.now();

    const payload = {
        msgId: msgId,
        timestamp: timestamp,
        method: 'thing.service.' + message.identifier,
        params: message.params || {}
    };

    return {
        messageId: msgId,
        message: JSON.stringify(payload),
        metadata: {
            topic: `/sys/${message.productKey}/${message.deviceKey}/thing/service/` + message.identifier
        }
    };
};

// ==================== 辅助函数 ====================

/**
 * 根据值推断数据类型
 */
function getValueType(value) {
    if (typeof value === 'boolean') {
        return 'BOOLEAN';
    }
    if (typeof value === 'number') {
        return Number.isInteger(value) ? 'INT' : 'DOUBLE';
    }
    if (typeof value === 'object') {
        return 'JSON';
    }
    return 'STRING';
}

/**
 * 生成消息ID
 */
function generateMessageId() {
    return Math.floor(Math.random() * 1000000);
}

/**
 * 合并默认配置
 */
function mergeConfig(defaultConfig, userConfig) {
    return Object.assign({}, defaultConfig, userConfig);
}

// 导出工具函数供外部使用
exports.utils = {
    getValueType: getValueType,
    generateMessageId: generateMessageId,
    mergeConfig: mergeConfig
};
