-- =========================
-- 1. FILE CATEGORIES
-- =========================
CREATE TABLE file_categories (
    id              BIGSERIAL PRIMARY KEY,

    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,

    max_size        BIGINT,
    bucket_name     VARCHAR(100),
    retention_days  INT,

    is_public       BOOLEAN DEFAULT FALSE,

    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 2. FILE CATEGORY TYPES
-- =========================
CREATE TABLE file_category_types (
    id              BIGSERIAL PRIMARY KEY,
    category_id     BIGINT NOT NULL,
    content_type    VARCHAR(100) NOT NULL,

    CONSTRAINT fk_cat_type
        FOREIGN KEY (category_id)
        REFERENCES file_categories(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_cat_type UNIQUE (category_id, content_type)
);

-- =========================
-- 3. FILES
-- =========================
CREATE TABLE files (
    id              BIGSERIAL PRIMARY KEY,

    -- storage
    bucket_name     VARCHAR(100) NOT NULL,
    object_key      VARCHAR(500) NOT NULL,

    -- file info
    file_name       VARCHAR(255) NOT NULL,
    original_name   VARCHAR(255),
    content_type    VARCHAR(100),
    file_size       BIGINT NOT NULL,

    checksum        VARCHAR(128),

    -- classification
    category_id     BIGINT,

    -- status
    status          SMALLINT NOT NULL DEFAULT 1, -- 1=ACTIVE,2=DELETED,3=ARCHIVED

    -- audit
    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100),
    updated_at      TIMESTAMPTZ,
    updated_by      VARCHAR(100),
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_files_category
        FOREIGN KEY (category_id)
        REFERENCES file_categories(id),

    CONSTRAINT uk_files_object UNIQUE (bucket_name, object_key)
);

-- =========================
-- 4. FILE ATTRIBUTES
-- =========================
CREATE TYPE attribute_data_type AS ENUM (
    'string',
    'number',
    'date',
    'boolean',
    'list'
);

CREATE TABLE file_attributes (
    id              BIGSERIAL PRIMARY KEY,

    key_code        VARCHAR(50) NOT NULL,
    display_name    VARCHAR(100) NOT NULL,

    data_type       attribute_data_type NOT NULL,

    is_required     BOOLEAN NOT NULL DEFAULT FALSE,

    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100),

    CONSTRAINT uk_file_attr UNIQUE (key_code)
);

-- =========================
-- 5. FILE ATTRIBUTE OPTIONS
-- =========================
CREATE TABLE file_attribute_options (
    id              BIGSERIAL PRIMARY KEY,

    attribute_id    BIGINT NOT NULL,

    option_label    VARCHAR(100) NOT NULL,
    option_value    VARCHAR(100) NOT NULL,

    sort_order      INT DEFAULT 0,

    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attr_opt
        FOREIGN KEY (attribute_id)
        REFERENCES file_attributes(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_attr_opt UNIQUE (attribute_id, option_value)
);

-- =========================
-- 6. FILE ATTRIBUTE VALUES
-- =========================
CREATE TABLE file_attribute_values (
    id              BIGSERIAL PRIMARY KEY,

    file_id         BIGINT NOT NULL,
    attribute_id    BIGINT NOT NULL,

    value_string    TEXT,
    value_number    DECIMAL(18,4),
    value_boolean   BOOLEAN,
    value_date      TIMESTAMPTZ,

    option_id       BIGINT,

    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attr_val_file
        FOREIGN KEY (file_id)
        REFERENCES files(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_attr_val_attr
        FOREIGN KEY (attribute_id)
        REFERENCES file_attributes(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_attr_val_option
        FOREIGN KEY (option_id)
        REFERENCES file_attribute_options(id)
        ON DELETE CASCADE,

    -- mỗi file chỉ có 1 value cho 1 attribute
    CONSTRAINT uk_file_attr_value UNIQUE (file_id, attribute_id),

    -- đảm bảo chỉ có 1 value được set
    CONSTRAINT chk_single_value CHECK (
        (CASE WHEN value_string IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN value_number IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN value_boolean IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN value_date IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN option_id IS NOT NULL THEN 1 ELSE 0 END)
        = 1
    )
);

CREATE OR REPLACE FUNCTION validate_file_attribute_value_type()
RETURNS TRIGGER AS $$
DECLARE
    attr_type attribute_data_type;
BEGIN
    SELECT data_type
    INTO attr_type
    FROM file_attributes
    WHERE id = NEW.attribute_id;

    IF attr_type IS NULL THEN
        RAISE EXCEPTION 'Unknown attribute_id %', NEW.attribute_id;
    END IF;

    IF NEW.value_string IS NOT NULL AND attr_type <> 'string' THEN
        RAISE EXCEPTION 'Attribute % expects string values', NEW.attribute_id;
    ELSIF NEW.value_number IS NOT NULL AND attr_type <> 'number' THEN
        RAISE EXCEPTION 'Attribute % expects number values', NEW.attribute_id;
    ELSIF NEW.value_boolean IS NOT NULL AND attr_type <> 'boolean' THEN
        RAISE EXCEPTION 'Attribute % expects boolean values', NEW.attribute_id;
    ELSIF NEW.value_date IS NOT NULL AND attr_type <> 'date' THEN
        RAISE EXCEPTION 'Attribute % expects date values', NEW.attribute_id;
    ELSIF NEW.option_id IS NOT NULL AND attr_type <> 'list' THEN
        RAISE EXCEPTION 'Attribute % expects list values', NEW.attribute_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_file_attribute_value_type
BEFORE INSERT OR UPDATE ON file_attribute_values
FOR EACH ROW
EXECUTE FUNCTION validate_file_attribute_value_type();

-- =========================
-- 7. FILE VERSIONS
-- =========================
CREATE TABLE file_versions (
    id              BIGSERIAL PRIMARY KEY,

    file_id         BIGINT NOT NULL,
    version         INT NOT NULL,
    object_key      VARCHAR(500) NOT NULL,

    created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(100),

    CONSTRAINT fk_file_version
        FOREIGN KEY (file_id)
        REFERENCES files(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_file_version UNIQUE (file_id, version)
);

-- =========================
-- 8. FILE ACCESS LOGS
-- =========================
CREATE TABLE file_access_logs (
    id              BIGSERIAL PRIMARY KEY,

    file_id         BIGINT NOT NULL,
    accessed_at     TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    accessed_by     VARCHAR(100),
    action          VARCHAR(20), -- DOWNLOAD / VIEW / DELETE

    CONSTRAINT fk_access_file
        FOREIGN KEY (file_id)
        REFERENCES files(id)
        ON DELETE CASCADE
);

-- =========================
-- INDEXES
-- =========================

-- files
CREATE INDEX idx_files_category ON files(category_id);
CREATE INDEX idx_files_status ON files(status);
CREATE INDEX idx_files_created ON files(created_at);
CREATE INDEX idx_files_checksum ON files(checksum);

-- attribute values
CREATE INDEX idx_attr_val_file ON file_attribute_values(file_id);
CREATE INDEX idx_attr_val_attr ON file_attribute_values(attribute_id);

CREATE INDEX idx_attr_val_string 
ON file_attribute_values(attribute_id, value_string);

CREATE INDEX idx_attr_val_number 
ON file_attribute_values(attribute_id, value_number);

CREATE INDEX idx_attr_val_date 
ON file_attribute_values(attribute_id, value_date);

CREATE INDEX idx_attr_val_option 
ON file_attribute_values(attribute_id, option_id);

-- access logs
CREATE INDEX idx_access_file ON file_access_logs(file_id);
CREATE INDEX idx_access_time ON file_access_logs(accessed_at);
