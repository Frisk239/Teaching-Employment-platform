USE teaching_employment_platform;

-- Create schools
INSERT INTO t_school (school_name, school_code, license_number, license_issue_date, address, province, city, website, contact_person, contact_phone, email, description, status)
VALUES
('Beijing University', 'BJU', '1100000001', '2020-01-01', 'Haidian District, Beijing', 'Beijing', 'Beijing', 'www.bju.edu.cn', 'Zhang Principal', '010-12345678', 'info@bju.edu.cn', 'Top University in China', 1),
('Tsinghua University', 'THU', '1100000002', '2020-01-01', 'Haidian District, Beijing', 'Beijing', 'Beijing', 'www.tsinghua.edu.cn', 'Li Principal', '010-23456789', 'info@tsinghua.edu.cn', 'Top University in China', 1)
ON DUPLICATE KEY UPDATE school_name=VALUES(school_name);

-- Create classrooms
INSERT INTO t_classroom (classroom_no, classroom_name, school_id, building, floor, capacity, classroom_type, has_projector, has_computer, has_multimedia, equipment, status)
VALUES
('A101', 'Room A101', 1, 'Building A', '1', 60, 'Classroom', 1, 0, 1, 'Projector, AC', 1),
('A102', 'Room A102', 1, 'Building A', '1', 80, 'Lecture Hall', 1, 0, 1, 'Projector, AC, Mic', 1),
('B301', 'Room B301', 1, 'Building B', '3', 50, 'Lab', 1, 1, 1, 'Computers, Projector', 1)
ON DUPLICATE KEY UPDATE classroom_no=VALUES(classroom_no);

-- Create teachers (delete courses first due to foreign key)
DELETE FROM t_course WHERE teacher_id IN (SELECT id FROM t_teacher WHERE user_id IN (SELECT id FROM t_user WHERE username LIKE 'teacher%'));
DELETE FROM t_teacher WHERE user_id IN (SELECT id FROM t_user WHERE username LIKE 'teacher%');
DELETE FROM t_user WHERE username LIKE 'teacher%' AND role_id = 3;

INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status)
VALUES
('teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Wang Qiang', '13800002001', 'teacher01@test.com', 3, 1, 1),
('teacher02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Li Na', '13800002002', 'teacher02@test.com', 3, 1, 1),
('teacher03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Zhang Wei', '13800002003', 'teacher03@test.com', 3, 1, 1);

INSERT INTO t_teacher (user_id, teacher_no, school_id, department, title, education, specialty, gender, phone, email, entry_date)
SELECT id, CONCAT('T', LPAD(id, 4, '0')), 1, 'Computer Science', 'Lecturer', 'Master', 'CS', 1, phone, email, '2020-09-01'
FROM t_user WHERE username LIKE 'teacher%' AND role_id = 3;

-- Create students
DELETE FROM t_student WHERE user_id IN (SELECT id FROM t_user WHERE username LIKE 'student%');
DELETE FROM t_user WHERE username LIKE 'student%' AND role_id = 4;

INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status)
VALUES
('student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Zhang San', '13900001001', 'student01@test.com', 4, 1, 1),
('student02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Li Si', '13900001002', 'student02@test.com', 4, 1, 1),
('student03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Wang Wu', '13900001003', 'student03@test.com', 4, 1, 1),
('student04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Zhao Liu', '13900001004', 'student04@test.com', 4, 1, 1),
('student05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Qian Qi', '13900001005', 'student05@test.com', 4, 1, 1),
('student06', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Sun Ba', '13900001006', 'student06@test.com', 4, 1, 1),
('student07', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Zhou Jiu', '13900001007', 'student07@test.com', 4, 1, 1),
('student08', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Wu Shi', '13900001008', 'student08@test.com', 4, 1, 1),
('student09', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Zheng Shiyi', '13900001009', 'student09@test.com', 4, 1, 1),
('student10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Wang Xiaoer', '13900001010', 'student10@test.com', 4, 1, 1);

INSERT INTO t_student (user_id, student_no, school_id, class_name, major, grade, gender, id_card, phone, email, enrollment_date, graduation_date, education, political_status, health_status, seeking_status)
SELECT
    id,
    CONCAT('2021', LPAD(id, 4, '0')),
    1,
    'CS Class 1',
    'Computer Science',
    '2021',
    ((id - 1) % 2) + 1,
    CONCAT('11010120000101', LPAD(id, 4, '0')),
    phone,
    email,
    '2021-09-01',
    '2025-06-30',
    'Bachelor',
    'Member',
    'Good',
    CASE ((id - 1) % 5)
        WHEN 0 THEN 'employed'
        WHEN 1 THEN 'seeking'
        WHEN 2 THEN 'admitted'
        ELSE 'actively_seeking'
    END
FROM t_user
WHERE username LIKE 'student%' AND role_id = 4;

-- Create courses
INSERT INTO t_course (course_name, course_code, course_type, credits, hours, school_id, department, description, status)
VALUES
('Data Structures', 'CS201', 'Required', 4, 64, 1, 'CS Dept', 'Introduction to data structures', 1),
('Computer Networks', 'CS202', 'Required', 3, 48, 1, 'CS Dept', 'Network fundamentals', 1),
('Operating Systems', 'CS203', 'Required', 4, 64, 1, 'CS Dept', 'OS principles', 1),
('Database Systems', 'CS204', 'Required', 3, 48, 1, 'CS Dept', 'Database design', 1),
('Software Engineering', 'CS205', 'Required', 3, 48, 1, 'CS Dept', 'SE methodology', 1),
('Java Programming', 'CS206', 'Elective', 3, 48, 1, 'CS Dept', 'Java development', 1),
('Web Development', 'CS207', 'Elective', 3, 48, 1, 'CS Dept', 'Frontend tech', 1)
ON DUPLICATE KEY UPDATE course_code=VALUES(course_code);

-- Display summary
SELECT 'College head test data initialized successfully!' AS '';
SELECT '===========================================' AS '';
SELECT username AS 'Username', real_name AS 'Name', role_id AS 'Role', '123456' AS 'Password'
FROM t_user WHERE username IN ('college01', 'teacher01', 'student01');
SELECT '===========================================' AS '';
SELECT CONCAT('Schools: ', COUNT(*)) AS 'Summary' FROM t_school;
SELECT CONCAT('Classrooms: ', COUNT(*)) AS 'Summary' FROM t_classroom;
SELECT CONCAT('Teachers: ', COUNT(*)) AS 'Summary' FROM t_teacher;
SELECT CONCAT('Students: ', COUNT(*)) AS 'Summary' FROM t_student;
SELECT CONCAT('Courses: ', COUNT(*)) AS 'Summary' FROM t_course;
