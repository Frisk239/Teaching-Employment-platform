-- 创建笔试测试数据
USE teaching_employment_platform;

-- 先检查现有的screened申请并为其创建笔试
-- 申请ID 66 是 screened 状态

INSERT INTO t_written_test (
    application_id, position_id, student_id, test_url,
    duration, start_time, end_time, score, total_score,
    status, comment, is_deleted, create_time, update_time
) VALUES
-- 为申请ID 66(已筛选状态)创建待考试记录
(66, 14, 1, 'https://exam.example.com/test/frontend-001',
 90, '2026-01-20 14:00:00', '2026-01-20 15:30:00',
 NULL, 100, 'pending', NULL, 0, NOW(), NOW());

-- 为了测试,我们创建一些不需要外键约束的记录(先创建对应的申请)
-- 首先将一些申请状态改为screened
UPDATE t_job_application SET status = 'screened', current_stage = 'test'
WHERE id IN (SELECT id FROM (
  SELECT id FROM t_job_application
  WHERE status = 'pending'
  LIMIT 5
) AS temp);

-- 然后为这些申请创建笔试
INSERT INTO t_written_test (
    application_id, position_id, student_id, test_url,
    duration, start_time, end_time, score, total_score,
    status, comment, is_deleted, create_time, update_time
)
SELECT
    id,
    position_id,
    student_id,
    CONCAT('https://exam.example.com/test/', id) AS test_url,
    90 AS duration,
    DATE_ADD(NOW(), INTERVAL 2 DAY) AS start_time,
    DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 90 MINUTE AS end_time,
    NULL AS score,
    100 AS total_score,
    'pending' AS status,
    NULL AS comment,
    0 AS is_deleted,
    NOW() AS create_time,
    NOW() AS update_time
FROM t_job_application
WHERE status = 'screened'
AND id NOT IN (SELECT application_id FROM t_written_test)
LIMIT 4;

-- 更新一些记录为不同状态用于测试
UPDATE t_written_test SET status = 'ongoing' WHERE id = (SELECT MIN(id) FROM t_written_test WHERE status = 'pending');
UPDATE t_written_test SET status = 'completed', score = 85, comment = '表现良好' WHERE id = (SELECT MIN(id) FROM (SELECT id FROM t_written_test WHERE status = 'pending' AND id > (SELECT MIN(id) FROM t_written_test)) AS temp);
UPDATE t_written_test SET status = 'completed', score = 55, comment = '基础薄弱' WHERE id = (SELECT MIN(id) FROM (SELECT id FROM t_written_test WHERE status = 'pending' AND id > (SELECT MIN(id) FROM (SELECT id FROM t_written_test WHERE status = 'pending' AND id > (SELECT MIN(id) FROM t_written_test)) AS temp2)) AS temp3);
UPDATE t_written_test SET status = 'missed' WHERE id = (SELECT MIN(id) FROM (SELECT id FROM t_written_test WHERE status = 'pending') AS temp4);

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
