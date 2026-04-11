package com.connector.application.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudApplicationService<REQUEST, RESPONSE, ID> {

    Page<RESPONSE> findAll(Pageable pageable);

    RESPONSE findById(ID id);

    RESPONSE create(REQUEST request);

    RESPONSE update(ID id, REQUEST request);

    void delete(ID id);
}
