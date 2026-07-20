-- Migration: HTTP webhook ingress (P2-4)
-- Third-party platforms push data to Simple IoT via HTTP POST with HMAC verification.

CREATE TABLE IF NOT EXISTS tb_webhook_ingress (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    token       VARCHAR(64) NOT NULL UNIQUE,
    secret      VARCHAR(64) NOT NULL,
    device_id   INTEGER NOT NULL,
    enabled     BOOLEAN NOT NULL DEFAULT true,
    description VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_webhook_ingress_device ON tb_webhook_ingress(device_id);
