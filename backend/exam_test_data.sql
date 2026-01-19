-- 创建笔试测试数据

-- 1. 插入考试信息
INSERT INTO t_exam (course_id, exam_type, exam_name, description, duration, start_time, end_time, total_score, passing_score, status, question_count) VALUES
(1, 'written', 'Java程序设计笔试', '测试Java基础知识和编程能力', 90, '2026-01-20 14:00:00', '2026-01-20 15:30:00', 100.00, 60, 'published', 10),
(1, 'written', 'Web前端开发笔试', 'HTML、CSS、JavaScript基础知识测试', 60, '2026-01-25 10:00:00', '2026-01-25 11:00:00', 100.00, 60, 'published', 8),
(2, 'written', '数据库原理笔试', '数据库设计、SQL语句、事务处理等', 90, '2026-01-22 09:00:00', '2026-01-22 10:30:00', 100.00, 60, 'published', 12),
(3, 'written', '数据结构与算法笔试', '线性表、树、图、排序算法等', 120, '2026-01-28 14:00:00', '2026-01-28 16:00:00', 100.00, 60, 'published', 15);

-- 2. 为学员张三（student_id=1）插入考试记录
-- 已完成考试 - Java程序设计（85分）
INSERT INTO t_student_exam_record (exam_id, student_id, start_time, end_time, score, status) VALUES
(1, 1, '2026-01-20 14:05:00', '2026-01-20 15:25:00', 85.00, 'graded');

-- 考试中 - Web前端开发（正在考试）
INSERT INTO t_student_exam_record (exam_id, student_id, start_time, status) VALUES
(2, 1, NOW(), 'in_progress');

-- 待考试 - 数据库原理
INSERT INTO t_student_exam_record (exam_id, student_id, status) VALUES
(3, 1, 'not_started');

-- 待考试 - 数据结构与算法
INSERT INTO t_student_exam_record (exam_id, student_id, status) VALUES
(4, 1, 'not_started');

-- 查看插入的数据
SELECT
    e.id,
    e.exam_name,
    e.exam_type,
    e.duration,
    e.start_time,
    e.total_score,
    e.passing_score,
    ser.status AS record_status,
    ser.start_time AS student_start_time,
    ser.score
FROM t_exam e
LEFT JOIN t_student_exam_record ser ON e.id = ser.exam_id AND ser.student_id = 1
ORDER BY e.start_time;
