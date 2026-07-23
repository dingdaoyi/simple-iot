package com.github.dingdaoyi.driver.modbus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Modbus TCP 帧编解码 (MBAP + PDU)
 * ponytail: 手写不引依赖，只支持 Read Holding/Input Registers (fc 3/4)
 */
public final class ModbusFrame {

    private static int txId = 0;

    /** Build read request: txId | protoId(0) | len | unitId | fc | startAddr | qty */
    public static byte[] readRequest(int unitId, int function, int address, int count) {
        int id = txId++ & 0xFFFF;
        ByteBuffer buf = ByteBuffer.allocate(12).order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) id);      // transaction id
        buf.putShort((short) 0);       // protocol id
        buf.putShort((short) 6);       // length
        buf.put((byte) unitId);        // unit id
        buf.put((byte) function);      // function code
        buf.putShort((short) address); // start address
        buf.putShort((short) count);   // quantity
        return buf.array();
    }

    /** Parse response: extract register values */
    public static int[] parseResponse(byte[] resp) {
        if (resp.length < 9) throw new IllegalArgumentException("Modbus response too short");
        int byteCount = resp[8] & 0xFF;
        int regCount = byteCount / 2;
        int[] regs = new int[regCount];
        for (int i = 0; i < regCount; i++) {
            regs[i] = ((resp[9 + i*2] & 0xFF) << 8) | (resp[10 + i*2] & 0xFF);
        }
        return regs;
    }

    /** Convert register(s) to value with scale */
    public static Object toValue(int[] regs, int offset, int count, String dataType, double scale) {
        return switch (dataType.toLowerCase()) {
            case "int16" -> scale(regs[offset], scale);
            case "int32" -> scale(((regs[offset+1] & 0xFFFF) << 16) | (regs[offset] & 0xFFFF), scale);
            case "float32" -> scale(Float.intBitsToFloat(((regs[offset] & 0xFFFF) << 16) | (regs[offset+1] & 0xFFFF)), scale);
            case "float64" -> scale(Double.longBitsToDouble(((long)(regs[offset] & 0xFFFF) << 48) | ((long)(regs[offset+1] & 0xFFFF) << 32) | ((long)(regs[offset+2] & 0xFFFF) << 16) | (regs[offset+3] & 0xFFFF)), scale);
            case "bool" -> regs[offset] != 0;
            default -> scale(regs[offset], scale);
        };
    }

    private static Object scale(double raw, double scale) {
        if (scale == 1.0) return raw;
        return raw * scale;
    }
}
