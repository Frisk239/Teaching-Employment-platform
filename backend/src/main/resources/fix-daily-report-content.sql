USE teaching_employment_platform;

-- 将content字段的内容迁移到today_content字段
UPDATE t_daily_report
SET today_content = content
WHERE today_content IS NULL AND content IS NOT NULL;

-- 显示更新后的数据
SELECT '========================================' AS '';
SELECT 'Daily report content migration completed!' AS '';
SELECT '========================================' AS '';

SELECT 'Updated records:' AS '';
SELECT
    dr.id,
    dr.student_id,
    dr.report_date,
    LEFT(dr.today_content, 50) AS content_preview,
    dr.study_hours,
    dr.status
FROM t_daily_report dr
WHERE dr.today_content IS NOT NULL
ORDER BY dr.report_date DESC, dr.id DESC
LIMIT 10;
