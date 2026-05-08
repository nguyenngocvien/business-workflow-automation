--liquibase formatted sql

--changeset codex:007-user-external-mappings
CREATE TABLE user_external_mappings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    user_id BIGINT NOT NULL
        REFERENCES users(id)
        ON DELETE CASCADE,

    provider_id BIGINT NOT NULL
        REFERENCES identity_providers(id),

    external_user_id VARCHAR(255) NOT NULL,
    external_username VARCHAR(255),
    synced_at TIMESTAMPTZ,

    UNIQUE(provider_id, external_user_id)
);

--rollback DROP TABLE IF EXISTS user_external_mappings;
