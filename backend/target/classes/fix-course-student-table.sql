-- 修复 t_course_student 表结构
-- 日期: 2026-01-19
-- 说明:
-- 1. 添加 progress 字段（学习进度）
-- 2. 修改 status 字段类型从 VARCHAR 改为 INT，与实体类保持一致（1-在读 0-已退课）
-- 3. 确保使用物理删除而不是逻辑删除（无 is_deleted 字段）

USE teaching_employment_platform;

-- 1. 检查并添加 progress 字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'teaching_employment_platform'
      AND TABLE_NAME = 't_course_student'
      AND COLUMN_NAME = 'progress'
);

-- 如果 progress 字段不存在，则添加
SET @sql = IF(@column_exists = 0,
    'ALTER TABLE t_course_student ADD COLUMN progress DECIMAL(5,2) DEFAULT 0.00 COMMENT ''学习进度(%)''',
    'SELECT ''progress field already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 修改 status 字段类型
-- 先将现有的 VARCHAR 状态转换为 INT
UPDATE t_course_student
SET status = CASE
    WHEN status = 'active' THEN '1'
    WHEN status = 'inactive' THEN '0'
    ELSE '1'
END
WHERE status IN ('active', 'inactive');

-- 修改字段类型
ALTER TABLE t_course_student
MODIFY COLUMN status INT DEFAULT 1 COMMENT '状态：1-在读 0-已退课';

-- 3. 确认表结构（查看当前结构）
SELECT
    COLUMN_NAME,
    DATA_TYPE,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'teaching_employment_platform'
  AND TABLE_NAME = 't_course_student'
ORDER BY ORDINAL_POSITION;
