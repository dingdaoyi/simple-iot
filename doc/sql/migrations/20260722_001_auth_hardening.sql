-- Migration: P0-4 authentication hardening
-- Add force_change_pwd column, update admin password to Admin@2026, force change on next login

ALTER TABLE tb_user ADD COLUMN IF NOT EXISTS force_change_pwd BOOLEAN NOT NULL DEFAULT false;

-- Update admin password to Admin@2026 (BCrypt hash)
UPDATE tb_user SET password = '$2a$10$6g2lLkAoNyU3e7Dfx2yfRe1I185SG52q/h.eB8WmyT.PJIxn0i6we',
                   force_change_pwd = true
WHERE username = 'admin';
