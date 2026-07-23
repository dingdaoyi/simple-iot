-- Sprint 3: IM push config + device parent_id for topology

CREATE TABLE IF NOT EXISTS tb_im_push_config (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    platform    VARCHAR(20)   NOT NULL,  -- DINGTALK / WECOM / FEISHU
    webhook_url VARCHAR(500)  NOT NULL,
    secret      VARCHAR(200),
    is_enabled  BOOLEAN       NOT NULL DEFAULT TRUE,
    description VARCHAR(500),
    create_time TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

-- R6: device topology - parent device for gateway/sub-device relationships
ALTER TABLE tb_device ADD COLUMN IF NOT EXISTS parent_id INTEGER;
CREATE INDEX IF NOT EXISTS idx_device_parent_id ON tb_device(parent_id);
