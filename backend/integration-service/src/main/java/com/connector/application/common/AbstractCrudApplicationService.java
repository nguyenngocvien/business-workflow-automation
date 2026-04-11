package com.connector.application.common;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.connector.common.exception.ResourceNotFoundException;

public abstract class AbstractCrudApplicationService<REQUEST, RESPONSE, ENTITY, ID>
    implements CrudApplicationService<REQUEST, RESPONSE, ID> {

    private final JpaRepository<ENTITY, ID> repository;
    private final String resourceName;

    protected AbstractCrudApplicationService(JpaRepository<ENTITY, ID> repository, String resourceName) {
        this.repository = repository;
        this.resourceName = resourceName;
    }

    @Override
    public Page<RESPONSE> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public RESPONSE findById(ID id) {
        return toResponse(requireEntity(id));
    }

    @Override
    public RESPONSE create(REQUEST request) {
        ENTITY entity = newEntity();
        updateEntity(entity, request, true);
        return toResponse(repository.save(entity));
    }

    @Override
    public RESPONSE update(ID id, REQUEST request) {
        ENTITY entity = requireEntity(id);
        updateEntity(entity, request, false);
        return toResponse(repository.save(entity));
    }

    @Override
    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(resourceName + " not found with id: " + id);
        }
        repository.deleteById(id);
    }

    protected ENTITY requireEntity(ID id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(resourceName + " not found with id: " + id));
    }

    protected abstract ENTITY newEntity();

    protected abstract RESPONSE toResponse(ENTITY entity);

    protected abstract void updateEntity(ENTITY entity, REQUEST request, boolean creating);
}
