-- Sprint 2: Audit log table
CREATE TABLE IF NOT EXISTS tb_audit_log (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50),
    action VARCHAR(50) NOT NULL,
    resource VARCHAR(100),
    resource_id VARCHAR(50),
    detail TEXT,
    ip VARCHAR(50),
    create_time TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_audit_log_username ON tb_audit_log(username);
CREATE INDEX IF NOT EXISTS idx_audit_log_create_time ON tb_audit_log(create_time);
