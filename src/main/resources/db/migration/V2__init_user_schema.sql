-- ============================================
-- 1. USER
-- ============================================
CREATE TABLE wf_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    email VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wf_user_username ON wf_user(username);

-- ============================================
-- 2. GROUP (Hierarchy)
-- ============================================
CREATE TABLE wf_group (
    id BIGSERIAL PRIMARY KEY,
    group_code VARCHAR(100) NOT NULL UNIQUE,
    group_name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    parent_group_id BIGINT,
    path VARCHAR(500),
    level INT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    CONSTRAINT fk_group_parent FOREIGN KEY (parent_group_id) REFERENCES wf_group(id)
);

CREATE INDEX idx_wf_group_parent ON wf_group(parent_group_id);

CREATE INDEX idx_wf_group_path ON wf_group(path);

CREATE INDEX idx_wf_group_level ON wf_group(level);

-- ============================================
-- 3. USER - GROUP MAPPING
-- ============================================
CREATE TABLE wf_user_group (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_group_user FOREIGN KEY (user_id) REFERENCES wf_user(id),
    CONSTRAINT fk_user_group_group FOREIGN KEY (group_id) REFERENCES wf_group(id),
    CONSTRAINT uq_user_group UNIQUE (user_id, group_id)
);

CREATE INDEX idx_wf_user_group_user ON wf_user_group(user_id);

CREATE INDEX idx_wf_user_group_group ON wf_user_group(group_id);