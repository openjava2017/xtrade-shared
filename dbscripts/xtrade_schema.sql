-- ----------------------------
-- 系统ID生成器数据模型
-- ----------------------------
DROP TABLE IF EXISTS `xtrade_sequence_key`;
CREATE TABLE `xtrade_sequence_key` (
  `id` BIGINT NOT NULL,
  `key` VARCHAR(50) NOT NULL COMMENT 'KEY标识',
  `start_with` BIGINT DEFAULT '1' COMMENT '起始值',
  `inc_span` BIGINT DEFAULT '1' COMMENT '跨度',
  `scope` VARCHAR(50) COMMENT '应用范围',
  `version` BIGINT NOT NULL DEFAULT '0' COMMENT '数据版本',
  `expired_date` DATE COMMENT '有效日期',
  `description` VARCHAR(100) COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sequence_key_key` (`key`, `scope`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;