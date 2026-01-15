-- 创建面试测试数据
USE teaching_employment_platform;

-- 首先将一些笔试成绩合格的申请状态更新为面试通过
UPDATE t_job_application SET status = 'interview_passed', current_stage = 'interview'
WHERE id IN (
  SELECT id FROM (
    SELECT ja.id FROM t_job_application ja
    INNER JOIN t_written_test wt ON ja.id = wt.application_id
    WHERE wt.status = 'completed' AND wt.score >= 60
    AND ja.status IN ('test_passed', 'interview_passed')
    LIMIT 5
  ) AS temp
);

-- 为面试通过的申请创建面试记录
INSERT INTO t_interview (
    application_id, position_id, student_id, round, interview_type,
    interview_time, location, meeting_link, interviewer, interviewer_contact,
    result, score, comment, status, is_deleted, create_time, update_time
)
SELECT
    id AS application_id,
    position_id,
    student_id,
    1 AS round,  -- 初试
    'online' AS interview_type,
    DATE_ADD(NOW(), INTERVAL 3 DAY) AS interview_time,
    NULL AS location,
    'https://meeting.example.com/room/interview-001' AS meeting_link,
    '张面试官' AS interviewer,
    '13800138001' AS interviewer_contact,
    'pending' AS result,
    NULL AS score,
    NULL AS comment,
    'scheduled' AS status,
    0 AS is_deleted,
    NOW() AS create_time,
    NOW() AS update_time
FROM t_job_application
WHERE status = 'interview_passed'
AND id NOT IN (SELECT application_id FROM t_interview)
LIMIT 5;

-- 更新一些记录为不同状态用于测试

-- 1. 设置一个为已完成(通过)的初试
SET @min_id1 = (SELECT MIN(id) FROM t_interview WHERE status = 'scheduled');
UPDATE t_interview
SET status = 'completed', result = 'passed', score = 85, comment = '技术扎实,沟通能力强'
WHERE id = @min_id1;

-- 2. 设置一个为已完成(未通过)的初试
SET @min_id2 = (SELECT MIN(id) FROM t_interview WHERE status = 'scheduled' AND id > IFNULL(@min_id1, 0));
UPDATE t_interview
SET status = 'completed', result = 'failed', score = 55, comment = '基础知识薄弱'
WHERE id = @min_id2;

-- 3. 设置一个为已取消
SET @min_id3 = (SELECT MIN(id) FROM t_interview WHERE status = 'scheduled' AND id > IFNULL(@min_id2, 0));
UPDATE t_interview
SET status = 'cancelled', result = 'cancelled', comment = '企业取消面试'
WHERE id = @min_id3;

-- 4. 为通过初试的申请创建复试(第二轮)
INSERT INTO t_interview (
    application_id, position_id, student_id, round, interview_type,
    interview_time, location, meeting_link, interviewer, interviewer_contact,
    result, score, comment, status, is_deleted, create_time, update_time
)
SELECT
    application_id,
    position_id,
    student_id,
    2 AS round,  -- 复试
    'offline' AS interview_type,
    DATE_ADD(NOW(), INTERVAL 5 DAY) AS interview_time,
    '科技园A栋301会议室' AS location,
    NULL AS meeting_link,
    '李技术总监' AS interviewer,
    '13900139001' AS interviewer_contact,
    'pending' AS result,
    NULL AS score,
    NULL AS comment,
    'scheduled' AS status,
    0 AS is_deleted,
    NOW() AS create_time,
    NOW() AS update_time
FROM t_interview
WHERE round = 1 AND result = 'passed'
AND NOT EXISTS (
  SELECT 1 FROM t_interview t2
  WHERE t2.application_id = t_interview.application_id
  AND t2.round = 2
);

-- 5. 设置复试为已完成(通过)
UPDATE t_interview
SET status = 'completed', result = 'passed', score = 90, comment = '综合素质优秀,拟录用'
WHERE round = 2 AND status = 'scheduled'
LIMIT 1;

-- 6. 为通过复试的申请创建终试(第三轮)
INSERT INTO t_interview (
    application_id, position_id, student_id, round, interview_type,
    interview_time, location, meeting_link, interviewer, interviewer_contact,
    result, score, comment, status, is_deleted, create_time, update_time
)
SELECT
    application_id,
    position_id,
    student_id,
    3 AS round,  -- 终试
    'phone' AS interview_type,
    DATE_ADD(NOW(), INTERVAL 7 DAY) AS interview_time,
    NULL AS location,
    NULL AS meeting_link,
    '王总经理' AS interviewer,
    '13700137001' AS interviewer_contact,
    'pending' AS result,
    NULL AS score,
    NULL AS comment,
    'scheduled' AS status,
    0 AS is_deleted,
    NOW() AS create_time,
    NOW() AS update_time
FROM t_interview
WHERE round = 2 AND result = 'passed'
AND NOT EXISTS (
  SELECT 1 FROM t_interview t2
  WHERE t2.application_id = t_interview.application_id
  AND t2.round = 3
)
LIMIT 1;

-- 验证插入的数据
SELECT
    i.id,
    i.round AS 轮次,
    i.interview_type AS 面试类型,
    i.interview_time AS 面试时间,
    u.real_name AS 学生姓名,
    p.position_name AS 职位名称,
    c.company_name AS 公司名称,
    i.interviewer AS 面试官,
    i.status AS 状态,
    i.result AS 结果,
    i.score AS 分数,
    i.comment AS 评语
FROM t_interview i
LEFT JOIN t_student s ON i.student_id = s.id
LEFT JOIN t_user u ON s.user_id = u.id
LEFT JOIN t_recruitment_position p ON i.position_id = p.id
LEFT JOIN t_company c ON p.company_id = c.id
ORDER BY i.create_time DESC;
