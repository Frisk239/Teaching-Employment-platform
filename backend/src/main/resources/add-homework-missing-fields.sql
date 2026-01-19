-- 为 t_homework 和 t_homework_submission 表添加缺失的字段
-- 日期: 2026-01-19

USE teaching_employment_platform;

-- 1. 为 t_homework 表添加缺失字段
-- 检查并添加 max_score
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'teaching_employment_platform'
    AND TABLE_NAME = 't_homework'
    AND COLUMN_NAME = 'max_score');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE t_homework ADD COLUMN max_score DECIMAL(5,2) DEFAULT 100.00 COMMENT ''满分'' AFTER content',
    'SELECT ''max_score already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 homework_type
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'teaching_employment_platform'
    AND TABLE_NAME = 't_homework'
    AND COLUMN_NAME = 'homework_type');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE t_homework ADD COLUMN homework_type VARCHAR(50) DEFAULT ''assignment'' COMMENT ''作业类型：assignment-作业 project-项目'' AFTER max_score',
    'SELECT ''homework_type already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 status
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'teaching_employment_platform'
    AND TABLE_NAME = 't_homework'
    AND COLUMN_NAME = 'status');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE t_homework ADD COLUMN status VARCHAR(20) DEFAULT ''published'' COMMENT ''状态：draft-草稿 published-已发布 closed-已截止'' AFTER homework_type',
    'SELECT ''status already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 为 t_homework_submission 表添加缺失字段
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'teaching_employment_platform'
    AND TABLE_NAME = 't_homework_submission'
    AND COLUMN_NAME = 'status');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE t_homework_submission ADD COLUMN status VARCHAR(20) DEFAULT ''submitted'' COMMENT ''提交状态：draft-草稿 submitted-已提交 graded-已批改'' AFTER comment',
    'SELECT ''status already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 查看修改后的表结构
SELECT '=== t_homework 表结构 ===' AS info;
DESCRIBE t_homework;

SELECT '=== t_homework_submission 表结构 ===' AS info;
DESCRIBE t_homework_submission;
