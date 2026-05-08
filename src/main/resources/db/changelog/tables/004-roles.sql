--liquibase formatted sql

--changeset codex:004-roles
CREATE TABLE roles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,

    role_type VARCHAR(50) NOT NULL,
    description TEXT,

    source VARCHAR(50) NOT NULL DEFAULT 'LOCAL',
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (role_type IN ('SYSTEM', 'BUSINESS', 'CAMUNDA', 'APPROVAL'))
);

--rollback DROP TABLE IF EXISTS roles;
