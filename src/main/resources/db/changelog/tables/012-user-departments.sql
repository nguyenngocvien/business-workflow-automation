--liquibase formatted sql

--changeset codex:012-user-departments
CREATE TABLE user_departments (
    user_id BIGINT NOT NULL
        REFERENCES users(id)
        ON DELETE CASCADE,

    department_id BIGINT NOT NULL
        REFERENCES departments(id)
        ON DELETE CASCADE,

    is_primary BOOLEAN NOT NULL DEFAULT FALSE,

    PRIMARY KEY(user_id, department_id)
);

--rollback DROP TABLE IF EXISTS user_departments;
