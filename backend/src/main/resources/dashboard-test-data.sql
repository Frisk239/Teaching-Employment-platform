USE teaching_employment_platform;

-- ================================================================
-- 学院负责人Dashboard测试数据
-- ================================================================

-- 1. 插入测试日报数据
INSERT INTO t_daily_report (student_id, report_date, content, study_hours, tomorrow_plan, status)
SELECT
    s.id,
    DATE_SUB(CURDATE(), INTERVAL ((s.id - 1) % 10) DAY) AS report_date,
    CONCAT('今日学习了', CASE ((s.id - 1) % 5)
        WHEN 0 THEN '数据结构'
        WHEN 1 THEN '算法'
        WHEN 2 THEN '数据库'
        WHEN 3 THEN '前端开发'
        ELSE 'Java编程'
    END, '相关知识，收获很大') AS content,
    6.0 + ((s.id - 1) % 4) AS study_hours,
    '继续深入学习相关知识' AS tomorrow_plan,
    CASE ((s.id - 1) % 3)
        WHEN 0 THEN 'submitted'
        WHEN 1 THEN 'reviewed'
        ELSE 'submitted'
    END AS status
FROM t_student s
WHERE s.id <= 20
AND NOT EXISTS (
    SELECT 1 FROM t_daily_report dr
    WHERE dr.student_id = s.id
    AND dr.report_date = DATE_SUB(CURDATE(), INTERVAL ((s.id - 1) % 10) DAY)
)
LIMIT 50;

-- 2. 更新部分学员的求职状态
UPDATE t_student SET seeking_status = 'employed'
WHERE id % 5 = 0 AND seeking_status != 'employed';

UPDATE t_student SET seeking_status = 'seeking'
WHERE id % 5 = 1 AND seeking_status NOT IN ('seeking', 'actively_seeking');

UPDATE t_student SET seeking_status = 'actively_seeking'
WHERE id % 5 = 2 AND seeking_status NOT IN ('seeking', 'actively_seeking', 'employed');

-- 3. 创建一些测试Offer（如果表存在且有student_id字段）
-- 注意：这里需要根据实际的t_offer表结构调整
-- INSERT IGNORE INTO t_offer (student_id, position_id, company_id, salary, salary_unit, start_date, status)
-- SELECT
--     s.id,
--     (SELECT id FROM t_recruitment_position LIMIT 1),
--     (SELECT id FROM t_company LIMIT 1),
--     10000 + (s.id % 5) * 2000,
--     'month',
--     '2024-07-01',
--     CASE (s.id % 3)
--         WHEN 0 THEN 'pending'
--         WHEN 1 THEN 'accepted'
--         ELSE 'rejected'
--     END
-- FROM t_student s
-- WHERE s.id <= 10
-- AND s.seeking_status IN ('seeking', 'actively_seeking')
-- LIMIT 10;

-- 4. 显示插入的数据统计
SELECT '========================================' AS '';
SELECT 'Dashboard测试数据初始化完成!' AS '';
SELECT '========================================' AS '';
SELECT '数据统计：' AS '';
SELECT CONCAT('日报记录: ', COUNT(*)) AS '' FROM t_daily_report;
SELECT CONCAT('学员总数: ', COUNT(*)) AS '' FROM t_student;
SELECT CONCAT('教师总数: ', COUNT(*)) AS '' FROM t_teacher;
SELECT CONCAT('课程总数: ', COUNT(*)) AS '' FROM t_course;

SELECT '========================================' AS '';
SELECT '学员求职状态分布：' AS '';
SELECT CONCAT('已就业: ', COUNT(*)) AS '已就业' FROM t_student WHERE seeking_status = 'employed';
SELECT CONCAT('求职中: ', COUNT(*)) AS '求职中' FROM t_student WHERE seeking_status = 'seeking';
SELECT CONCAT('正在求职: ', COUNT(*)) AS '正在求职' FROM t_student WHERE seeking_status = 'actively_seeking';
SELECT CONCAT('已录取: ', COUNT(*)) AS '已录取' FROM t_student WHERE seeking_status = 'admitted';

SELECT '========================================' AS '';
SELECT '最新5条日报：' AS '';
SELECT
    dr.id,
    s.real_name AS student_name,
    dr.report_date,
    LEFT(dr.content, 50) AS content_preview,
    dr.study_hours,
    dr.status
FROM t_daily_report dr
JOIN t_student s ON dr.student_id = s.id
ORDER BY dr.report_date DESC, dr.id DESC
LIMIT 5;
