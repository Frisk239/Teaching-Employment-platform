-- 更新笔试表结构,添加缺失字段
USE teaching_employment_platform;

-- 添加缺失的字段
ALTER TABLE t_written_test
ADD COLUMN IF NOT EXISTS position_id BIGINT COMMENT '职位ID' AFTER application_id,
ADD COLUMN IF NOT EXISTS student_id BIGINT COMMENT '学生ID' AFTER position_id,
ADD COLUMN IF NOT EXISTS test_url VARCHAR(500) COMMENT '考试链接或试卷URL' AFTER student_id,
ADD COLUMN IF NOT EXISTS start_time DATETIME COMMENT '开始时间' AFTER test_url,
ADD COLUMN IF NOT EXISTS end_time DATETIME COMMENT '结束时间' AFTER start_time,
ADD COLUMN IF NOT EXISTS score INT COMMENT '得分' AFTER end_time,
ADD COLUMN IF NOT EXISTS comment TEXT COMMENT '评语' AFTER score;

-- 更新 status 枚举,添加 missed 状态
ALTER TABLE t_written_test
MODIFY COLUMN status VARCHAR(20) DEFAULT 'pending' COMMENT '状态:pending-待考试 ongoing-考试中 completed-已完成 missed-缺席';

-- 添加索引
ALTER TABLE t_written_test
ADD INDEX IF NOT EXISTS idx_position_id (position_id),
ADD INDEX IF NOT EXISTS idx_student_id (student_id),
ADD INDEX IF NOT EXISTS idx_status (status);

-- 验证表结构
DESCRIBE t_written_test;
