package com.connector.application.port.out.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DbRequest {

    private String url;
    private String username;
    private String password;
    private String driverClassName;

    private String sql;
    private Map<String, Object> params;
}