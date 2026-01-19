-- 修复 t_exam 表结构
-- 1. 重命名字段以匹配实体类
ALTER TABLE t_exam CHANGE COLUMN title exam_name VARCHAR(200);
ALTER TABLE t_exam CHANGE COLUMN full_score total_score DECIMAL(5,2);
ALTER TABLE t_exam CHANGE COLUMN passing_score passing_score INT;

-- 2. 添加缺失字段
ALTER TABLE t_exam ADD COLUMN exam_type VARCHAR(50) AFTER course_id;
ALTER TABLE t_exam ADD COLUMN is_random INT DEFAULT 0 AFTER status;
ALTER TABLE t_exam ADD COLUMN question_count INT AFTER is_random;
