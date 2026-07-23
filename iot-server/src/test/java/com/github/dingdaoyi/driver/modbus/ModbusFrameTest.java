package com.github.dingdaoyi.driver.modbus;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

class ModbusFrameTest {

    @Test
    void readRequest_hasCorrectStructure() {
        byte[] req = ModbusFrame.readRequest(1, 3, 0, 2);
        assertEquals(12, req.length);
        // unit id at offset 6
        assertEquals(1, req[6]);
        // function code at offset 7
        assertEquals(3, req[7]);
        // start address at offset 8-9
        assertEquals(0, ByteBuffer.wrap(req, 8, 2).getShort());
        // quantity at offset 10-11
        assertEquals(2, ByteBuffer.wrap(req, 10, 2).getShort());
    }

    @Test
    void parseResponse_int16() {
        // MBAP header(7) + byteCount(1) + 2 regs
        ByteBuffer buf = ByteBuffer.allocate(13).order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) 1);  // tx id
        buf.putShort((short) 0);  // proto id
        buf.putShort((short) 5);  // length
        buf.put((byte) 1);        // unit id
        buf.put((byte) 3);        // function code
        buf.put((byte) 4);        // byte count
        buf.putShort((short) 100); // reg 0
        buf.putShort((short) 200); // reg 1
        int[] regs = ModbusFrame.parseResponse(buf.array());
        assertEquals(2, regs.length);
        assertEquals(100, regs[0]);
        assertEquals(200, regs[1]);
    }

    @Test
    void toValue_int16_withScale() {
        int[] regs = {250};
        Object val = ModbusFrame.toValue(regs, 0, 1, "int16", 0.1);
        assertEquals(25.0, val);
    }

    @Test
    void toValue_int32() {
        int[] regs = {0x0001, 0x0002};
        // low word first: (0x0002 << 16) | 0x0001 = 131073
        Object val = ModbusFrame.toValue(regs, 0, 2, "int32", 1.0);
        assertEquals(131073.0, val);
    }

    @Test
    void toValue_bool() {
        int[] regs = {0};
        Object val = ModbusFrame.toValue(regs, 0, 1, "bool", 1.0);
        assertEquals(false, val);
        int[] regs2 = {1};
        Object val2 = ModbusFrame.toValue(regs2, 0, 1, "bool", 1.0);
        assertEquals(true, val2);
    }
}
