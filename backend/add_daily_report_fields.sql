-- Add missing fields to t_daily_report table
ALTER TABLE t_daily_report
ADD COLUMN code_lines INT AFTER study_hours,
ADD COLUMN submit_time DATETIME AFTER status,
ADD COLUMN review_time DATETIME AFTER submit_time;
