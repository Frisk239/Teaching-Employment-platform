-- =============================================
-- 数据库表结构修复补丁
-- 日期: 2026-01-12
-- 说明: 修复已存在数据库的表结构问题
-- =============================================

USE teaching_employment_platform;

-- =============================================
-- 修复 t_job_application 表
-- 说明: 添加缺失的字段以匹配JobApplication实体类
-- =============================================

-- 1. 修改 status 字段类型 (VARCHAR(20) -> VARCHAR(50))
ALTER TABLE t_job_application
MODIFY COLUMN status VARCHAR(50) DEFAULT 'pending'
COMMENT '申请状态：pending-待处理 screened-已筛选 test_passed-笔试通过 interview_passed-面试通过 test_failed-笔试失败 interview_failed-面试失败 rejected-已拒绝 offered-已发offer hired-已入职 declined-已拒绝offer';

-- 2. 添加 current_stage 字段
ALTER TABLE t_job_application
ADD COLUMN current_stage VARCHAR(50) COMMENT '当前阶段：resume-简历筛选 test-笔试 interview-面试 offer-offer发放 hired-入职' AFTER status;

-- 3. 添加 application_time 字段
ALTER TABLE t_job_application
ADD COLUMN application_time DATETIME COMMENT '申请时间' AFTER current_stage;

-- 4. 添加 hr_remark 字段
ALTER TABLE t_job_application
ADD COLUMN hr_remark VARCHAR(500) COMMENT 'HR备注' AFTER application_time;

-- =============================================
-- 修复 t_recruitment_position 表
-- 说明: 添加缺失的salary_unit字段
-- =============================================

-- 添加 salary_unit 字段
ALTER TABLE t_recruitment_position
ADD COLUMN salary_unit VARCHAR(20) COMMENT '薪资单位：month-按月 year-按年 day-按天 hour-按时' AFTER salary_max;

-- =============================================
-- 验证修改
-- =============================================
SELECT 'JobApplication表结构验证:' AS info;
DESC t_job_application;

SELECT '' AS info;
SELECT 'Position表结构验证:' AS info;
DESC t_recruitment_position;

SELECT '✅ 数据库表结构修复完成!' AS message;
