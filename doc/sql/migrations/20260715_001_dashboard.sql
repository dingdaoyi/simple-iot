-- Migration: custom dashboards (P1-5)

CREATE TABLE IF NOT EXISTS tb_dashboard (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    layout      JSONB NOT NULL DEFAULT '[]'::jsonb,
    is_default  BOOLEAN NOT NULL DEFAULT false,
    create_time TIMESTAMP NOT NULL DEFAULT now(),
    update_time TIMESTAMP NOT NULL DEFAULT now()
);

COMMENT ON COLUMN tb_dashboard.layout IS 'Array of widget configs: {id, type, title, x, y, w, h, config}';
