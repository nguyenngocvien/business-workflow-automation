--liquibase formatted sql

--changeset codex:005-permissions
CREATE TABLE permissions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    code VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,

    resource VARCHAR(255),
    action VARCHAR(100),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

--rollback DROP TABLE IF EXISTS permissions;
