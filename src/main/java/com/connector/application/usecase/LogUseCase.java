package com.connector.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.connector.application.result.LogResult;

public interface LogUseCase {

    Page<LogResult> findAll(Pageable pageable);

    LogResult findById(Long id);

    void delete(Long id);
}
