-- Migration: alarm comments (P1-3)

CREATE TABLE IF NOT EXISTS tb_alarm_comment (
    id          SERIAL PRIMARY KEY,
    alarm_id    INTEGER NOT NULL,
    comment     TEXT NOT NULL,
    action      VARCHAR(30) DEFAULT 'COMMENT',
    author      VARCHAR(100),
    create_time TIMESTAMP NOT NULL DEFAULT now()
);

COMMENT ON TABLE  tb_alarm_comment IS '告警评论/操作记录';
COMMENT ON COLUMN tb_alarm_comment.alarm_id IS '告警ID';
COMMENT ON COLUMN tb_alarm_comment.comment IS '评论内容';
COMMENT ON COLUMN tb_alarm_comment.action IS '操作类型: COMMENT, ACKNOWLEDGE, CLEAR, ESCALATE';
COMMENT ON COLUMN tb_alarm_comment.author IS '操作人';

CREATE INDEX idx_alarm_comment_alarm_id ON tb_alarm_comment(alarm_id);
