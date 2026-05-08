--liquibase formatted sql

--changeset codex:014-sync-jobs
CREATE TABLE sync_jobs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    provider_id BIGINT NOT NULL
        REFERENCES identity_providers(id),

    job_type VARCHAR(50) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,

    status VARCHAR(20) NOT NULL,
    summary JSONB
);

--rollback DROP TABLE IF EXISTS sync_jobs;
