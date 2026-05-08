--liquibase formatted sql

--changeset codex:003-groups
CREATE TABLE groups (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,

    parent_group_id BIGINT
        REFERENCES groups(id)
        ON DELETE SET NULL,

    group_type VARCHAR(50) NOT NULL,
    path TEXT,
    description TEXT,

    source VARCHAR(50) NOT NULL DEFAULT 'LOCAL',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (group_type IN ('DEPARTMENT', 'TEAM', 'PROJECT', 'SECURITY', 'CAMUNDA'))
);

--rollback DROP TABLE IF EXISTS groups;
