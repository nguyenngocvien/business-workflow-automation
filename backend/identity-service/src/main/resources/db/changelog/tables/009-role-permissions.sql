--liquibase formatted sql

--changeset codex:009-role-permissions
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL
        REFERENCES roles(id)
        ON DELETE CASCADE,

    permission_id BIGINT NOT NULL
        REFERENCES permissions(id)
        ON DELETE CASCADE,

    PRIMARY KEY(role_id, permission_id)
);

--rollback DROP TABLE IF EXISTS role_permissions;
