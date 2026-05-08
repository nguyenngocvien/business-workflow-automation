--liquibase formatted sql

--changeset codex:006-departments
CREATE TABLE departments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,

    parent_department_id BIGINT
        REFERENCES departments(id),

    manager_user_id BIGINT
        REFERENCES users(id),

    cost_center VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_department_manager
ON departments(manager_user_id);

--rollback DROP INDEX IF EXISTS idx_department_manager;
--rollback DROP TABLE IF EXISTS departments;
