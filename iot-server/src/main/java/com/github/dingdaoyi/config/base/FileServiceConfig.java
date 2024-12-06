package com.github.dingdaoyi.config.base;

import com.github.dingdaoyi.service.impl.LocalStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author dingyunwei
 */
@Configuration
public class FileServiceConfig {

    @Bean
    @ConditionalOnProperty(prefix = "sample.iot",name = "storage-type",havingValue = "file")
    public LocalStorageService storageService(IotConfigProperties iotConfigProperties) throws IOException {
        IotConfigProperties.LocalStorage localstorage = iotConfigProperties.getLocalstorage();
        return new LocalStorageService(localstorage.getLocalDir());
    }

}
