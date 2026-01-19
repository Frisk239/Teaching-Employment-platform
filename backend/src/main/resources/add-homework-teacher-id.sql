-- 为 t_homework 表添加 teacher_id 字段
-- 日期: 2026-01-19

USE teaching_employment_platform;

-- 检查并添加 teacher_id 字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'teaching_employment_platform'
    AND TABLE_NAME = 't_homework'
    AND COLUMN_NAME = 'teacher_id');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE t_homework ADD COLUMN teacher_id BIGINT COMMENT ''教师ID'' AFTER course_id',
    'SELECT ''teacher_id already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 更新现有作业的 teacher_id（假设所有作业都属于教师ID=1）
UPDATE t_homework
SET teacher_id = 1
WHERE teacher_id IS NULL;

-- 查看修改后的表结构
DESCRIBE t_homework;
