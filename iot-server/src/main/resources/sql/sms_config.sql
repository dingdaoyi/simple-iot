-- 短信配置表
CREATE TABLE IF NOT EXISTS sms_config (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    supplier VARCHAR(50) NOT NULL,
    access_key VARCHAR(200) NOT NULL,
    secret_key VARCHAR(200) NOT NULL,
    sign_name VARCHAR(100),
    template_id VARCHAR(100),
    endpoint VARCHAR(200),
    region VARCHAR(50),
    config_json TEXT,
    is_default BOOLEAN DEFAULT FALSE,
    status INTEGER DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建唯一索引确保只有一个默认配置
CREATE UNIQUE INDEX IF NOT EXISTS idx_sms_config_default ON sms_config (is_default) WHERE is_default = TRUE;

-- 插入默认配置
INSERT INTO sms_config (name, supplier, access_key, secret_key, sign_name, template_id, is_default, status) 
VALUES ('默认腾讯云短信', 'tencent', 'AKIDPuAaI6R4D5sQtCTedQ196HE4qgPv9yjP', 'EQQAYpxxnU0KSo4FKf6Zt42nMhFPOHoF', '消智云', 'SMS_123456789', TRUE, 1)
ON CONFLICT DO NOTHING;