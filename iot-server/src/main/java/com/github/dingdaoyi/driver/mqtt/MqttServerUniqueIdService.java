package com.github.dingdaoyi.driver.mqtt;

import com.github.dingdaoyi.driver.DriverConfig;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.dromara.mica.mqtt.core.server.auth.IMqttServerUniqueIdService;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

/**
 * @author dingyunwei
 */
@Component
public class MqttServerUniqueIdService implements IMqttServerUniqueIdService {

    private String  mqttClientIdPrefix;
    public final static String ERROR_UNIQUE_ID = "INVALID_DEVICE";

    @Resource
    public void initMqttClientIdPrefix(DriverConfig driverConfig) {
        mqttClientIdPrefix = driverConfig.getMqtt().getMqttClientIdPrefix();
    }
    @Override
    public String getUniqueId(ChannelContext context, String clientId, String userName, String password) {
        if (StringUtils.isNotBlank(mqttClientIdPrefix) && Strings.CS.startsWith(clientId, mqttClientIdPrefix)) {
            String deviceKey = StringUtils.substringAfter(clientId, mqttClientIdPrefix);
            if (StringUtils.isNotBlank(deviceKey)) {
                return deviceKey;
            }
        }
        if (StringUtils.isNotBlank(userName)) {
            return userName;
        }
        return ERROR_UNIQUE_ID;
    }
}
