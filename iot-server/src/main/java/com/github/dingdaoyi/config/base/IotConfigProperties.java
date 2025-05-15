package com.github.dingdaoyi.config.base;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingyunwei
 */
@Data
@Configuration
@ConfigurationProperties("sample.iot")
public class IotConfigProperties {
    /**
     * 开启设备密钥认证
     */
    private boolean enableDeviceSecret;


    /**
     * 配置本地存储
     */
    private LocalStorage localstorage;

    /**
     * 存储类型
     */
    private StorageType storageType;

    /**
     * 设备通道管理类型
     */
    private DeviceChannelType deviceChannelType;

    /**
     * influxdb配置
     */
    private InfluxDbProperties influxdb;

    @Data
    public static class InfluxDbProperties {

        /**
         * 地址
         */
        private String url;

        /**
         * token
         */
        private String token;

        /**
         * org
         */
        private String database;


        /**
         * 属性数据数据库
         */
        private String propDatabase;

        /**
         * 事件
         */
        private String eventDatabase;
    }
    public enum DeviceChannelType {
        PARTITION,SINGLE
    }

    public enum StorageType{
        FILE,
    }

    @Data
    public static class LocalStorage {
        /**
         * 本地存储目录
         */
        private String localDir;
    }
}
