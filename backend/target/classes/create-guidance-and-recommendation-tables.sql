-- 创建职位推荐表和指导记录表

-- 1. 职位推荐表
CREATE TABLE IF NOT EXISTS `t_position_recommendation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `position_id` BIGINT NOT NULL COMMENT '职位ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `reason` TEXT COMMENT '推荐理由',
  `remark` TEXT COMMENT '备注信息',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待查看，viewed-已查看，applied-已投递，rejected-已拒绝',
  `view_time` DATETIME DEFAULT NULL COMMENT '学员查看时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_position_id` (`position_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位推荐表';

-- 2. 指导记录表
CREATE TABLE IF NOT EXISTS `t_guidance_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `guidance_type` VARCHAR(50) NOT NULL COMMENT '指导类型：career_planning-职业规划，resume_guidance-简历指导，interview_guidance-面试指导，skill_improvement-技能提升，psychological_counseling-心理辅导，other-其他',
  `content` TEXT NOT NULL COMMENT '指导内容',
  `next_action` TEXT COMMENT '后续跟进计划',
  `guidance_date` DATETIME NOT NULL COMMENT '指导时间',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '指导地点（线上/线下）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_guidance_type` (`guidance_type`),
  KEY `idx_guidance_date` (`guidance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指导记录表';
