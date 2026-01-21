-- 添加passed字段到t_exam_record表
-- 执行日期：2026-01-20
-- 说明：修复开始考试时的数据库错误

ALTER TABLE t_exam_record
ADD COLUMN passed TINYINT(1) DEFAULT 0 COMMENT '及格状态：0-不及格 1-及格'
AFTER total_score;

-- 验证字段是否添加成功
DESCRIBE t_exam_record;
