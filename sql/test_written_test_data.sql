-- 创建笔试测试数据
USE teaching_employment_platform;

-- 为已筛选(screened)状态的申请创建笔试记录
INSERT INTO t_written_test (
    application_id, position_id, student_id, test_url,
    duration, start_time, end_time, score, total_score,
    status, comment, is_deleted, create_time, update_time
) VALUES
-- 为申请ID 35(已筛选状态)创建待考试记录
(35, 13, 1, 'https://exam.example.com/test/java-basic-001',
 90, '2026-01-20 14:00:00', '2026-01-20 15:30:00',
 NULL, 100, 'pending', NULL, 0, NOW(), NOW()),

-- 为申请ID 36(已筛选状态)创建考试中记录
(36, 14, 5, 'https://exam.example.com/test/frontend-002',
 60, '2026-01-18 10:00:00', '2026-01-18 11:00:00',
 NULL, 100, 'ongoing', NULL, 0, NOW(), NOW()),

-- 为申请ID 37(已筛选状态)创建已完成记录(通过)
(37, 15, 3, 'https://exam.example.com/test/algorithm-003',
 120, '2026-01-15 09:00:00', '2026-01-15 11:00:00',
 85, 100, 'completed', '算法基础扎实,逻辑思维清晰', 0, NOW(), NOW()),

-- 为申请ID 38(已筛选状态)创建已完成记录(未通过)
(38, 13, 4, 'https://exam.example.com/test/java-basic-004',
 90, '2026-01-16 14:00:00', '2026-01-16 15:30:00',
 45, 100, 'completed', '基础知识薄弱,需要加强学习', 0, NOW(), NOW()),

-- 为申请ID 39(已筛选状态)创建缺席记录
(39, 14, 6, 'https://exam.example.com/test/frontend-005',
 60, '2026-01-17 10:00:00', '2026-01-17 11:00:00',
 NULL, 100, 'missed', '未按时参加考试', 0, NOW(), NOW());

-- 验证插入的数据
SELECT
    wt.id,
    u.real_name AS studentName,
    p.position_name AS positionName,
    wt.start_time,
    wt.duration,
    wt.score,
    wt.total_score,
    wt.status,
    wt.comment
FROM t_written_test wt
LEFT JOIN t_student s ON wt.student_id = s.id
LEFT JOIN t_user u ON s.user_id = u.id
LEFT JOIN t_recruitment_position p ON wt.position_id = p.id
ORDER BY wt.create_time DESC;
