package com.github.dingdaoyi.driver.tcp.core;

import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import java.util.*;

/**
 * 动态加载所有 DeviceKeyParser 实现（SPI方式）
 * @author dingyunwei
 */
public class DeviceKeyParserLoader {
    private static final Map<String, DeviceKeyParser> PARSER_MAP = new HashMap<>();
    static {
        ServiceLoader<DeviceKeyParser> loader = ServiceLoader.load(DeviceKeyParser.class);
        for (DeviceKeyParser parser : loader) {
            PARSER_MAP.put(parser.driverName(), parser);
        }
    }
    public static DeviceKeyParser getParser(String driverName) {
        return PARSER_MAP.get(driverName);
    }
    public static Collection<DeviceKeyParser> allParsers() {
        return PARSER_MAP.values();
    }
    public static Map<String, DeviceKeyParser> getParserMap() {
        return PARSER_MAP;
    }
} 