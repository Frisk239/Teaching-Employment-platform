-- 为人才库表添加缺失的字段
USE teaching_employment_platform;

-- 添加 position_name 字段（如果不存在）
ALTER TABLE t_talent_pool
ADD COLUMN IF NOT EXISTS position_name VARCHAR(100) COMMENT '意向职位';

-- 查看表结构确认
DESC t_talent_pool;
