package com.github.dingdaoyi.driver.mqtt.model;

import com.github.dingdaoyi.driver.DriverConfig;
import com.github.dingdaoyi.driver.mqtt.MqttTopicConstants;
import com.github.dingdaoyi.proto.model.ProtoMessageType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ResourceLock("MqttTopicConstants")
class MqttTopicTest {

    private Map<String, String> originalTopicMap;
    private Pattern originalTopicParsePattern;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUpTopicPattern() {
        originalTopicMap = new HashMap<>((Map<String, String>) ReflectionTestUtils.getField(MqttTopicConstants.class, "TOPIC_MAP"));
        originalTopicParsePattern = MqttTopicConstants.TOPIC_PARSE_PATTERN;

        DriverConfig.MqttConfigProperties mqttConfig = new DriverConfig.MqttConfigProperties();
        mqttConfig.setTopicPrefix("iot");
        DriverConfig driverConfig = mock(DriverConfig.class);
        when(driverConfig.getMqtt()).thenReturn(mqttConfig);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBean(DriverConfig.class)).thenReturn(driverConfig);

        new MqttTopicConstants().setApplicationContext(applicationContext);
    }

    @AfterEach
    @SuppressWarnings("unchecked")
    void restoreTopicPattern() {
        Map<String, String> topicMap = (Map<String, String>) ReflectionTestUtils.getField(MqttTopicConstants.class, "TOPIC_MAP");
        topicMap.clear();
        topicMap.putAll(originalTopicMap);
        MqttTopicConstants.TOPIC_PARSE_PATTERN = originalTopicParsePattern;
    }

    @Test
    void parseReturnsPropertyTopicWhenKnownMessageTypeMatchesPrefix() {
        Optional<MqttTopic> topic = MqttTopic.parse("iot/pro/PK001");

        assertThat(topic).isPresent();
        assertThat(topic.get().getMessageType()).isEqualTo(ProtoMessageType.PROPERTY);
        assertThat(topic.get().getProductKey()).isEqualTo("PK001");
    }

    @Test
    void parseAllowsHyphenatedProductKeys() {
        Optional<MqttTopic> topic = MqttTopic.parse("iot/ev/demo-smart-sensor");

        assertThat(topic).isPresent();
        assertThat(topic.get().getMessageType()).isEqualTo(ProtoMessageType.EVENT);
        assertThat(topic.get().getProductKey()).isEqualTo("demo-smart-sensor");
    }

    @Test
    void parseRejectsUnknownMessageTypeInsteadOfReturningNullType() {
        Optional<MqttTopic> topic = MqttTopic.parse("iot/unknown/PK001");

        assertThat(topic).isEmpty();
    }

    @Test
    void getTopicAppliesConfiguredPrefix() {
        String topic = MqttTopicConstants.getTopic(MqttTopicConstants.PROPERTY_TOPIC, "PK001");

        assertThat(topic).isEqualTo("iot/pro/PK001");
    }
}
