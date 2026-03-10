package com.github.dingdaoyi.config.base;

import com.github.dingdaoyi.service.impl.LocalStorageService;
import com.github.dingdaoyi.service.impl.S3StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.net.URI;

/**
 * @author dingyunwei
 */
@Slf4j
@Configuration
public class FileServiceConfig {

    @Bean
    @ConditionalOnProperty(prefix = "sample.iot",name = "storage-type",havingValue = "file")
    public LocalStorageService storageService(IotConfigProperties iotConfigProperties) throws IOException {
        IotConfigProperties.LocalStorage localstorage = iotConfigProperties.getLocalstorage();
        return new LocalStorageService(localstorage.getLocalDir());
    }

    @Bean
    @ConditionalOnProperty(prefix = "sample.iot", name = "storage-type", havingValue = "s3")
    public S3StorageService s3StorageService(IotConfigProperties iotConfigProperties) {
        IotConfigProperties.S3Storage s3Config = iotConfigProperties.getS3storage();

        log.info("Initializing S3-compatible storage: endpoint={}, bucket={}, region={}",
                s3Config.getEndpointUrl(), s3Config.getBucketName(), s3Config.getRegion());

        // 配置 S3 客户端，兼容多种 S3 存储 (RustFS, MinIO, AWS S3 等)
        S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(s3Config.getEndpointUrl()))
                .region(Region.of(s3Config.getRegion()))  // 从配置读取 region
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3Config.getAccessKeyId(), s3Config.getSecretAccessKey())))
                .forcePathStyle(true)  // Path-Style 模式，兼容非 AWS S3 存储
                .build();

        log.info("S3 client initialized successfully");
        return new S3StorageService(s3Client, s3Config.getBucketName());
    }
}
