package com.connector.api.common;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.connector.application.common.CrudApplicationService;

public abstract class AbstractCrudController<REQUEST, RESPONSE, ID> {

    private final CrudApplicationService<REQUEST, RESPONSE, ID> service;

    protected AbstractCrudController(CrudApplicationService<REQUEST, RESPONSE, ID> service) {
        this.service = service;
    }

    @GetMapping
    public Page<RESPONSE> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public RESPONSE findById(@PathVariable ID id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<RESPONSE> create(@Valid @RequestBody REQUEST request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public RESPONSE update(@PathVariable ID id, @Valid @RequestBody REQUEST request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
