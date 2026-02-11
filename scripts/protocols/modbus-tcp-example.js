/**
 * Modbus TCP 协议脚本示例
 * 演示如何使用 JavaScript 编写物联网设备协议解析器
 *
 * 数据库配置示例：
 * - name: Modbus TCP
 * - proto_type: 3 (JAVASCRIPT)
 * - url: (可选) 脚本文件路径
 * - mark: (脚本内容，优先从此读取)
 * - proto_key: modbus-tcp
 * - handler_class: (可为空)
 */

// ==================== 解码函数 ====================
/**
 * 解码设备上报的数据
 *
 * @param {Object} request - 请求对象
 * @param {string} request.deviceKey - 设备key
 * @param {string} request.messageType - 消息类型: PROPERTY/EVENT/SERVICE_RES
 * @param {string} request.data - 原始数据 (Base64 或十六进制字符串)
 * @param {Object} tslModel - 物模型
 * @returns {Object} 解码结果
 */
exports.decode = function(request, tslModel) {
    // 解析原始数据
    const hexData = request.data; // 假设是十六进制字符串
    const deviceKey = request.deviceKey;
    const messageType = request.messageType;

    // 解析 Modbus TCP 帧结构
    // 示例: 00 01 00 00 00 06 01 03 00 00 00 02
    // 事务ID(2) + 协议ID(2) + 长度(2) + 单元ID(1) + 功能码(1) + 数据

    let offset = 0;
    const transactionId = parseInt(hexData.substr(offset, 4), 16);
    offset += 4;
    const protocolId = parseInt(hexData.substr(offset, 4), 16);
    offset += 4;
    const length = parseInt(hexData.substr(offset, 4), 16);
    offset += 4;
    const unitId = parseInt(hexData.substr(offset, 2), 16);
    offset += 2;
    const functionCode = parseInt(hexData.substr(offset, 2), 16);
    offset += 2;

    const result = {
        messageId: transactionId,
        rawData: hexData,
        dataList: []
    };

    // 根据功能码处理数据
    switch (functionCode) {
        case 0x03: // 读保持寄存器
        case 0x04: // 读输入寄存器
            const byteCount = parseInt(hexData.substr(offset, 2), 16);
            offset += 2;
            const registerCount = byteCount / 2;

            for (let i = 0; i < registerCount; i++) {
                const registerAddr = i; // 实际应根据起始地址
                const value = parseInt(hexData.substr(offset, 4), 16);
                offset += 4;

                // 查找物模型中对应的属性
                const identifier = `register_${registerAddr}`;

                result.dataList.push({
                    identifier: identifier,
                    type: 'INT',
                    value: value
                });
            }
            break;

        case 0x01: // 读线圈
        case 0x02: // 读离散输入
            const coilCount = parseInt(hexData.substr(offset, 2), 16);
            offset += 2;

            for (let i = 0; i < coilCount; i++) {
                const byteIndex = Math.floor(i / 8);
                const bitIndex = i % 8;
                const byteValue = parseInt(hexData.substr(offset + byteIndex * 2, 2), 16);
                const value = (byteValue >> bitIndex) & 0x01;

                result.dataList.push({
                    identifier: `coil_${i}`,
                    type: 'BOOLEAN',
                    value: value === 1
                });
            }
            break;

        default:
            // 未知功能码，返回原始数据
            break;
    }

    return result;
};

// ==================== 编码函数 ====================
/**
 * 编码下发给设备的命令
 *
 * @param {Object} message - 命令消息
 * @param {string} message.identifier - 功能/属性标识符
 * @param {Object} message.params - 参数对象
 * @param {Object} tslModel - 物模型
 * @returns {Object} 编码结果
 */
exports.encode = function(message, tslModel) {
    const identifier = message.identifier;
    const params = message.params;

    // 构建 Modbus TCP 写命令
    // 示例: 写单个寄存器 (功能码 0x06)
    const transactionId = Math.floor(Math.random() * 65535);
    const unitId = params.unitId || 1;
    const functionCode = 0x06; // 写单个保持寄存器
    const registerAddr = params.registerAddr || 0;
    const value = params.value || 0;

    // 构建十六进制帧
    let frame = '';
    frame += transactionId.toString(16).padStart(4, '0');
    frame += '0000'; // 协议ID
    frame += '0006'; // 长度
    frame += unitId.toString(16).padStart(2, '0');
    frame += functionCode.toString(16).padStart(2, '0');
    frame += registerAddr.toString(16).padStart(4, '0');
    frame += value.toString(16).padStart(4, '0');

    return {
        messageId: transactionId,
        message: frame,
        metadata: {
            topic: `/device/${params.productKey}/${params.deviceKey}/command`
        }
    };
};

// ==================== 辅助函数 ====================

/**
 * 字节数组转十六进制字符串
 */
function bytesToHex(bytes) {
    return Array.from(bytes)
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');
}

/**
 * 十六进制字符串转字节数组
 */
function hexToBytes(hex) {
    const bytes = [];
    for (let i = 0; i < hex.length; i += 2) {
        bytes.push(parseInt(hex.substr(i, 2), 16));
    }
    return bytes;
}

/**
 * CRC16 校验计算 (Modbus 标准)
 */
function calculateCRC16(data) {
    let crc = 0xFFFF;
    for (let i = 0; i < data.length; i++) {
        crc ^= data[i];
        for (let j = 0; j < 8; j++) {
            if (crc & 0x0001) {
                crc = (crc >> 1) ^ 0xA001;
            } else {
                crc >>= 1;
            }
        }
    }
    return crc;
}
