-- Migration: rule execution log (P1-4 v2)

CREATE TABLE IF NOT EXISTS tb_rule_execution_log (
    id              SERIAL PRIMARY KEY,
    rule_chain_id   INTEGER NOT NULL,
    device_key      VARCHAR(100),
    device_id       INTEGER,
    device_name     VARCHAR(100),
    message_type    VARCHAR(30),
    event_time      TIMESTAMP,
    success         BOOLEAN NOT NULL DEFAULT true,
    duration_ms     BIGINT,
    trace_count     INTEGER DEFAULT 0,
    traces          JSONB,
    input_snapshot  JSONB,
    create_time     TIMESTAMP NOT NULL DEFAULT now()
);

COMMENT ON TABLE  tb_rule_execution_log IS '规则链执行日志';
COMMENT ON COLUMN tb_rule_execution_log.rule_chain_id IS '规则链ID';
COMMENT ON COLUMN tb_rule_execution_log.device_key IS '设备Key';
COMMENT ON COLUMN tb_rule_execution_log.traces IS '执行轨迹(JSON)';
COMMENT ON COLUMN tb_rule_execution_log.input_snapshot IS '输入快照(JSON)';

CREATE INDEX idx_rule_exec_log_chain ON tb_rule_execution_log(rule_chain_id);
CREATE INDEX idx_rule_exec_log_device ON tb_rule_execution_log(device_key);
CREATE INDEX idx_rule_exec_log_time ON tb_rule_execution_log(create_time);
