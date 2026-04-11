package com.connector.domain.entity.connectionconfig;

public record SftpConnectionConfig(
    String host,
    Integer port,
    String username,
    String password,
    String privateKey,
    String remoteDirectory
) implements ConnectionConfig {
}
