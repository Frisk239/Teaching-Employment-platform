-- Fix database schema issues discovered during testing
-- Date: 2026-01-18

SET NAMES utf8mb4;

-- 1. Add is_deleted field to t_job_application
ALTER TABLE t_job_application
ADD COLUMN is_deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '0:未删除 1:已删除' AFTER update_time;

-- 2. Add is_deleted field to t_offer
ALTER TABLE t_offer
ADD COLUMN is_deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '0:未删除 1:已删除' AFTER update_time;

-- 3. Add is_deleted field to t_interview
ALTER TABLE t_interview
ADD COLUMN is_deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '0:未删除 1:已删除' AFTER update_time;

-- 4. Add round field to t_interview
ALTER TABLE t_interview
ADD COLUMN round INT DEFAULT 1 COMMENT '面试轮次' AFTER student_id;

-- 5. Add interviewer_name field to t_interview (for compatibility)
ALTER TABLE t_interview
ADD COLUMN interviewer_name VARCHAR(50) COMMENT '面试官姓名' AFTER interviewer;

-- 6. Add interviewer_contact field to t_interview
ALTER TABLE t_interview
ADD COLUMN interviewer_contact VARCHAR(50) COMMENT '面试官联系方式' AFTER interviewer_name;

-- Verify changes
SELECT 't_job_application columns' as table_info;
DESCRIBE t_job_application;

SELECT 't_offer columns' as table_info;
DESCRIBE t_offer;

SELECT 't_interview columns' as table_info;
DESCRIBE t_interview;
