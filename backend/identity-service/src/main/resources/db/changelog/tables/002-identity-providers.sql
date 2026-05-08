--liquibase formatted sql

--changeset codex:002-identity-providers
CREATE TABLE identity_providers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    provider_code VARCHAR(50) UNIQUE NOT NULL,
    provider_name VARCHAR(255) NOT NULL,
    provider_type VARCHAR(50) NOT NULL,

    config JSONB,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

--rollback DROP TABLE IF EXISTS identity_providers;
