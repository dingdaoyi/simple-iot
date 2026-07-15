-- Migration: OTA firmware management (P1-6)

-- Firmware packages
CREATE TABLE IF NOT EXISTS tb_firmware (
    id          SERIAL PRIMARY KEY,
    product_id  INTEGER NOT NULL,
    name        VARCHAR(200) NOT NULL,
    version     VARCHAR(50) NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    file_size   BIGINT DEFAULT 0,
    checksum    VARCHAR(128),
    description TEXT,
    status      VARCHAR(20) DEFAULT 'DRAFT',  -- DRAFT / PUBLISHED / ARCHIVED
    created_by  VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- OTA upgrade tasks
CREATE TABLE IF NOT EXISTS tb_ota_task (
    id          SERIAL PRIMARY KEY,
    firmware_id INTEGER NOT NULL,
    group_id    INTEGER,
    device_ids  JSONB DEFAULT '[]',   -- target device id array
    status      VARCHAR(20) DEFAULT 'PENDING',  -- PENDING / RUNNING / COMPLETED / FAILED
    total       INTEGER DEFAULT 0,
    success     INTEGER DEFAULT 0,
    fail        INTEGER DEFAULT 0,
    progress    JSONB DEFAULT '{}',   -- {deviceId: "PENDING|UPGRADING|SUCCESS|FAIL"}
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
