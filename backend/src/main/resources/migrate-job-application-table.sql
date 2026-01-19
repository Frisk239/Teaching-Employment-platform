-- 求职申请表结构完善脚本
-- 添加缺失的字段以匹配实体类
-- 执行时间: 2026-01-19

USE teaching_employment_platform;

-- 查看当前表结构
DESC t_job_application;

-- 添加缺失的字段
ALTER TABLE t_job_application
  ADD COLUMN resume_id BIGINT COMMENT '简历ID' AFTER student_id,
  ADD COLUMN current_stage VARCHAR(50) COMMENT '当前阶段：resume-简历筛选 test-笔试 interview-面试 offer-offer发放 hired-入职' AFTER status,
  ADD COLUMN hr_remark VARCHAR(500) COMMENT 'HR备注' AFTER apply_time;

-- 重命名字段 apply_time 为 application_time
ALTER TABLE t_job_application CHANGE COLUMN apply_time application_time DATETIME COMMENT '申请时间';

-- 查看修改后的表结构
DESC t_job_application;

-- 验证字段添加成功
SELECT
  id,
  position_id,
  student_id,
  resume_id,
  status,
  current_stage,
  application_time,
  hr_remark
FROM t_job_application
LIMIT 5;
