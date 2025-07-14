package com.github.dingdaoyi.driver.tcp.core;

import com.github.dingdaoyi.core.driver.DeviceKeyParser;
import com.github.dingdaoyi.core.service.DeviceProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 动态加载所有 DeviceKeyParser 实现（SPI方式）
 * @author dingyunwei
 */
@Component
public class DeviceKeyParserLoader implements ApplicationContextAware {
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

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        DeviceProvider deviceProvider = context.getBean(DeviceProvider.class);
        for (DeviceKeyParser parser : allParsers()) {
            parser.setDeviceProvider(deviceProvider);
        }
    }
}