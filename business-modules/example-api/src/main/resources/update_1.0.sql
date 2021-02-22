-- 2021-01-11 16:33:01 by JerryMa
CREATE TABLE `demo`( `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',`is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记',`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',PRIMARY KEY (`id`))AUTO_INCREMENT=10000002 DEFAULT CHARSET=utf8mb4 COMMENT '样例';
-- 2021-01-11 18:57:25 by JerryMa
ALTER TABLE `demo` ADD COLUMN `gender` varchar(100) COMMENT '性别' AFTER `id`;
-- 2021-01-11 18:57:25 by JerryMa
ALTER TABLE `demo` MODIFY COLUMN `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记' AFTER `gender`;
-- 2021-01-12 13:30:33 by JerryMa
ALTER TABLE `demo` ADD COLUMN `title` varchar(100) NOT NULL COMMENT '标题' AFTER `id`;
-- 2021-01-12 13:30:33 by JerryMa
ALTER TABLE `demo` MODIFY COLUMN `gender` varchar(100) COMMENT '性别' AFTER `title`;
-- 2021-01-12 14:51:15 by JerryMa
ALTER TABLE `demo` ADD COLUMN `complete` tinyint(1) COMMENT '完成' AFTER `title`;
-- 2021-01-12 14:51:16 by JerryMa
ALTER TABLE `demo` MODIFY COLUMN `gender` varchar(100) COMMENT '性别' AFTER `complete`;
