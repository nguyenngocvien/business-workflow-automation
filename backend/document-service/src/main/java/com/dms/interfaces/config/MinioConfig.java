package com.dms.interfaces.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {
    private String url;
    private String accessKey;
    private String secretKey;
}