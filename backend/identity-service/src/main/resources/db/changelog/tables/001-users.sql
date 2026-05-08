--liquibase formatted sql

--changeset codex:001-users
CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    external_id UUID UNIQUE,

    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,

    first_name VARCHAR(100),
    last_name VARCHAR(100),
    full_name VARCHAR(255),

    phone_number VARCHAR(50),

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    source VARCHAR(50) NOT NULL DEFAULT 'LOCAL',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'SUSPENDED')),
    CHECK (source IN ('LOCAL', 'KEYCLOAK', 'LDAP', 'AD'))
);

CREATE INDEX idx_users_username
ON users(username);

--rollback DROP INDEX IF EXISTS idx_users_username;
--rollback DROP TABLE IF EXISTS users;
