package com.github.dingdaoyi.config.base;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author dingyunwei
 */
@Configuration
@Data
public class ExecutorServiceConfig {


    @Bean
    public ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
