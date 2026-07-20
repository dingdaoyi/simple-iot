package com.github.dingdaoyi.iot.influx;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.influxdb.v3.client.InfluxDBClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author dingyunwei
 */
@Slf4j
@Configuration
public class InfluxDbConfig {
    @Resource
    private IotConfigProperties iotConfigProperties;

    @Lazy
    @Bean(destroyMethod = "close")
    public InfluxDBClient influxDbClient() {
        IotConfigProperties.InfluxDbProperties influxdb = iotConfigProperties.getInfluxdb();
        try {
            return InfluxDBClient.getInstance(influxdb.getUrl(), influxdb.getToken().toCharArray(), influxdb.getDatabase());
        } catch (Throwable e) {
            // ponytail: Arrow/Netty 版本冲突在容器中可能导致初始化失败
            // 不应该让 InfluxDB 连接问题阻止整个应用启动
            log.error("InfluxDB 客户端初始化失败，时序数据功能不可用: {}", e.getMessage(), e);
            return null;
        }
    }
}
