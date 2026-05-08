--liquibase formatted sql

--changeset codex:008-user-groups
CREATE TABLE user_groups (
    user_id BIGINT NOT NULL
        REFERENCES users(id)
        ON DELETE CASCADE,

    group_id BIGINT NOT NULL
        REFERENCES groups(id)
        ON DELETE CASCADE,

    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    PRIMARY KEY(user_id, group_id)
);

CREATE INDEX idx_user_groups_user
ON user_groups(user_id);

--rollback DROP INDEX IF EXISTS idx_user_groups_user;
--rollback DROP TABLE IF EXISTS user_groups;
