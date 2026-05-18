package com.connector.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.connector.application.port.out.HttpClient;
import com.connector.infra.http.RestTemplateHttpClient;

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClientPort() {
        return new RestTemplateHttpClient(new RestTemplate());
    }
}