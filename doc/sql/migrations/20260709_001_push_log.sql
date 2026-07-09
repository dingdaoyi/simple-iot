-- Migration: push delivery log table
CREATE TABLE IF NOT EXISTS tb_push_log (
    id              SERIAL PRIMARY KEY,
    push_config_id   INTEGER      NOT NULL,
    push_config_name VARCHAR(100),
    event_type       VARCHAR(50)  DEFAULT 'push.test',
    success          BOOLEAN      NOT NULL DEFAULT false,
    status_code      INTEGER,
    message          TEXT,
    response_body    TEXT,
    device_key       VARCHAR(100),
    duration_ms      BIGINT,
    create_time      TIMESTAMP    NOT NULL DEFAULT now(),
    update_time      TIMESTAMP
);

COMMENT ON TABLE  tb_push_log IS '推送交付日志';
COMMENT ON COLUMN tb_push_log.push_config_id   IS '推送配置ID';
COMMENT ON COLUMN tb_push_log.push_config_name IS '推送配置名称(冗余，便于查询)';
COMMENT ON COLUMN tb_push_log.event_type       IS '事件类型';
COMMENT ON COLUMN tb_push_log.success          IS '是否成功';
COMMENT ON COLUMN tb_push_log.status_code      IS 'HTTP状态码';
COMMENT ON COLUMN tb_push_log.message          IS '结果消息';
COMMENT ON COLUMN tb_push_log.response_body    IS '响应体(截断)';
COMMENT ON COLUMN tb_push_log.device_key       IS '触发设备';
COMMENT ON COLUMN tb_push_log.duration_ms      IS '耗时(毫秒)';

CREATE INDEX idx_push_log_config_id ON tb_push_log(push_config_id);
CREATE INDEX idx_push_log_create_time ON tb_push_log(create_time);
