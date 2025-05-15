package com.github.dingdaoyi.iot.influx;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.influxdb.v3.client.InfluxDBClient;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingyunwei
 */
@Configuration
public class InfluxDbConfig {
    @Resource
    private IotConfigProperties iotConfigProperties;

    @Bean(destroyMethod = "close")
    public InfluxDBClient influxDbClient() {
        IotConfigProperties.InfluxDbProperties influxdb = iotConfigProperties.getInfluxdb();
        return InfluxDBClient.getInstance(influxdb.getUrl(), influxdb.getToken().toCharArray(), influxdb.getDatabase());
    }
}