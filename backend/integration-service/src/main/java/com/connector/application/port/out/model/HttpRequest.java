package com.connector.application.port.out.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRequest {

    private String url;
    private String method;
    private Map<String, String> headers;
    private String body;
}