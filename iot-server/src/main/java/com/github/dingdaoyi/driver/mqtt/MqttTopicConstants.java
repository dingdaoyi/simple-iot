package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.driver.DriverConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * mqtttopic
 *
 * @author dingyunwei
 */
@Component
@Slf4j
public class MqttTopicConstants implements ApplicationContextAware {

    private final static Map<String, String> TOPIC_MAP = new HashMap<>();
    /**
     * 事件上报
     */
    public static String EVENT_TOPIC = "ev/{productKey}/{identifier}";


    /**
     * 指令下发
     */
    public static String COMMAND_TOPIC = "cam/{productKey}/{identifier}";

    /**
     * 设备指令回复
     */
    public static String COMMAND_RES_TOPIC = "cam_res/{productKey}/{identifier}";

    /**
     * 上报属性
     */
    public static String PROPERTY_TOPIC = "pro/{productKey}/{identifier}";

    public static Pattern TOPIC_PARSE_PATTERN;

    /**
     * 暂时未校验空值
     *
     * @param topicName
     * @param productKey
     * @return
     */
    public static String getTopic(String topicName, String productKey) {
        return TOPIC_MAP.get(topicName).replace("productKey", productKey);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        DriverConfig driverConfig = applicationContext.getBean(DriverConfig.class);
        DriverConfig.MqttConfigProperties mqtt = driverConfig.getMqtt();
        String topicPrefix = mqtt.getTopicPrefix();
        if (StringUtils.isNotBlank(topicPrefix)) {
            TOPIC_MAP.put(EVENT_TOPIC, topicPrefix + "/" + EVENT_TOPIC);
            TOPIC_MAP.put(COMMAND_TOPIC, topicPrefix + "/" + COMMAND_TOPIC);
            TOPIC_MAP.put(PROPERTY_TOPIC, topicPrefix + "/" + PROPERTY_TOPIC);
            TOPIC_MAP.put(COMMAND_RES_TOPIC, topicPrefix + "/" + COMMAND_RES_TOPIC);
            TOPIC_PARSE_PATTERN = Pattern.compile("^" + topicPrefix + "/(\\w*)/(\\w*)/(\\w*)$");
        } else {
            TOPIC_MAP.put(EVENT_TOPIC, EVENT_TOPIC);
            TOPIC_MAP.put(COMMAND_TOPIC, COMMAND_TOPIC);
            TOPIC_MAP.put(PROPERTY_TOPIC, PROPERTY_TOPIC);
            TOPIC_MAP.put(COMMAND_RES_TOPIC, COMMAND_RES_TOPIC);
            TOPIC_PARSE_PATTERN = Pattern.compile("^(\\w*)/(\\w*)/(\\w*)$");
        }
        log.info("MqttTopicConstants 初始化成功!");
    }
}
