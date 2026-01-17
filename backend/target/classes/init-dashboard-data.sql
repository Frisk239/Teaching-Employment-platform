USE teaching_employment_platform;

-- Insert test daily report data
INSERT INTO t_daily_report (student_id, report_date, content, study_hours, tomorrow_plan, status)
SELECT
    s.id,
    DATE_SUB(CURDATE(), INTERVAL ((s.id - 1) % 10) DAY),
    CONCAT('Studied ', CASE ((s.id - 1) % 5)
        WHEN 0 THEN 'Data Structures'
        WHEN 1 THEN 'Algorithms'
        WHEN 2 THEN 'Database'
        WHEN 3 THEN 'Web Development'
        ELSE 'Java Programming'
    END) AS content,
    6.0 + ((s.id - 1) % 4) AS study_hours,
    'Continue learning tomorrow' AS tomorrow_plan,
    CASE ((s.id - 1) % 3)
        WHEN 0 THEN 'submitted'
        WHEN 1 THEN 'reviewed'
        ELSE 'submitted'
    END
FROM t_student s
WHERE s.id <= 20
AND NOT EXISTS (
    SELECT 1 FROM t_daily_report dr
    WHERE dr.student_id = s.id
    AND dr.report_date = DATE_SUB(CURDATE(), INTERVAL ((s.id - 1) % 10) DAY)
)
LIMIT 50;

-- Display summary
SELECT '========================================' AS '';
SELECT 'Dashboard test data initialized!' AS '';
SELECT '========================================' AS '';
SELECT CONCAT('Daily reports: ', COUNT(*)) AS '' FROM t_daily_report;
SELECT CONCAT('Total students: ', COUNT(*)) AS '' FROM t_student;
SELECT CONCAT('Total teachers: ', COUNT(*)) AS '' FROM t_teacher;
SELECT CONCAT('Total courses: ', COUNT(*)) AS '' FROM t_course;

SELECT '========================================' AS '';
SELECT 'Recent 5 daily reports:' AS '';
SELECT
    dr.id,
    u.real_name AS student_name,
    dr.report_date,
    LEFT(dr.content, 50) AS content_preview,
    dr.study_hours,
    dr.status
FROM t_daily_report dr
JOIN t_student s ON dr.student_id = s.id
JOIN t_user u ON s.user_id = u.id
ORDER BY dr.report_date DESC, dr.id DESC
LIMIT 5;
