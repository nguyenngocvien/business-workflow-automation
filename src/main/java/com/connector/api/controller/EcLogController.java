package com.connector.api.controller;

import java.time.LocalDateTime;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.connector.application.dto.request.EcLogRequest;
import com.connector.application.dto.response.EcLogResponse;
import com.connector.application.service.EcLogApplicationService;
import com.connector.domain.entity.EcLogId;

@RestController
@RequestMapping("/api/logs")
public class EcLogController {

    private final EcLogApplicationService service;

    public EcLogController(EcLogApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public Page<EcLogResponse> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}/{requestTime}")
    public EcLogResponse findById(
        @PathVariable Long id,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestTime
    ) {
        return service.findById(new EcLogId(id, requestTime));
    }

    @PostMapping
    public ResponseEntity<EcLogResponse> create(@Valid @RequestBody EcLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}/{requestTime}")
    public EcLogResponse update(
        @PathVariable Long id,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestTime,
        @Valid @RequestBody EcLogRequest request
    ) {
        return service.update(new EcLogId(id, requestTime), request);
    }

    @DeleteMapping("/{id}/{requestTime}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestTime
    ) {
        service.delete(new EcLogId(id, requestTime));
        return ResponseEntity.noContent().build();
    }
}
