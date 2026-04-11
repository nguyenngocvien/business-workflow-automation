-- =========================
-- 1. PROCESS DEFINITIONS
-- =========================

CREATE TABLE process_definitions (
    id BIGSERIAL PRIMARY KEY,
    
    application_name VARCHAR(100) NOT NULL,
    process_key VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,

    CONSTRAINT uq_process_definitions_key UNIQUE (process_key)
);

-- =========================
-- 2. PROCESS VERSIONS
-- =========================

CREATE TABLE process_versions (
    id BIGSERIAL PRIMARY KEY,

    process_definition_id BIGINT NOT NULL,
    version INTEGER NOT NULL,

    name VARCHAR(255) NOT NULL,

    bpmn_xml TEXT NOT NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',

    -- Camunda mapping
    camunda_definition_key VARCHAR(255),
    camunda_version INTEGER,
    deployment_id VARCHAR(255),

    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),

    updated_at TIMESTAMP,
    deployed_at TIMESTAMP,

    CONSTRAINT fk_process_versions_definition
        FOREIGN KEY (process_definition_id)
        REFERENCES process_definitions(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_process_version
        UNIQUE (process_definition_id, version),

    CONSTRAINT chk_process_status
        CHECK (status IN ('DRAFT', 'DEPLOYED', 'ARCHIVED'))
);

CREATE INDEX idx_versions_definition_version_desc
ON process_versions(process_definition_id, version DESC);

CREATE INDEX idx_versions_definition_status
ON process_versions(process_definition_id, status);

CREATE INDEX idx_versions_deployment_id
ON process_versions(deployment_id);

CREATE UNIQUE INDEX ux_one_active_version
ON process_versions(process_definition_id)
WHERE status = 'DEPLOYED';

-- =========================
-- 3. PROCESS DEPLOYMENTS
-- ========================

CREATE TABLE process_deployments (
    id BIGSERIAL PRIMARY KEY,

    process_definition_id BIGINT NOT NULL,
    process_version_id BIGINT NOT NULL,

    deployment_id VARCHAR(255) NOT NULL, -- from Camunda

    deployed_by VARCHAR(100),
    deployed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    environment VARCHAR(50), -- dev / staging / prod

    CONSTRAINT fk_deployment_definition
        FOREIGN KEY (process_definition_id)
        REFERENCES process_definitions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_deployment_version
        FOREIGN KEY (process_version_id)
        REFERENCES process_versions(id)
        ON DELETE CASCADE
);

-- =========================
-- 4. PROCESS STEPS DEFINITION
-- =========================

CREATE TABLE process_step_definition (
    id BIGSERIAL PRIMARY KEY,
    process_definition_id BIGINT NOT NULL,
    step_name VARCHAR(150) NOT NULL,
    step_code VARCHAR(100) NOT NULL,
    step_type VARCHAR(50) NOT NULL,
    step_order INT,
    next_step_code VARCHAR(100),
    condition_expression VARCHAR(500),
    sla_minutes INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_step_def_process FOREIGN KEY (process_definition_id) REFERENCES process_definitions(id) ON DELETE CASCADE
);

CREATE INDEX idx_step_def_process ON process_step_definition(process_definition_id);

-- =========================
-- 5. PROCESS INSTANCE
-- =========================
CREATE TABLE process_instance (
    id BIGSERIAL PRIMARY KEY,
    instance_id VARCHAR(64) UNIQUE,
    process_definition_id BIGINT NOT NULL,
    business_key VARCHAR(150),
    status VARCHAR(50) NOT NULL,
    current_step_code VARCHAR(100),
    started_by VARCHAR(100),
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    CONSTRAINT fk_instance_definition FOREIGN KEY (process_definition_id) REFERENCES process_definitions(id)
);

CREATE INDEX idx_process_instance_status ON process_instance(status);

CREATE INDEX idx_process_instance_business_key ON process_instance(business_key);

