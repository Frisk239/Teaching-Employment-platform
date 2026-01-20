-- 为人才库表添加 is_deleted 字段
USE teaching_employment_platform;

-- 检查并添加 is_deleted 字段
ALTER TABLE t_talent_pool
ADD COLUMN IF NOT EXISTS is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除';

-- 查看表结构确认
DESC t_talent_pool;
