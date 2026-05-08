--liquibase formatted sql

--changeset codex:015-audit-logs
CREATE TABLE audit_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(255) NOT NULL,

    action VARCHAR(100) NOT NULL,
    performed_by VARCHAR(255),

    details JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

--rollback DROP TABLE IF EXISTS audit_logs;
