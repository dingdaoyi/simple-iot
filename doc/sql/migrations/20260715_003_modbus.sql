-- Migration: Modbus TCP driver (P2-1)

-- Modbus device configurations
CREATE TABLE IF NOT EXISTS tb_modbus_config (
    id          SERIAL PRIMARY KEY,
    device_id   INTEGER NOT NULL,
    host        VARCHAR(100) NOT NULL,
    port        INTEGER NOT NULL DEFAULT 502,
    unit_id     SMALLINT NOT NULL DEFAULT 1,
    interval_ms INTEGER NOT NULL DEFAULT 5000,
    register_map JSONB NOT NULL DEFAULT '[]'::jsonb,  -- [{identifier, function, address, count, dataType, scale}]
    enabled     BOOLEAN NOT NULL DEFAULT true,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_modbus_config_device ON tb_modbus_config(device_id);
