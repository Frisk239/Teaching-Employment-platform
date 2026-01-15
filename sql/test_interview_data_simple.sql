-- 创建面试测试数据(简化版)
USE teaching_employment_platform;

-- 检查是否有符合条件的申请
SELECT COUNT(*) INTO @app_count FROM t_job_application WHERE status IN ('test_passed', 'screened', 'interview_passed');

-- 如果没有符合条件的申请,先创建一些
SET @app_count = IFNULL(@app_count, 0);

-- 为笔试通过或已筛选的申请创建面试记录
INSERT INTO t_interview (
    application_id, position_id, student_id, round, interview_type,
    interview_time, location, meeting_link, interviewer, interviewer_contact,
    result, score, comment, status, is_deleted, create_time, update_time
)
SELECT
    ja.id AS application_id,
    ja.position_id,
    ja.student_id,
    1 AS round,
    'online' AS interview_type,
    DATE_ADD(NOW(), INTERVAL 3 DAY) AS interview_time,
    NULL AS location,
    CONCAT('https://meeting.example.com/room/', ja.id) AS meeting_link,
    '面试官' AS interviewer,
    '13800138000' AS interviewer_contact,
    'pending' AS result,
    NULL AS score,
    NULL AS comment,
    'scheduled' AS status,
    0 AS is_deleted,
    NOW() AS create_time,
    NOW() AS update_time
FROM t_job_application ja
WHERE ja.status IN ('test_passed', 'screened', 'interview_passed')
AND NOT EXISTS (
    SELECT 1 FROM t_interview i WHERE i.application_id = ja.id
)
LIMIT 5;

-- 如果没有数据,手动插入一些测试数据
SET @interview_count = (SELECT COUNT(*) FROM t_interview);

IF @interview_count = 0 THEN
    -- 获取一个有效的申请ID
    SET @app_id = (SELECT id FROM t_job_application LIMIT 1);

    IF @app_id IS NOT NULL THEN
        -- 获取该申请的position_id和student_id
        SELECT position_id, student_id INTO @pos_id, @stu_id
        FROM t_job_application WHERE id = @app_id;

        -- 插入测试面试记录
        INSERT INTO t_interview (
            application_id, position_id, student_id, round, interview_type,
            interview_time, location, meeting_link, interviewer, interviewer_contact,
            result, score, comment, status, is_deleted, create_time, update_time
        ) VALUES
        -- 已安排的面试
        (@app_id, @pos_id, @stu_id, 1, 'online',
         DATE_ADD(NOW(), INTERVAL 2 DAY), NULL,
         'https://meeting.example.com/room/test1',
         '张面试官', '13800138001',
         'pending', NULL, NULL, 'scheduled', 0, NOW(), NOW()),
        -- 已完成通过的面试
        (@app_id, @pos_id, @stu_id, 1, 'offline',
         DATE_ADD(NOW(), INTERVAL -1 DAY), '会议室A',
         NULL, '李技术总监', '13900139002',
         'passed', 85, '表现良好', 'completed', 0, NOW(), NOW()),
        -- 已完成未通过的面试
        (@app_id, @pos_id, @stu_id, 1, 'phone',
         DATE_ADD(NOW(), INTERVAL -2 DAY), NULL,
         NULL, '王面试官', '13700137003',
         'failed', 55, '基础薄弱', 'completed', 0, NOW(), NOW()),
        -- 已取消的面试
        (@app_id, @pos_id, @stu_id, 1, 'online',
         DATE_ADD(NOW(), INTERVAL -3 DAY), NULL,
         'https://meeting.example.com/room/test2',
         '赵面试官', '13600136004',
         'cancelled', NULL, '企业取消', 'cancelled', 0, NOW(), NOW());
    END IF;
END IF;

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
