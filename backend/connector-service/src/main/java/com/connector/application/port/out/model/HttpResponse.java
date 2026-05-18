package com.connector.application.port.out.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpResponse {

    private int status;
    private Map<String, List<String>> headers;
    private String body;
}