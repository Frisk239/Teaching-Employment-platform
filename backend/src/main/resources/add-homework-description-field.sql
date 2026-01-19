-- 为 t_homework 表添加 description 字段
-- 日期: 2026-01-19

USE teaching_employment_platform;

-- 检查并添加 description 字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'teaching_employment_platform'
    AND TABLE_NAME = 't_homework'
    AND COLUMN_NAME = 'description');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE t_homework ADD COLUMN description TEXT COMMENT ''作业描述'' AFTER title',
    'SELECT ''description already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 将现有 content 字段的数据复制到 description 字段
UPDATE t_homework
SET description = content
WHERE description IS NULL AND content IS NOT NULL;

-- 查看修改后的表结构
DESCRIBE t_homework;
