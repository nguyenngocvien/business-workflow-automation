package com.dms.interfaces.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3PresignerConfig {

    @Bean
    public S3Presigner s3Presigner(MinioConfig props) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(props.getUrl()))
                .region(Region.AP_SOUTHEAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        props.getAccessKey(),
                                        props.getSecretKey())))

                .build();
    }

    // @Bean
    // public S3Presigner s3Presigner(
    // @Value("${aws.region}") String region,
    // @Value("${aws.endpoint:}") String endpoint) {
    // S3Presigner.Builder builder = S3Presigner.builder()
    // .region(Region.of(region));

    // if (endpoint != null && !endpoint.isEmpty()) {
    // builder.endpointOverride(URI.create(endpoint));
    // }

    // return builder.build();
    // }
}
