package com.connector.infra.db;

import com.connector.application.port.out.DatabaseClient;
import com.connector.application.port.out.model.DbRequest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.jdbc.core.namedparam.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcDatabaseClient implements DatabaseClient {

    // cache datasource by connection
    private final Map<ConnectionKey, HikariDataSource> dataSourceCache = new ConcurrentHashMap<>();

    @Override
    public int executeUpdate(DbRequest request) {
        var jdbc = new NamedParameterJdbcTemplate(getOrCreateDataSource(request));
        return jdbc.update(request.getSql(), new MapSqlParameterSource(request.getParams()));
    }

    @Override
    public List<Map<String, Object>> executeQuery(DbRequest request) {
        var jdbc = new NamedParameterJdbcTemplate(getOrCreateDataSource(request));
        return jdbc.queryForList(request.getSql(), request.getParams());
    }

    // =============================================

    private HikariDataSource getOrCreateDataSource(DbRequest request) {
        ConnectionKey key = ConnectionKey.from(request);

        return dataSourceCache.computeIfAbsent(key, k -> createDataSource(request));
    }

    private HikariDataSource createDataSource(DbRequest r) {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(r.getUrl());
        config.setUsername(r.getUsername());
        config.setPassword(r.getPassword());

        if (r.getDriverClassName() != null) {
            config.setDriverClassName(r.getDriverClassName());
        }

        // ===== tuning cơ bản (production safe default)
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300_000);      // 5 min
        config.setMaxLifetime(1_800_000);    // 30 min
        config.setConnectionTimeout(30_000); // 30s

        // optional: leak detection
        config.setLeakDetectionThreshold(60_000);

        return new HikariDataSource(config);
    }

    // =============================================
    // KEY dùng để cache
    // =============================================

    private static class ConnectionKey {

        private final String url;
        private final String username;
        private final String driver;

        private ConnectionKey(String url, String username, String driver) {
            this.url = url;
            this.username = username;
            this.driver = driver;
        }

        public static ConnectionKey from(DbRequest r) {
            return new ConnectionKey(
                r.getUrl(),
                r.getUsername(),
                r.getDriverClassName()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ConnectionKey that)) return false;
            return Objects.equals(url, that.url)
                && Objects.equals(username, that.username)
                && Objects.equals(driver, that.driver);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, username, driver);
        }
    }
}