CREATE INDEX idx_process_instance_camunda_id ON process_instance(camunda_instance_id);

-- =========================
-- 6. PROCESS TASK
-- =========================
CREATE TABLE process_task (
    id BIGSERIAL PRIMARY KEY,
    process_instance_id BIGINT NOT NULL,
    task_id VARCHAR(64) UNIQUE,
    task_name VARCHAR(150),
    task_code VARCHAR(100),
    assignee VARCHAR(100),
    candidate_group VARCHAR(100),
    owner VARCHAR(100),
    status VARCHAR(50),
    priority INT,
    due_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    claimed_at TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT fk_task_instance FOREIGN KEY (process_instance_id) REFERENCES process_instance(id) ON DELETE CASCADE
);

CREATE INDEX idx_process_task_assignee ON process_task(assignee);

CREATE INDEX idx_process_task_status ON process_task(status);

CREATE INDEX idx_process_task_task_id ON process_task(task_id);

-- =========================
-- 7. PROCESS TASK DATA
-- =========================
CREATE TABLE process_data (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    data_json JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_task_data_task FOREIGN KEY (task_id) REFERENCES process_task(id) ON DELETE CASCADE
);

-- =========================
-- 8. PROCESS TASK DATA HISTORY
-- =========================

CREATE TABLE process_task_data_history (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    data_json JSONB,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT,
    CONSTRAINT fk_task_data_hist_task FOREIGN KEY (task_id) REFERENCES process_task(id) ON DELETE CASCADE
);

-- ============================================
-- 9. TASK IDENTITY LINK
-- ============================================
CREATE TABLE process_task_identity_link (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT,
    group_id BIGINT,
    type VARCHAR(50) NOT NULL,
    -- CANDIDATE, ASSIGNEE, OWNER, PARTICIPANT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_identity_task FOREIGN KEY (task_id) REFERENCES process_task(id)
);

CREATE INDEX idx_process_task_identity_task ON process_task_identity_link(task_id);

CREATE INDEX idx_process_task_identity_group ON process_task_identity_link(group_id);

CREATE INDEX idx_process_task_identity_user ON process_task_identity_link(user_id);

-- =========================
-- 10. PROCESS ATTACHMENTS
-- =========================
CREATE TABLE process_attachment (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    uploaded_by VARCHAR(100),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attachment_task FOREIGN KEY (task_id) REFERENCES process_task(id) ON DELETE CASCADE
);

-- =========================
-- 11. SLA / TIMER
-- =========================
CREATE TABLE process_timer (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    due_date TIMESTAMP,
    reminder_date TIMESTAMP,
    escalation_user VARCHAR(100),
    status VARCHAR(50),
    CONSTRAINT fk_timer_task FOREIGN KEY (task_id) REFERENCES process_task(id) ON DELETE CASCADE
);

-- =========================
-- 12. PROCESS HISTORY
-- =========================
CREATE TABLE process_history (
    id BIGSERIAL PRIMARY KEY,
    process_instance_id BIGINT,
    task_id BIGINT,
    action VARCHAR(100),
    action_by VARCHAR(100),
    action_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    note VARCHAR(500),
    CONSTRAINT fk_history_instance FOREIGN KEY (process_instance_id) REFERENCES process_instance(id),
    CONSTRAINT fk_history_task FOREIGN KEY (task_id) REFERENCES process_task(id)
);

-- ============================================
-- 13. PROCESS TASK ASSIGNMENT HISTORY
-- ============================================
CREATE TABLE process_task_assignment_history (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    from_user VARCHAR(100),
    to_user VARCHAR(100),
    from_group BIGINT,
    to_group BIGINT,
    action_by VARCHAR(100),
    action_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    comment VARCHAR(500),
    CONSTRAINT fk_hist_task FOREIGN KEY (task_id) REFERENCES process_task(id)
);

CREATE INDEX idx_process_task_assign_hist_task ON process_task_assignment_history(task_id);