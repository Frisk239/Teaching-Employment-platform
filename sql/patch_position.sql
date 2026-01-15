-- =============================================
-- 修复 t_recruitment_position 表
-- 日期: 2026-01-12
-- 说明: 添加缺失的salary_unit字段
-- =============================================

USE teaching_employment_platform;

-- 添加 salary_unit 字段
ALTER TABLE t_recruitment_position
ADD COLUMN salary_unit VARCHAR(20) COMMENT '薪资单位：month-按月 year-按年 day-按天 hour-按时' AFTER salary_max;

-- 验证修改
SELECT 'Position表结构验证:' AS info;
DESC t_recruitment_position;

SELECT '✅ Position表结构修复完成!' AS message;
