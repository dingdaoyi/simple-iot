package com.github.dingdaoyi.iot.influx;

import com.github.dingdaoyi.config.base.IotConfigProperties;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
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
        return InfluxDBClientFactory.create(influxdb.getUrl(), influxdb.getToken().toCharArray(), influxdb.getOrg(), influxdb.getBucket());
    }
}