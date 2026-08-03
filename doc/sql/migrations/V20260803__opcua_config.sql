-- OPC UA 驱动配置表
CREATE TABLE IF NOT EXISTS tb_opcua_config (
    id              SERIAL PRIMARY KEY,
    device_id       INTEGER NOT NULL REFERENCES tb_device(id) ON DELETE CASCADE,
    endpoint_url    VARCHAR(500) NOT NULL,
    security_mode   VARCHAR(50) DEFAULT 'NONE',
    username        VARCHAR(100),
    password        VARCHAR(200),
    interval_ms     INTEGER DEFAULT 5000,
    node_map        TEXT DEFAULT '[]',
    enabled         BOOLEAN DEFAULT TRUE,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE tb_opcua_config IS 'OPC UA 设备配置';
COMMENT ON COLUMN tb_opcua_config.endpoint_url IS 'OPC UA 端点 URL, 如 opc.tcp://192.168.1.100:4840';
COMMENT ON COLUMN tb_opcua_config.security_mode IS '安全模式: NONE / SIGN / SIGN_ENCRYPT';
COMMENT ON COLUMN tb_opcua_config.node_map IS '节点映射 JSON: [{identifier, nodeId, dataType, scale}]';
