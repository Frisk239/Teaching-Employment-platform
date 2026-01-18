-- 创建消息通知表
CREATE TABLE IF NOT EXISTS `t_notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '接收用户ID（NULL表示所有用户）',
  `type` VARCHAR(50) NOT NULL DEFAULT 'system' COMMENT '通知类型：system-系统 notice-公告 homework-作业 report-日报 job-职位 application-申请 test-笔试 interview-面试 offer-offer',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `related_id` BIGINT DEFAULT NULL COMMENT '关联对象ID（如作业ID、职位ID等）',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';
