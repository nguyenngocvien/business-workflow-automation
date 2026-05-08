package com.workflow.interfaces.config;

import java.net.http.HttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IbmBpmProperties.class)
public class IbmBpmConfig {

    @Bean
    public HttpClient ibmBpmHttpClient(IbmBpmProperties properties) {
        return HttpClient.newBuilder()
            .connectTimeout(properties.getRequestTimeout())
            .build();
    }
}
