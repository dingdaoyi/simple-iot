CREATE TABLE tb_user (
    id integer PRIMARY KEY,
    username varchar(50) NOT NULL,
    password varchar(255) NOT NULL,
    enabled boolean NOT NULL DEFAULT true,
    account_non_expired boolean NOT NULL DEFAULT true,
    account_non_locked boolean NOT NULL DEFAULT true,
    credentials_non_expired boolean NOT NULL DEFAULT true
);

CREATE TABLE sms_config (
    id integer PRIMARY KEY,
    name varchar(100) NOT NULL,
    supplier varchar(50) NOT NULL,
    access_key varchar(200) NOT NULL,
    secret_key varchar(200) NOT NULL,
    sign_name varchar(100),
    template_id varchar(100),
    endpoint varchar(200),
    region varchar(50),
    config_json text,
    is_default boolean NOT NULL DEFAULT false,
    status integer NOT NULL DEFAULT 1,
    create_time timestamp,
    update_time timestamp
);

CREATE TABLE tb_iot_driver (
    driver_id integer PRIMARY KEY,
    name varchar(100) NOT NULL,
    type varchar(50) NOT NULL,
    description varchar(255),
    connection_type varchar(50),
    jar_path varchar(255),
    is_default boolean NOT NULL DEFAULT false,
    status integer NOT NULL DEFAULT 1,
    port integer NOT NULL
);

CREATE TABLE tb_protocol (
    id integer PRIMARY KEY,
    name varchar(50) NOT NULL,
    proto_type smallint NOT NULL DEFAULT 2,
    url varchar(255) NOT NULL DEFAULT '',
    mark varchar(255) NOT NULL DEFAULT '',
    proto_key varchar(50) NOT NULL,
    handler_class varchar(255),
    script_content text,
    script_lang varchar(50),
    status integer NOT NULL DEFAULT 1
);

INSERT INTO tb_iot_driver (driver_id, name, type, description, connection_type, jar_path, is_default, status, port)
VALUES (1, 'Integration TCP Driver', 'TCP', 'Integration test driver', 'DEFAULT', NULL, false, 1, 1677);

INSERT INTO tb_protocol (id, name, proto_type, url, mark, proto_key, handler_class, script_content, script_lang, status)
VALUES (1, 'System Protocol', 2, '', 'Integration test protocol', 'system-default', 'com.github.dingdaoyi.iot.proto.defaul.DefaultProtocolDecoder', NULL, NULL, 1);
