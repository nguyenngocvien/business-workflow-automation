package com.baw.api_gateway.infrastructure.fallback;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baw.api_gateway.infrastructure.exception.ApiErrorResponse;

@RestController
@RequestMapping(path = "/fallback/document-service", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentServiceFallbackController {

    @RequestMapping
    public ResponseEntity<ApiErrorResponse> fallback() {
        ApiErrorResponse body = new ApiErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                "Service is currently unavailable, please try again later.",
                "/fallback/document-service"
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
