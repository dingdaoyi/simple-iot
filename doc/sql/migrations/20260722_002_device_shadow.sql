-- Migration: P0-8 device shadow

CREATE TABLE IF NOT EXISTS tb_device_shadow (
    id              SERIAL PRIMARY KEY,
    device_id       INTEGER NOT NULL REFERENCES tb_device(id) ON DELETE CASCADE,
    desired_state   JSONB NOT NULL DEFAULT '{}'::jsonb,
    reported_state  JSONB NOT NULL DEFAULT '{}'::jsonb,
    version         INTEGER NOT NULL DEFAULT 0,
    create_time     TIMESTAMP NOT NULL DEFAULT now(),
    update_time     TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE(device_id)
);

COMMENT ON COLUMN tb_device_shadow.desired_state IS '期望状态 JSON';
COMMENT ON COLUMN tb_device_shadow.reported_state IS '上报状态 JSON';
COMMENT ON COLUMN tb_device_shadow.version IS '乐观锁版本号';
