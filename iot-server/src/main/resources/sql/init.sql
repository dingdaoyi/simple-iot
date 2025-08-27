
-- ----------------------------
-- Sequence structure for icon_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."icon_id_seq";
CREATE SEQUENCE "public"."icon_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for iot_driver_driver_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."iot_driver_driver_id_seq";
CREATE SEQUENCE "public"."iot_driver_driver_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for message_receive_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."message_receive_id_seq";
CREATE SEQUENCE "public"."message_receive_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for product_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."product_id_seq";
CREATE SEQUENCE "public"."product_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for product_tyoe_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."product_tyoe_id_seq";
CREATE SEQUENCE "public"."product_tyoe_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for protocol_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."protocol_id_seq";
CREATE SEQUENCE "public"."protocol_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sms_config_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sms_config_id_seq";
CREATE SEQUENCE "public"."sms_config_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sms_template_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sms_template_id_seq";
CREATE SEQUENCE "public"."sms_template_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for tb_device_command_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."tb_device_command_id_seq";
CREATE SEQUENCE "public"."tb_device_command_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for tb_device_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."tb_device_id_seq";
CREATE SEQUENCE "public"."tb_device_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for tb_dict_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."tb_dict_id_seq";
CREATE SEQUENCE "public"."tb_dict_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for tb_iot_rule_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."tb_iot_rule_id_seq";
CREATE SEQUENCE "public"."tb_iot_rule_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for tb_model_property_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."tb_model_property_id_seq";
CREATE SEQUENCE "public"."tb_model_property_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for tb_model_service_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."tb_model_service_id_seq";
CREATE SEQUENCE "public"."tb_model_service_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for tb_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."tb_user_id_seq";
CREATE SEQUENCE "public"."tb_user_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Table structure for sms_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."sms_config";
CREATE TABLE "public"."sms_config" (
  "id" int4 NOT NULL DEFAULT nextval('sms_config_id_seq'::regclass),
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "supplier" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "access_key" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "secret_key" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "sign_name" varchar(100) COLLATE "pg_catalog"."default",
  "template_id" varchar(100) COLLATE "pg_catalog"."default",
  "endpoint" varchar(200) COLLATE "pg_catalog"."default",
  "region" varchar(50) COLLATE "pg_catalog"."default",
  "config_json" json,
  "is_default" bool DEFAULT false,
  "status" int4 DEFAULT 1,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."sms_config" OWNER TO "sample";

-- ----------------------------
-- Records of sms_config
-- ----------------------------
-- ----------------------------
-- Table structure for sms_template
-- ----------------------------
DROP TABLE IF EXISTS "public"."sms_template";
CREATE TABLE "public"."sms_template" (
  "id" int4 NOT NULL DEFAULT nextval('sms_template_id_seq'::regclass),
  "config_id" int4 NOT NULL,
  "template_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "template_id" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "template_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "template_content" text COLLATE "pg_catalog"."default",
  "status" int4 DEFAULT 1,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."sms_template" OWNER TO "sample";

-- ----------------------------
-- Records of sms_template
-- ----------------------------

-- ----------------------------
-- Table structure for tb_device
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_device";
CREATE TABLE "public"."tb_device" (
  "id" int4 NOT NULL DEFAULT nextval('tb_device_id_seq'::regclass),
  "product_id" int4 NOT NULL DEFAULT '-1'::integer,
  "device_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "device_key" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "device_secret" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "online" bool DEFAULT false,
  "active_status" bool DEFAULT false,
  "parentid" int4 NOT NULL DEFAULT '-1'::integer,
  "create_time" timestamp(6),
  "update_time" timestamp(6),
  "third_device_id" varchar(64) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."tb_device" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_device"."id" IS '设备编号';
COMMENT ON COLUMN "public"."tb_device"."product_id" IS '产品id';
COMMENT ON COLUMN "public"."tb_device"."device_name" IS '设备名称';
COMMENT ON COLUMN "public"."tb_device"."device_key" IS '设备key';
COMMENT ON COLUMN "public"."tb_device"."device_secret" IS '设备密钥';
COMMENT ON COLUMN "public"."tb_device"."online" IS '在线状态:false 离线.true 在线';
COMMENT ON COLUMN "public"."tb_device"."active_status" IS '激活状态';
COMMENT ON COLUMN "public"."tb_device"."parentid" IS '父级id';
COMMENT ON COLUMN "public"."tb_device"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."tb_device"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."tb_device"."third_device_id" IS '第三方id';

-- ----------------------------
-- Records of tb_device
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_device" ("id", "product_id", "device_name", "device_key", "device_secret", "online", "active_status", "parentid", "create_time", "update_time", "third_device_id") VALUES (1, 1, '压力探测器', '12312321312', 'v3mqITcQpms4SAerMmka', 't', 't', -1, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for tb_device_command
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_device_command";
CREATE TABLE "public"."tb_device_command" (
  "id" int8 NOT NULL DEFAULT nextval('tb_device_command_id_seq'::regclass),
  "create_time" timestamp(6) NOT NULL DEFAULT '1000-01-01 00:00:00'::timestamp without time zone,
  "update_time" timestamp(6) NOT NULL DEFAULT '1000-01-01 00:00:00'::timestamp without time zone,
  "identifier" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "proto_key" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "device_key" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "input_params" json,
  "result_data" json,
  "status" int4 DEFAULT 0,
  "product_key" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying
)
;
ALTER TABLE "public"."tb_device_command" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_device_command"."id" IS 'id';
COMMENT ON COLUMN "public"."tb_device_command"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."tb_device_command"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."tb_device_command"."identifier" IS '标识符';
COMMENT ON COLUMN "public"."tb_device_command"."proto_key" IS '协议key';
COMMENT ON COLUMN "public"."tb_device_command"."device_key" IS '设备编号';
COMMENT ON COLUMN "public"."tb_device_command"."input_params" IS '请求参数';
COMMENT ON COLUMN "public"."tb_device_command"."result_data" IS '指令结果';
COMMENT ON COLUMN "public"."tb_device_command"."status" IS '完成状态';
COMMENT ON COLUMN "public"."tb_device_command"."product_key" IS '产品key';

-- ----------------------------
-- Records of tb_device_command
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (29, '2025-06-23 17:23:25.116156', '2025-06-23 17:23:25.119682', 'canmand', 'system-default', '12312321312', '{"pressure":"1"}', '{}', 0, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (30, '2025-06-23 17:58:28.789587', '2025-06-23 17:58:28.792906', 'canmand', 'system-default', '12312321312', '{"pressure":"1"}', '{}', 0, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (31, '2025-06-23 17:59:13.297899', '2025-06-23 17:59:13.29831', 'canmand', 'system-default', '12312321312', '{"pressure":"1"}', '{}', 0, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (32, '2025-06-23 18:06:31.272481', '2025-06-23 18:06:31.273522', 'canmand', 'system-default', '12312321312', '{"pressure":"1"}', '{}', 0, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (33, '2025-06-23 18:07:29.075976', '2025-06-23 18:07:29.076156', 'customservice', 'system-default', '12312321312', '{}', '{}', 1, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (34, '2025-06-23 18:08:19.970989', '2025-06-23 18:08:19.971202', 'customservice', 'system-default', '12312321312', '{}', '{}', 1, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (35, '2025-06-23 18:10:24.426944', '2025-06-23 18:10:24.42902', 'customservice', 'system-default', '12312321312', '{}', '{}', 1, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (36, '2025-06-23 18:10:49.964724', '2025-06-23 18:10:49.964896', 'customservice', 'system-default', '12312321312', '{}', '{}', 1, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (37, '2025-06-23 18:11:00.236628', '2025-06-23 18:11:00.236805', 'customservice', 'system-default', '12312321312', '{}', '{}', 1, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (38, '2025-06-23 18:12:43.160102', '2025-06-23 18:12:43.161512', 'customservice', 'system-default', '12312321312', '{}', '{}', 1, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (39, '2025-06-23 18:13:22.899501', '2025-06-23 18:13:22.899987', 'customservice', 'system-default', '12312321312', '{}', '{}', 1, 'B29Gx0OpapnOtPrQXtb4');
INSERT INTO "public"."tb_device_command" ("id", "create_time", "update_time", "identifier", "proto_key", "device_key", "input_params", "result_data", "status", "product_key") VALUES (40, '2025-06-23 18:13:37.537859', '2025-06-23 18:13:37.538069', 'customservice', 'system-default', '12312321312', '{}', '{}', 1, 'B29Gx0OpapnOtPrQXtb4');
COMMIT;

-- ----------------------------
-- Table structure for tb_dict
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_dict";
CREATE TABLE "public"."tb_dict" (
  "id" int8 NOT NULL DEFAULT nextval('tb_dict_id_seq'::regclass),
  "dict_group" varchar(255) COLLATE "pg_catalog"."default",
  "dict_label" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "dict_value" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "sort" int4 DEFAULT 0,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
ALTER TABLE "public"."tb_dict" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_dict"."id" IS '主键';
COMMENT ON COLUMN "public"."tb_dict"."dict_group" IS '字典编码';
COMMENT ON COLUMN "public"."tb_dict"."dict_label" IS '字典键';
COMMENT ON COLUMN "public"."tb_dict"."dict_value" IS '字典值';
COMMENT ON COLUMN "public"."tb_dict"."remark" IS '备注信息';
COMMENT ON COLUMN "public"."tb_dict"."sort" IS '排序字段';
COMMENT ON COLUMN "public"."tb_dict"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."tb_dict"."update_time" IS '创建时间';
COMMENT ON TABLE "public"."tb_dict" IS '用字典表';

-- ----------------------------
-- Records of tb_dict
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (3, 'analog_quantity', '事件计数', '件', '事件计数', 1, '2024-12-12 13:41:06.33492', '2024-12-12 13:41:06.33492');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (19, 'analog_quantity', '无功功率', 'KW', '无功功率', 17, '2024-12-12 13:41:08.406747', '2024-12-12 13:41:08.406747');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (9, 'analog_quantity', '时间', 's', '时间', 7, '2024-12-12 13:41:07.12163', '2024-12-12 13:41:07.12163');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (17, 'analog_quantity', '通用', '通用', '通用', 0, '2024-12-12 13:41:08.148748', '2024-12-12 13:41:08.148748');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (7, 'analog_quantity', '压力(kPa)', 'kPa', '压力(kPa)', 4, '2024-12-12 13:41:06.865904', '2024-12-12 13:41:06.865904');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (12, 'analog_quantity', '流量', 'L/s', '流量', 10, '2024-12-12 13:41:07.504495', '2024-12-12 13:41:07.504495');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (6, 'analog_quantity', '压力(MPa)', 'MPa', '压力(MPa)', 5, '2024-12-12 13:41:06.735772', '2024-12-12 13:41:06.735772');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (5, 'analog_quantity', '温度', '℃', '温度', 3, '2024-12-12 13:41:06.607154', '2024-12-12 13:41:06.607154');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (10, 'analog_quantity', '电压', 'V', '电压', 8, '2024-12-12 13:41:07.249512', '2024-12-12 13:41:07.249512');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (4, 'analog_quantity', '高度', 'm', '高度', 2, '2024-12-12 13:41:06.47364', '2024-12-12 13:41:06.47364');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (18, 'analog_quantity', '有功电量', 'KW.H', '有功电量', 15, '2024-12-12 13:41:08.278119', '2024-12-12 13:41:08.278119');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (16, 'analog_quantity', '相对湿度', '%', '相对湿度', 14, '2024-12-12 13:41:08.022004', '2024-12-12 13:41:08.022004');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (15, 'analog_quantity', '电流', 'mA', '电流', 13, '2024-12-12 13:41:07.893001', '2024-12-12 13:41:07.893001');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (14, 'analog_quantity', '风速', 'm/s', '风速', 12, '2024-12-12 13:41:07.764374', '2024-12-12 13:41:07.764374');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (13, 'analog_quantity', '风速', 'm3/min', '风速', 12, '2024-12-12 13:41:07.635457', '2024-12-12 13:41:07.635457');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (11, 'analog_quantity', '电流', 'A', '电流', 9, '2024-12-12 13:41:07.377009', '2024-12-12 13:41:07.377009');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (8, 'analog_quantity', '气体浓度', '%LEL', '气体浓度', 6, '2024-12-12 13:41:06.993386', '2024-12-12 13:41:06.993386');
INSERT INTO "public"."tb_dict" ("id", "dict_group", "dict_label", "dict_value", "remark", "sort", "create_time", "update_time") VALUES (1, 'analog_quantity', '-1', '计量单位', '计量单位', 1, '2024-12-12 13:34:53.924621', '2024-12-12 13:34:53.924621');
COMMIT;

-- ----------------------------
-- Table structure for tb_icon
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_icon";
CREATE TABLE "public"."tb_icon" (
  "id" int4 NOT NULL DEFAULT nextval('icon_id_seq'::regclass),
  "name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "path" varchar(128) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying
)
;
ALTER TABLE "public"."tb_icon" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_icon"."id" IS 'id';
COMMENT ON COLUMN "public"."tb_icon"."name" IS '名称备注';
COMMENT ON COLUMN "public"."tb_icon"."path" IS '地址';

-- ----------------------------
-- Records of tb_icon
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_icon" ("id", "name", "path") VALUES (4, 'ceer', '/iot/file/TvawAuymQd_消防压力.png');
INSERT INTO "public"."tb_icon" ("id", "name", "path") VALUES (5, 'ferfre', '/iot/file/ysmzmkvjQO_消防压力.png');
COMMIT;

-- ----------------------------
-- Table structure for tb_iot_driver
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_iot_driver";
CREATE TABLE "public"."tb_iot_driver" (
  "driver_id" int4 NOT NULL DEFAULT nextval('iot_driver_driver_id_seq'::regclass),
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "description" varchar(255) COLLATE "pg_catalog"."default",
  "connection_type" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "jar_path" varchar(255) COLLATE "pg_catalog"."default",
  "is_default" bool DEFAULT false,
  "status" int4 DEFAULT 1,
  "port" int4
)
;
ALTER TABLE "public"."tb_iot_driver" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_iot_driver"."port" IS '端口';

-- ----------------------------
-- Records of tb_iot_driver
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_iot_driver" ("driver_id", "name", "type", "description", "connection_type", "jar_path", "is_default", "status", "port") VALUES (1, 'GB/T-28181', 'TCP', '国标协议接入', 'DEFAULT', NULL, 'f', 1, 1677);
INSERT INTO "public"."tb_iot_driver" ("driver_id", "name", "type", "description", "connection_type", "jar_path", "is_default", "status", "port") VALUES (2, 'AEP', 'HTTP', '电信aep推送', 'DEFAULT', NULL, 'f', 1, NULL);
COMMIT;

-- ----------------------------
-- Table structure for tb_iot_rule
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_iot_rule";
CREATE TABLE "public"."tb_iot_rule" (
  "id" int4 NOT NULL DEFAULT nextval('tb_iot_rule_id_seq'::regclass),
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "rule_type" int4 NOT NULL DEFAULT '-1'::integer,
  "script" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "remark" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "input_type" int4 NOT NULL DEFAULT '-1'::integer,
  "source_type" int4 NOT NULL DEFAULT '-1'::integer,
  "source_id" int4 NOT NULL DEFAULT '-1'::integer,
  "target_type" int4 NOT NULL DEFAULT '-1'::integer,
  "target_id" int4 NOT NULL DEFAULT '-1'::integer,
  "identifier" varchar(50) COLLATE "pg_catalog"."default" DEFAULT ''::character varying
)
;
ALTER TABLE "public"."tb_iot_rule" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_iot_rule"."id" IS '规则id';
COMMENT ON COLUMN "public"."tb_iot_rule"."name" IS '规则名称';
COMMENT ON COLUMN "public"."tb_iot_rule"."rule_type" IS '处理类型';
COMMENT ON COLUMN "public"."tb_iot_rule"."script" IS '处理脚本';
COMMENT ON COLUMN "public"."tb_iot_rule"."remark" IS '备注';
COMMENT ON COLUMN "public"."tb_iot_rule"."input_type" IS '输入类型';
COMMENT ON COLUMN "public"."tb_iot_rule"."source_type" IS '源数据类型,1,产品;2,设备分组,3, 特定设备';
COMMENT ON COLUMN "public"."tb_iot_rule"."source_id" IS '数据源id';
COMMENT ON COLUMN "public"."tb_iot_rule"."target_type" IS '目标类型, 1: http, 2,mqtt; 3,message';
COMMENT ON COLUMN "public"."tb_iot_rule"."target_id" IS '目标id';
COMMENT ON COLUMN "public"."tb_iot_rule"."identifier" IS '标识符';

-- ----------------------------
-- Records of tb_iot_rule
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_iot_rule" ("id", "name", "rule_type", "script", "remark", "input_type", "source_type", "source_id", "target_type", "target_id", "identifier") VALUES (3, '压力过高推送模板', 1, '#value>3', '测试', 1, 3, 1, 3, 1, 'pressure');
COMMIT;

-- ----------------------------
-- Table structure for tb_message_receive
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_message_receive";
CREATE TABLE "public"."tb_message_receive" (
  "id" int4 NOT NULL DEFAULT nextval('message_receive_id_seq'::regclass),
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "receiver" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "notify_type" int4 NOT NULL DEFAULT '-1'::integer,
  "remark" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying
)
;
ALTER TABLE "public"."tb_message_receive" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_message_receive"."id" IS 'id';
COMMENT ON COLUMN "public"."tb_message_receive"."name" IS '接收者名称';
COMMENT ON COLUMN "public"."tb_message_receive"."receiver" IS '接收对象';
COMMENT ON COLUMN "public"."tb_message_receive"."notify_type" IS '通知类型';
COMMENT ON COLUMN "public"."tb_message_receive"."remark" IS '备注';

-- ----------------------------
-- Records of tb_message_receive
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_message_receive" ("id", "name", "receiver", "notify_type", "remark") VALUES (1, '岩冰', '1493001032@qq.com', 1, '12312frefer');
INSERT INTO "public"."tb_message_receive" ("id", "name", "receiver", "notify_type", "remark") VALUES (2, '短信测试', '18180422217', 2, '测试');
COMMIT;

-- ----------------------------
-- Table structure for tb_model_property
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_model_property";
CREATE TABLE "public"."tb_model_property" (
  "id" int4 NOT NULL DEFAULT nextval('tb_model_property_id_seq'::regclass),
  "data_type" int4,
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "identifier" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(64) COLLATE "pg_catalog"."default",
  "access_mode" char(2) COLLATE "pg_catalog"."default",
  "product_type_id" int4,
  "icon_id" int4,
  "custom" bool,
  "bool0" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "bool1" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "length" int4 DEFAULT 0,
  "param_type" int4 DEFAULT 1,
  "unit" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "unit_name" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "max" int8 DEFAULT 0,
  "min" int8 DEFAULT 0,
  "step" int8 DEFAULT 0,
  "enums" json DEFAULT '[]'::json,
  "product_id" int4,
  "parent_id" int4 DEFAULT '-1'::integer
)
;
ALTER TABLE "public"."tb_model_property" OWNER TO "postgres";
COMMENT ON COLUMN "public"."tb_model_property"."id" IS 'id';
COMMENT ON COLUMN "public"."tb_model_property"."data_type" IS '数据类型';
COMMENT ON COLUMN "public"."tb_model_property"."remark" IS '备注';
COMMENT ON COLUMN "public"."tb_model_property"."identifier" IS '标识符';
COMMENT ON COLUMN "public"."tb_model_property"."name" IS '属性名称';
COMMENT ON COLUMN "public"."tb_model_property"."access_mode" IS '访问权限:r,只读,rw读写';
COMMENT ON COLUMN "public"."tb_model_property"."product_type_id" IS '产品类型';
COMMENT ON COLUMN "public"."tb_model_property"."icon_id" IS '图标';
COMMENT ON COLUMN "public"."tb_model_property"."custom" IS '是否为自定义属性';
COMMENT ON COLUMN "public"."tb_model_property"."bool0" IS '布尔类型时,false对应的值';
COMMENT ON COLUMN "public"."tb_model_property"."bool1" IS '布尔类型时,true对应的值';
COMMENT ON COLUMN "public"."tb_model_property"."length" IS '字符串类型时,长度';
COMMENT ON COLUMN "public"."tb_model_property"."param_type" IS '参数类型,1属性, 2 参数(事件和服务的出入参数)';
COMMENT ON COLUMN "public"."tb_model_property"."unit" IS '计量单位';
COMMENT ON COLUMN "public"."tb_model_property"."unit_name" IS '计量单位名称';
COMMENT ON COLUMN "public"."tb_model_property"."max" IS '最大值';
COMMENT ON COLUMN "public"."tb_model_property"."min" IS '最小值';
COMMENT ON COLUMN "public"."tb_model_property"."step" IS '步长';
COMMENT ON COLUMN "public"."tb_model_property"."enums" IS '枚举参对照表';
COMMENT ON COLUMN "public"."tb_model_property"."product_id" IS '产品id';
COMMENT ON COLUMN "public"."tb_model_property"."parent_id" IS '父级id';

-- ----------------------------
-- Records of tb_model_property
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_model_property" ("id", "data_type", "remark", "identifier", "name", "access_mode", "product_type_id", "icon_id", "custom", "bool0", "bool1", "length", "param_type", "unit", "unit_name", "max", "min", "step", "enums", "product_id", "parent_id") VALUES (3, 4, '测试', 'enumte', '枚举测试', 'r ', 1, NULL, 'f', '', '', 0, 1, '', '', 0, 0, 0, '[{"key":1,"value":"告警"},{"key":2,"value":"测试"}]', NULL, -1);
INSERT INTO "public"."tb_model_property" ("id", "data_type", "remark", "identifier", "name", "access_mode", "product_type_id", "icon_id", "custom", "bool0", "bool1", "length", "param_type", "unit", "unit_name", "max", "min", "step", "enums", "product_id", "parent_id") VALUES (2, 1, 'rfer', 'temper', '温度', 'r ', 1, 5, 'f', '', '', 0, 1, '℃', '温度', 1000, -40, 1, '[{"key":1,"value":""}]', NULL, -1);
INSERT INTO "public"."tb_model_property" ("id", "data_type", "remark", "identifier", "name", "access_mode", "product_type_id", "icon_id", "custom", "bool0", "bool1", "length", "param_type", "unit", "unit_name", "max", "min", "step", "enums", "product_id", "parent_id") VALUES (6, 1, 'ce', 'levele', '液位自定义', 'r ', 1, 4, 'f', '', '', 0, 1, 'm', '高度', 100, 0, 1, '[{"key":1,"value":""}]', 1, -1);
INSERT INTO "public"."tb_model_property" ("id", "data_type", "remark", "identifier", "name", "access_mode", "product_type_id", "icon_id", "custom", "bool0", "bool1", "length", "param_type", "unit", "unit_name", "max", "min", "step", "enums", "product_id", "parent_id") VALUES (1, 1, '压力', 'pressure', '压力', 'r ', 1, 5, 'f', '', '', 0, 1, 'MPa', '压力(MPa)', 100, 0, 1, '[]', NULL, -1);
COMMIT;

-- ----------------------------
-- Table structure for tb_model_service
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_model_service";
CREATE TABLE "public"."tb_model_service" (
  "id" int4 NOT NULL DEFAULT nextval('tb_model_service_id_seq'::regclass),
  "async" bool,
  "event_type" int4,
  "status" int4 DEFAULT 0,
  "service_type" int4,
  "identifier" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "remark" varchar(255) COLLATE "pg_catalog"."default",
  "product_type_id" int4,
  "icon_id" int4,
  "custom" bool,
  "required" bool,
  "product_id" int4
)
;
ALTER TABLE "public"."tb_model_service" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_model_service"."id" IS 'id';
COMMENT ON COLUMN "public"."tb_model_service"."async" IS '服务是否为异步';
COMMENT ON COLUMN "public"."tb_model_service"."event_type" IS '事件类型,INFO,WARN,FAULT';
COMMENT ON COLUMN "public"."tb_model_service"."status" IS '功能状态,1 启用,0禁用';
COMMENT ON COLUMN "public"."tb_model_service"."identifier" IS '标识符';
COMMENT ON COLUMN "public"."tb_model_service"."name" IS '名称';
COMMENT ON COLUMN "public"."tb_model_service"."remark" IS '备注';
COMMENT ON COLUMN "public"."tb_model_service"."product_type_id" IS '产品类型id';
COMMENT ON COLUMN "public"."tb_model_service"."icon_id" IS '图标id';
COMMENT ON COLUMN "public"."tb_model_service"."custom" IS '是否为自定义';
COMMENT ON COLUMN "public"."tb_model_service"."required" IS '是否必选';
COMMENT ON COLUMN "public"."tb_model_service"."product_id" IS '产品id';
COMMENT ON TABLE "public"."tb_model_service" IS '物模型服务表';

-- ----------------------------
-- Records of tb_model_service
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_model_service" ("id", "async", "event_type", "status", "service_type", "identifier", "name", "remark", "product_type_id", "icon_id", "custom", "required", "product_id") VALUES (1, 't', 1, 1, 2, 'heartbeat', '心跳', '心跳', 1, NULL, 'f', 't', NULL);
INSERT INTO "public"."tb_model_service" ("id", "async", "event_type", "status", "service_type", "identifier", "name", "remark", "product_type_id", "icon_id", "custom", "required", "product_id") VALUES (3, 't', 1, 0, 2, 'alarm', '告警', '测试告警', 1, NULL, 'f', 'f', NULL);
INSERT INTO "public"."tb_model_service" ("id", "async", "event_type", "status", "service_type", "identifier", "name", "remark", "product_type_id", "icon_id", "custom", "required", "product_id") VALUES (4, NULL, NULL, 0, 2, 'fault', '故障', '测试', 1, NULL, 'f', 'f', NULL);
INSERT INTO "public"."tb_model_service" ("id", "async", "event_type", "status", "service_type", "identifier", "name", "remark", "product_type_id", "icon_id", "custom", "required", "product_id") VALUES (2, 't', 1, 0, 1, 'canmand', '指令下发', '指令下发', 1, NULL, 'f', 'f', NULL);
INSERT INTO "public"."tb_model_service" ("id", "async", "event_type", "status", "service_type", "identifier", "name", "remark", "product_type_id", "icon_id", "custom", "required", "product_id") VALUES (10, 'f', NULL, 0, 1, 'customservice', '自定义服务', '测试', 1, NULL, 't', 'f', 1);
COMMIT;

-- ----------------------------
-- Table structure for tb_product
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_product";
CREATE TABLE "public"."tb_product" (
  "id" int4 NOT NULL DEFAULT nextval('product_id_seq'::regclass),
  "model" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "manufacturer" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "remark" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "protocol_id" int4,
  "product_type_id" int4,
  "product_key" char(20) COLLATE "pg_catalog"."default" DEFAULT ''::bpchar
)
;
ALTER TABLE "public"."tb_product" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_product"."id" IS '产品id';
COMMENT ON COLUMN "public"."tb_product"."model" IS '产品型号';
COMMENT ON COLUMN "public"."tb_product"."manufacturer" IS '厂家';
COMMENT ON COLUMN "public"."tb_product"."remark" IS '描述';
COMMENT ON COLUMN "public"."tb_product"."protocol_id" IS '协议id';
COMMENT ON COLUMN "public"."tb_product"."product_key" IS '产品key';

-- ----------------------------
-- Records of tb_product
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_product" ("id", "model", "manufacturer", "remark", "protocol_id", "product_type_id", "product_key") VALUES (1, 'mqtt-test', '岩冰', '测试压力', 1, 1, 'B29Gx0OpapnOtPrQXtb4');
COMMIT;

-- ----------------------------
-- Table structure for tb_product_type
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_product_type";
CREATE TABLE "public"."tb_product_type" (
  "id" int4 NOT NULL DEFAULT nextval('product_tyoe_id_seq'::regclass),
  "name" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "parent_id" int4 NOT NULL DEFAULT '-1'::integer,
  "status" int4 NOT NULL DEFAULT 2,
  "icon_url" varchar(255) COLLATE "pg_catalog"."default",
  "mark" varchar(255) COLLATE "pg_catalog"."default" DEFAULT ''::character varying,
  "part_type_code" varchar(64) COLLATE "pg_catalog"."default" DEFAULT ''::character varying
)
;
ALTER TABLE "public"."tb_product_type" OWNER TO "postgres";
COMMENT ON COLUMN "public"."tb_product_type"."id" IS 'id';
COMMENT ON COLUMN "public"."tb_product_type"."name" IS '产品类型名称';
COMMENT ON COLUMN "public"."tb_product_type"."parent_id" IS '父级类型id';
COMMENT ON COLUMN "public"."tb_product_type"."status" IS '1,启用,2 禁用';
COMMENT ON COLUMN "public"."tb_product_type"."icon_url" IS '图标路径';
COMMENT ON COLUMN "public"."tb_product_type"."mark" IS '描述';
COMMENT ON COLUMN "public"."tb_product_type"."part_type_code" IS '类型code,在多级别网关设备协议中使用';
COMMENT ON TABLE "public"."tb_product_type" IS '品类';

-- ----------------------------
-- Records of tb_product_type
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_product_type" ("id", "name", "parent_id", "status", "icon_url", "mark", "part_type_code") VALUES (1, '压力传感器', -1, 1, NULL, '测试用传', '136');
INSERT INTO "public"."tb_product_type" ("id", "name", "parent_id", "status", "icon_url", "mark", "part_type_code") VALUES (2, '测试2', -1, 2, NULL, '测试', '');
COMMIT;

-- ----------------------------
-- Table structure for tb_protocol
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_protocol";
CREATE TABLE "public"."tb_protocol" (
  "id" int4 NOT NULL DEFAULT nextval('protocol_id_seq'::regclass),
  "name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "proto_type" int2 NOT NULL DEFAULT 2,
  "url" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "mark" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "proto_key" varchar(50) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  "handler_class" varchar(255) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying
)
;
ALTER TABLE "public"."tb_protocol" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_protocol"."id" IS 'id';
COMMENT ON COLUMN "public"."tb_protocol"."name" IS '协议名称';
COMMENT ON COLUMN "public"."tb_protocol"."proto_type" IS '协议类型';
COMMENT ON COLUMN "public"."tb_protocol"."url" IS '协议包地址';
COMMENT ON COLUMN "public"."tb_protocol"."mark" IS '协议描述';
COMMENT ON COLUMN "public"."tb_protocol"."proto_key" IS '协议key';

-- ----------------------------
-- Records of tb_protocol
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_protocol" ("id", "name", "proto_type", "url", "mark", "proto_key", "handler_class") VALUES (1, '系统协议', 2, '', '系统默认协议', 'system-default', 'com.github.dingdaoyi.iot.proto.defaul.DefaultProtocolDecoder');
COMMIT;

-- ----------------------------
-- Table structure for tb_service_property
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_service_property";
CREATE TABLE "public"."tb_service_property" (
  "service_id" int4,
  "property_id" int4,
  "param_type" int4
)
;
ALTER TABLE "public"."tb_service_property" OWNER TO "sample";
COMMENT ON COLUMN "public"."tb_service_property"."service_id" IS '服务id';
COMMENT ON COLUMN "public"."tb_service_property"."property_id" IS '属性id';
COMMENT ON COLUMN "public"."tb_service_property"."param_type" IS '1:出参数,2:入参';
COMMENT ON TABLE "public"."tb_service_property" IS '服务关联属性表';

-- ----------------------------
-- Records of tb_service_property
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_service_property" ("service_id", "property_id", "param_type") VALUES (1, 1, 1);
INSERT INTO "public"."tb_service_property" ("service_id", "property_id", "param_type") VALUES (3, 2, 1);
INSERT INTO "public"."tb_service_property" ("service_id", "property_id", "param_type") VALUES (4, 3, 1);
INSERT INTO "public"."tb_service_property" ("service_id", "property_id", "param_type") VALUES (2, 3, 1);
INSERT INTO "public"."tb_service_property" ("service_id", "property_id", "param_type") VALUES (2, 2, 1);
INSERT INTO "public"."tb_service_property" ("service_id", "property_id", "param_type") VALUES (2, 1, 2);
INSERT INTO "public"."tb_service_property" ("service_id", "property_id", "param_type") VALUES (10, 6, 1);
COMMIT;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."tb_user";
CREATE TABLE "public"."tb_user" (
  "id" int4 NOT NULL DEFAULT nextval('tb_user_id_seq'::regclass),
  "username" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "password" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "enabled" bool NOT NULL DEFAULT true,
  "account_non_expired" bool NOT NULL DEFAULT true,
  "account_non_locked" bool NOT NULL DEFAULT true,
  "credentials_non_expired" bool NOT NULL DEFAULT true
)
;
ALTER TABLE "public"."tb_user" OWNER TO "postgres";

-- ----------------------------
-- Records of tb_user
-- ----------------------------
BEGIN;
INSERT INTO "public"."tb_user" ("id", "username", "password", "enabled", "account_non_expired", "account_non_locked", "credentials_non_expired") VALUES (1, 'admin', '$2a$10$2HnXpeo7MPQZdDKWca489uylXm1T1VS7OKSYR2jeFgdOFwuIKJJtq', 't', 't', 't', 't');
COMMIT;

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."icon_id_seq"
OWNED BY "public"."tb_icon"."id";
SELECT setval('"public"."icon_id_seq"', 10, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."iot_driver_driver_id_seq"
OWNED BY "public"."tb_iot_driver"."driver_id";
SELECT setval('"public"."iot_driver_driver_id_seq"', 2, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."message_receive_id_seq"
OWNED BY "public"."tb_message_receive"."id";
SELECT setval('"public"."message_receive_id_seq"', 2, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."product_id_seq"
OWNED BY "public"."tb_product"."id";
SELECT setval('"public"."product_id_seq"', 2, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
SELECT setval('"public"."product_tyoe_id_seq"', 6, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."protocol_id_seq"
OWNED BY "public"."tb_protocol"."id";
SELECT setval('"public"."protocol_id_seq"', 5, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sms_config_id_seq"
OWNED BY "public"."sms_config"."id";
SELECT setval('"public"."sms_config_id_seq"', 1, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sms_template_id_seq"
OWNED BY "public"."sms_template"."id";
SELECT setval('"public"."sms_template_id_seq"', 1, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."tb_device_command_id_seq"
OWNED BY "public"."tb_device_command"."id";
SELECT setval('"public"."tb_device_command_id_seq"', 40, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."tb_device_id_seq"
OWNED BY "public"."tb_device"."id";
SELECT setval('"public"."tb_device_id_seq"', 2, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."tb_dict_id_seq"
OWNED BY "public"."tb_dict"."id";
SELECT setval('"public"."tb_dict_id_seq"', 19, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."tb_iot_rule_id_seq"
OWNED BY "public"."tb_iot_rule"."id";
SELECT setval('"public"."tb_iot_rule_id_seq"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."tb_model_property_id_seq"
OWNED BY "public"."tb_model_property"."id";
SELECT setval('"public"."tb_model_property_id_seq"', 6, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."tb_model_service_id_seq"
OWNED BY "public"."tb_model_service"."id";
SELECT setval('"public"."tb_model_service_id_seq"', 10, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."tb_user_id_seq"
OWNED BY "public"."tb_user"."id";
SELECT setval('"public"."tb_user_id_seq"', 1, true);

-- ----------------------------
-- Indexes structure for table sms_config
-- ----------------------------
CREATE UNIQUE INDEX "idx_sms_config_default" ON "public"."sms_config" USING btree (
  "is_default" "pg_catalog"."bool_ops" ASC NULLS LAST
) WHERE is_default = true;

-- ----------------------------
-- Primary Key structure for table sms_config
-- ----------------------------
ALTER TABLE "public"."sms_config" ADD CONSTRAINT "sms_config_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table sms_template
-- ----------------------------
CREATE UNIQUE INDEX "idx_sms_template_unique" ON "public"."sms_template" USING btree (
  "config_id" "pg_catalog"."int4_ops" ASC NULLS LAST,
  "template_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sms_template
-- ----------------------------
ALTER TABLE "public"."sms_template" ADD CONSTRAINT "sms_template_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table tb_device
-- ----------------------------
CREATE UNIQUE INDEX "ux_tb_device_device_key" ON "public"."tb_device" USING btree (
  "device_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table tb_device
-- ----------------------------
ALTER TABLE "public"."tb_device" ADD CONSTRAINT "tb_device_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table tb_device_command
-- ----------------------------
CREATE INDEX "ix_tb_device_command_create_time" ON "public"."tb_device_command" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "ix_tb_device_command_device_key" ON "public"."tb_device_command" USING btree (
  "device_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table tb_device_command
-- ----------------------------
ALTER TABLE "public"."tb_device_command" ADD CONSTRAINT "tb_device_command_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_dict
-- ----------------------------
ALTER TABLE "public"."tb_dict" ADD CONSTRAINT "tb_dict_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_icon
-- ----------------------------
ALTER TABLE "public"."tb_icon" ADD CONSTRAINT "icon_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_iot_driver
-- ----------------------------
ALTER TABLE "public"."tb_iot_driver" ADD CONSTRAINT "iot_driver_pkey" PRIMARY KEY ("driver_id");

-- ----------------------------
-- Indexes structure for table tb_iot_rule
-- ----------------------------
CREATE UNIQUE INDEX "ux_iot_rule_name" ON "public"."tb_iot_rule" USING btree (
  "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table tb_iot_rule
-- ----------------------------
ALTER TABLE "public"."tb_iot_rule" ADD CONSTRAINT "tb_iot_rule_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table tb_message_receive
-- ----------------------------
CREATE UNIQUE INDEX "ux_message_receive_name" ON "public"."tb_message_receive" USING btree (
  "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table tb_message_receive
-- ----------------------------
ALTER TABLE "public"."tb_message_receive" ADD CONSTRAINT "message_receive_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_model_property
-- ----------------------------
ALTER TABLE "public"."tb_model_property" ADD CONSTRAINT "tb_model_property_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Uniques structure for table tb_model_service
-- ----------------------------
ALTER TABLE "public"."tb_model_service" ADD CONSTRAINT "model_service_pk" UNIQUE ("identifier", "product_type_id");

-- ----------------------------
-- Primary Key structure for table tb_model_service
-- ----------------------------
ALTER TABLE "public"."tb_model_service" ADD CONSTRAINT "tb_model_service_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table tb_product
-- ----------------------------
CREATE UNIQUE INDEX "ux_product_manufacturer_model" ON "public"."tb_product" USING btree (
  "manufacturer" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "model" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "product_type_id" "pg_catalog"."int4_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table tb_product
-- ----------------------------
ALTER TABLE "public"."tb_product" ADD CONSTRAINT "product_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table tb_product_type
-- ----------------------------
ALTER TABLE "public"."tb_product_type" ADD CONSTRAINT "tb_product_type_pk" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table tb_protocol
-- ----------------------------
CREATE UNIQUE INDEX "ux_protocol_proto_key" ON "public"."tb_protocol" USING btree (
  "proto_key" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table tb_protocol
-- ----------------------------
ALTER TABLE "public"."tb_protocol" ADD CONSTRAINT "protocol_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table tb_service_property
-- ----------------------------
CREATE UNIQUE INDEX "tb_service_property_property_id_service_id_uindex" ON "public"."tb_service_property" USING btree (
  "property_id" "pg_catalog"."int4_ops" ASC NULLS LAST,
  "service_id" "pg_catalog"."int4_ops" ASC NULLS LAST,
  "param_type" "pg_catalog"."int4_ops" ASC NULLS LAST
);

-- ----------------------------
-- Uniques structure for table tb_user
-- ----------------------------
ALTER TABLE "public"."tb_user" ADD CONSTRAINT "tb_user_username_key" UNIQUE ("username");

-- ----------------------------
-- Primary Key structure for table tb_user
-- ----------------------------
ALTER TABLE "public"."tb_user" ADD CONSTRAINT "tb_user_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Keys structure for table sms_template
-- ----------------------------
ALTER TABLE "public"."sms_template" ADD CONSTRAINT "sms_template_config_id_fkey" FOREIGN KEY ("config_id") REFERENCES "public"."sms_config" ("id") ON DELETE CASCADE ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table tb_model_property
-- ----------------------------
ALTER TABLE "public"."tb_model_property" ADD CONSTRAINT "property_type_fk" FOREIGN KEY ("product_type_id") REFERENCES "public"."tb_product_type" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table tb_service_property
-- ----------------------------
ALTER TABLE "public"."tb_service_property" ADD CONSTRAINT "spfk_property" FOREIGN KEY ("property_id") REFERENCES "public"."tb_model_property" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."tb_service_property" ADD CONSTRAINT "spfk_service" FOREIGN KEY ("service_id") REFERENCES "public"."tb_model_service" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMENT ON CONSTRAINT "spfk_property" ON "public"."tb_service_property" IS '关联属性';
