-- 创建教学计划表
CREATE TABLE IF NOT EXISTS `t_teaching_plan` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教学计划ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID（关联t_course）',
  `lesson_number` INT NOT NULL COMMENT '课次序号（第几次课，从1开始）',
  `week_number` INT NOT NULL COMMENT '周次（对应timetable的week_number）',
  `title` VARCHAR(200) NOT NULL COMMENT '本次课标题',
  `content` TEXT COMMENT '教学内容描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_lesson` (`course_id`, `lesson_number`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_week_number` (`week_number`),
  CONSTRAINT `fk_teaching_plan_course` FOREIGN KEY (`course_id`) REFERENCES `t_course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教学计划表';
