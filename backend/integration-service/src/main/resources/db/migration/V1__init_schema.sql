CREATE TABLE ec_connection (
    id                  BIGSERIAL PRIMARY KEY,
    connection_code     VARCHAR(100) NOT NULL UNIQUE,
    connection_name     VARCHAR(255) NOT NULL,
    connection_type     VARCHAR(50) NOT NULL, -- DB, REST, SOAP, SFTP, SMTP, KAFKA
    config_json         JSONB NOT NULL,
    active              BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ec_email_template (
    id                  BIGSERIAL PRIMARY KEY,

    app_id              VARCHAR(100) NOT NULL,
    template_type       VARCHAR(50) NOT NULL,
    template_code       VARCHAR(100) NOT NULL,

    title               VARCHAR(255) NOT NULL,
    content             TEXT NOT NULL,

    status              BOOLEAN NOT NULL DEFAULT TRUE,

    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),

    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_email_template UNIQUE (app_id, template_type, template_code)
);

CREATE TABLE ec_service (
    id                  BIGSERIAL PRIMARY KEY,
    service_code        VARCHAR(100) NOT NULL UNIQUE,
    service_name        VARCHAR(255),
    service_type        VARCHAR(50), -- REST, DB, PIPELINE
    service_version     VARCHAR(50), -- REST, DB, PIPELINE
    app_id              VARCHAR(100),
    connection_id       BIGINT,
	config_json         JSONB NOT NULL,
    timeout_ms          INT,
    retry_count         INT DEFAULT 0,
    active              BOOLEAN DEFAULT TRUE,
	log_enable          BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	created_by          VARCHAR(100),
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_by          VARCHAR(100),
    CONSTRAINT fk_service_connection
        FOREIGN KEY (connection_id)
        REFERENCES ec_connection(id)
);

CREATE TABLE ec_pipeline (
    id                  BIGSERIAL PRIMARY KEY,
    pipeline_code       VARCHAR(100) UNIQUE,
    pipeline_name       VARCHAR(255),
    description         TEXT,
    active              BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ec_pipeline_step (
    id                  BIGSERIAL PRIMARY KEY,
    pipeline_id         BIGINT,
    step_order          INT,
    service_id          BIGINT,
    step_name           VARCHAR(255),
    request_transform   TEXT,
    response_transform  TEXT,
    continue_on_error   BOOLEAN DEFAULT FALSE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_step_pipeline
        FOREIGN KEY (pipeline_id)
        REFERENCES ec_pipeline(id),
    CONSTRAINT fk_step_service
        FOREIGN KEY (service_id)
        REFERENCES ec_service(id)
);

CREATE TABLE ec_schedule_job (
    id                  BIGSERIAL PRIMARY KEY,
    job_code            VARCHAR(100) UNIQUE,
    job_name            VARCHAR(255),
    job_type            VARCHAR(50), -- SERVICE, PIPELINE
    service_id          BIGINT,
    pipeline_id         BIGINT,
    cron_expression     VARCHAR(50),
    fixed_rate_ms       BIGINT,
    enabled             BOOLEAN DEFAULT TRUE,
    last_run_time       TIMESTAMP,
    next_run_time       TIMESTAMP,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ec_job_execution (
    id                  BIGSERIAL PRIMARY KEY,
    job_id              BIGINT,
    start_time          TIMESTAMP,
    end_time            TIMESTAMP,
    status              VARCHAR(50),
    request_data        JSONB,
    response_data       JSONB,
    error_message       TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ec_log (
    id                          BIGSERIAL,
    service_id                  BIGINT,

    trace_id                    VARCHAR(100),
    correlation_id              VARCHAR(100),

    request_time                TIMESTAMP NOT NULL,
    response_time               TIMESTAMP,
    duration_ms                 BIGINT,

    status_code                 INT,

    request_headers             JSONB,
    request_body                JSONB,
    request_after_transform     JSONB,

    response_body               JSONB,
    response_after_transform    JSONB,

    error_message               TEXT,
    stacktrace                  TEXT,

    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),

    PRIMARY KEY (id, request_time),

    CONSTRAINT fk_log_service
        FOREIGN KEY (service_id)
        REFERENCES ec_service(id)
        ON DELETE SET NULL
)
PARTITION BY RANGE (request_time);

CREATE TABLE ec_log_default PARTITION OF ec_log DEFAULT;

CREATE OR REPLACE FUNCTION create_ec_log_partition(p_date DATE)
RETURNS VOID AS $$
DECLARE
    partition_name TEXT;
BEGIN
    partition_name := 'ec_log_' || TO_CHAR(p_date, 'YYYYMMDD');

    EXECUTE format(
        'CREATE TABLE IF NOT EXISTS %I PARTITION OF ec_log
        FOR VALUES FROM (%L) TO (%L)',
        partition_name,
        p_date,
        p_date + INTERVAL '1 day'
    );
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_duration_ms()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.response_time IS NOT NULL THEN
        NEW.duration_ms := (EXTRACT(EPOCH FROM (NEW.response_time - NEW.request_time)) * 1000)::BIGINT;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_set_duration
BEFORE INSERT ON ec_log
FOR EACH ROW
EXECUTE FUNCTION set_duration_ms();
