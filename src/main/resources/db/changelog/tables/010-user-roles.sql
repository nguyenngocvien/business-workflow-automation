--liquibase formatted sql

--changeset codex:010-user-roles
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL
        REFERENCES users(id)
        ON DELETE CASCADE,

    role_id BIGINT NOT NULL
        REFERENCES roles(id)
        ON DELETE CASCADE,

    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ,

    PRIMARY KEY(user_id, role_id)
);

CREATE INDEX idx_user_roles_user
ON user_roles(user_id);

--rollback DROP INDEX IF EXISTS idx_user_roles_user;
--rollback DROP TABLE IF EXISTS user_roles;
