-- ============================================================================
-- Migration: add HMAC signing columns to tb_push_config
-- Issue / PR : #34 (feat: signed HTTP push testing)
-- Date       : 2026-06-22
-- Safe to run multiple times (IF NOT EXISTS guards).
-- ============================================================================

ALTER TABLE tb_push_config
    ADD COLUMN IF NOT EXISTS http_sign_enabled BOOLEAN DEFAULT FALSE;

ALTER TABLE tb_push_config
    ADD COLUMN IF NOT EXISTS http_sign_secret VARCHAR(255);

COMMENT ON COLUMN tb_push_config.http_sign_enabled IS '是否启用 HTTP HMAC 签名';
COMMENT ON COLUMN tb_push_config.http_sign_secret IS 'HTTP HMAC 签名密钥';
