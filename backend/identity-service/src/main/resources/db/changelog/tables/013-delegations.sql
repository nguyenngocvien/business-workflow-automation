--liquibase formatted sql

--changeset codex:013-delegations
CREATE TABLE delegations (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    assignor_user_id BIGINT NOT NULL
        REFERENCES users(id),

    delegate_user_id BIGINT NOT NULL
        REFERENCES users(id),

    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,

    reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (assignor_user_id <> delegate_user_id),
    CHECK (end_time > start_time)
);

CREATE INDEX idx_delegation_assignor
ON delegations(assignor_user_id);

CREATE INDEX idx_delegation_time
ON delegations(start_time, end_time);

--rollback DROP INDEX IF EXISTS idx_delegation_time;
--rollback DROP INDEX IF EXISTS idx_delegation_assignor;
--rollback DROP TABLE IF EXISTS delegations;
