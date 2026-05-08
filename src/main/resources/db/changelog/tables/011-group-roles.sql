--liquibase formatted sql

--changeset codex:011-group-roles
CREATE TABLE group_roles (
    group_id BIGINT NOT NULL
        REFERENCES groups(id)
        ON DELETE CASCADE,

    role_id BIGINT NOT NULL
        REFERENCES roles(id)
        ON DELETE CASCADE,

    PRIMARY KEY(group_id, role_id)
);

CREATE INDEX idx_group_roles_group
ON group_roles(group_id);

--rollback DROP INDEX IF EXISTS idx_group_roles_group;
--rollback DROP TABLE IF EXISTS group_roles;
