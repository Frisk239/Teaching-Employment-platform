-- 修复 t_written_test 表结构
-- 1. 删除不需要的字段
ALTER TABLE t_written_test DROP FOREIGN KEY t_written_test_ibfk_1;
ALTER TABLE t_written_test DROP COLUMN course_id;
ALTER TABLE t_written_test DROP COLUMN title;
ALTER TABLE t_written_test DROP COLUMN description;

-- 2. 添加缺失字段
ALTER TABLE t_written_test ADD COLUMN application_id BIGINT AFTER id;
ALTER TABLE t_written_test ADD COLUMN student_id BIGINT AFTER position_id;
ALTER TABLE t_written_test ADD COLUMN score INT AFTER end_time;
ALTER TABLE t_written_test ADD COLUMN total_score INT AFTER score;
ALTER TABLE t_written_test ADD COLUMN status VARCHAR(50) AFTER total_score;
ALTER TABLE t_written_test ADD COLUMN comment TEXT AFTER status;

-- 3. 添加索引
ALTER TABLE t_written_test ADD INDEX idx_application_id (application_id);
ALTER TABLE t_written_test ADD INDEX idx_student_id (student_id);
