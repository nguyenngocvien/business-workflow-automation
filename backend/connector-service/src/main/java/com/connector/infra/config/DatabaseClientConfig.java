package com.connector.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.connector.application.port.out.DatabaseClient;
import com.connector.infra.db.JdbcDatabaseClient;

@Configuration
public class DatabaseClientConfig {

    @Bean
    public DatabaseClient databaseClient() {
        return new JdbcDatabaseClient();
    }
}