package com.github.dingdaoyi.proto.utils;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 通用编解码工具类
 * 提供字节流处理、进制转换、校验等常用方法。
 */
public final class CodecUtil {

    private CodecUtil() {
        // 私有构造器防止实例化
    }

    private static final String IP_REGEX = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." 
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

    /**
     * 从字节数组中读取指定长度的字符串。
     */
    public static String readStr(byte[] data, int offset, int length, Charset charset) {
        byte[] read = new byte[length];
        System.arraycopy(data, offset, read, 0, length);
        return new String(read, charset).trim().split("\u0000")[0];
    }

    /**
     * 从字节数组中读取指定长度的16进制字符串。
     */
    public static String readHexStr(byte[] data, int offset, int length) {
        byte[] read = new byte[length];
        System.arraycopy(data, offset, read, 0, length);
        return bytesToHex(read).toUpperCase(Locale.ROOT);
    }


    /**
     * 判断字符串是否为有效IP。
     */
    public static boolean isValidIP(String ip) {
        return ip != null && !ip.isEmpty() && ip.matches(IP_REGEX);
    }

    /**
     * 判断字符串是否为有效16进制字符串。
     */
    public static boolean isHexStr(String str) {
        return str.matches("^[0-9A-Fa-f]+$");
    }

    /**
     * 转换整数为补零的二进制字符串。
     */
    public static String intToBinary(int num, int bitNum) {
        String binaryStr = Integer.toBinaryString(num);
        return String.format("%" + bitNum + "s", binaryStr).replace(' ', '0');
    }


    /**
     * 将字节数组转换为16进制字符串。
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexStr = new StringBuilder();
        for (byte b : bytes) {
            hexStr.append(String.format("%02x", b));
        }
        return hexStr.toString();
    }

    /**
     * 将16进制字符串转换为字节数组。
     */
    public static byte[] hexToBytes(String hexStr) {
        int len = hexStr.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                    + Character.digit(hexStr.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 验证字符串是否为Base64编码。
     */
    public static boolean isBase64(String str) {
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";
        return Pattern.matches(base64Pattern, str);
    }

    /**
     * 校验和计算（加法校验）。
     */
    public static int checkSum(byte[] data) {
        long checksum = 0;
        for (byte b : data) {
            checksum += b & 0xFF;
        }
        return (int) (checksum & 0xFF);
    }
}