-- Add missing fields to t_course table
ALTER TABLE t_course
ADD COLUMN school_id BIGINT AFTER course_type,
ADD COLUMN classroom_id BIGINT AFTER teacher_id,
ADD COLUMN start_date DATE AFTER hours,
ADD COLUMN end_date DATE AFTER start_date,
ADD COLUMN max_students INT DEFAULT 50 AFTER end_date,
ADD COLUMN current_students INT DEFAULT 0 AFTER max_students;
