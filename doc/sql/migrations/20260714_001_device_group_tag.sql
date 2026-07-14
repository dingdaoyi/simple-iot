-- Migration: device groups and tags (P1-2)

-- Device groups (tree structure)
CREATE TABLE IF NOT EXISTS tb_device_group (
    id          SERIAL PRIMARY KEY,
    parent_id   INTEGER     NOT NULL DEFAULT -1,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    create_time TIMESTAMP   NOT NULL DEFAULT now(),
    update_time TIMESTAMP   NOT NULL DEFAULT now()
);
COMMENT ON TABLE  tb_device_group IS '设备分组';
COMMENT ON COLUMN tb_device_group.parent_id IS '父分组ID, -1=根';
COMMENT ON COLUMN tb_device_group.name IS '分组名称';
COMMENT ON COLUMN tb_device_group.description IS '描述';

-- Device tags
CREATE TABLE IF NOT EXISTS tb_device_tag (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    color       VARCHAR(20)  DEFAULT '#6366f1',
    create_time TIMESTAMP   NOT NULL DEFAULT now(),
    update_time TIMESTAMP   NOT NULL DEFAULT now()
);
COMMENT ON TABLE  tb_device_tag IS '设备标签';
COMMENT ON COLUMN tb_device_tag.name IS '标签名称';
COMMENT ON COLUMN tb_device_tag.color IS '标签颜色';

-- Device ↔ Group (many-to-many)
CREATE TABLE IF NOT EXISTS tb_device_group_relation (
    id          SERIAL PRIMARY KEY,
    device_id   INTEGER NOT NULL,
    group_id    INTEGER NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT now()
);
COMMENT ON TABLE tb_device_group_relation IS '设备-分组关联';
CREATE UNIQUE INDEX IF NOT EXISTS uk_device_group ON tb_device_group_relation(device_id, group_id);
CREATE INDEX IF NOT EXISTS idx_dgr_group_id ON tb_device_group_relation(group_id);

-- Device ↔ Tag (many-to-many)
CREATE TABLE IF NOT EXISTS tb_device_tag_relation (
    id          SERIAL PRIMARY KEY,
    device_id   INTEGER NOT NULL,
    tag_id      INTEGER NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT now()
);
COMMENT ON TABLE tb_device_tag_relation IS '设备-标签关联';
CREATE UNIQUE INDEX IF NOT EXISTS uk_device_tag ON tb_device_tag_relation(device_id, tag_id);
CREATE INDEX IF NOT EXISTS idx_dtr_tag_id ON tb_device_tag_relation(tag_id);
