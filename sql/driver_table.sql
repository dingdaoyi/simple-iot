-- 驱动管理表
CREATE TABLE IF NOT EXISTS `tb_iot_driver` (
  `driver_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '驱动ID',
  `name` varchar(100) NOT NULL COMMENT '驱动名称',
  `type` varchar(20) NOT NULL COMMENT '驱动类型：MQTT/TCP/UDP/HTTP/CUSTOM',
  `description` varchar(500) DEFAULT NULL COMMENT '驱动描述',
  `connection_type` varchar(20) NOT NULL COMMENT '连接类型：DEFAULT/CUSTOM',
  `jar_path` varchar(500) DEFAULT NULL COMMENT '驱动JAR路径',
  `is_default` tinyint(1) DEFAULT 0 COMMENT '是否为默认驱动',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `port` int(11) DEFAULT NULL COMMENT '驱动监听端口',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`driver_id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='驱动管理表';

-- 插入默认驱动数据
INSERT INTO `tb_iot_driver` (`name`, `type`, `description`, `connection_type`, `is_default`, `status`, `port`) VALUES
('MQTT标准驱动', 'MQTT', 'MQTT协议标准驱动', 'DEFAULT', 1, 1, 1883),
('TCP统一驱动', 'TCP', 'TCP协议统一驱动', 'DEFAULT', 1, 1, 8080),
('国标GB28181驱动', 'TCP', '国标GB/T 28181协议驱动', 'CUSTOM', 0, 1, 5060); 