package com.dms.interfaces.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

        @Bean
        public S3Client s3Client(MinioConfig props) {
                return S3Client.builder()
                                .endpointOverride(URI.create(props.getUrl()))
                                .region(Region.AP_SOUTHEAST_1)
                                .credentialsProvider(
                                                StaticCredentialsProvider.create(
                                                                AwsBasicCredentials.create(
                                                                                props.getAccessKey(),
                                                                                props.getSecretKey())))
                                .forcePathStyle(true)
                                .build();
        }

        // @Bean
        // public S3Client s3Client(
        //                 @Value("${aws.region}") String region) {
        //         return S3Client.builder()
        //                         .region(Region.of(region))
        //                         .build();
        // }

        // @Bean
        // public S3Presigner s3Presigner(
        //                 @Value("${aws.region}") String region) {
        //         return S3Presigner.builder()
        //                         .region(Region.of(region))
        //                         .build();
        // }
}