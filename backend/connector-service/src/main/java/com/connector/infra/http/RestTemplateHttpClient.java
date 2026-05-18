package com.connector.infra.http;

import com.connector.application.port.out.HttpClient;
import com.connector.application.port.out.model.HttpRequest;
import com.connector.application.port.out.model.HttpResponse;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestTemplateHttpClient implements HttpClient {

    private final RestTemplate restTemplate;

    public RestTemplateHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {

        HttpHeaders headers = new HttpHeaders();

        if (request.getHeaders() != null) {
            request.getHeaders().forEach(headers::set);
        }

        HttpEntity<String> entity = new HttpEntity<>(request.getBody(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
            URI.create(request.getUrl()),
            HttpMethod.valueOf(request.getMethod().toUpperCase()),
            entity,
            String.class
        );

        HttpResponse result = new HttpResponse();
        result.setStatus(response.getStatusCode().value());
        result.setHeaders(convertHeaders(response.getHeaders()));
        result.setBody(response.getBody());

        return result;
    }

    private Map<String, List<String>> convertHeaders(HttpHeaders headers) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        headers.forEach((k, v) -> map.put(k, List.copyOf(v)));
        return map;
    }
}