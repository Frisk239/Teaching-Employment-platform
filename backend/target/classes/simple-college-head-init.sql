USE teaching_employment_platform;

-- Create college head account
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status)
VALUES ('college01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'Li Dean', '13800001001', 'college01@test.com', 2, 1, 1)
ON DUPLICATE KEY UPDATE real_name='Li Dean', phone='13800001001';

-- Create schools
INSERT IGNORE INTO t_school (school_name, school_code, license_number, license_issue_date, address, province, city, website, contact_person, contact_phone, email, description, status)
VALUES
('Beijing University', 'BJU', '1100000001', '2020-01-01', 'Haidian District', 'Beijing', 'Beijing', 'www.bju.edu', 'Zhang', '010-12345678', 'info@bju.edu', 'Top university', 1),
('Tsinghua University', 'THU', '1100000002', '2020-01-01', 'Haidian District', 'Beijing', 'Beijing', 'www.tsinghua.edu', 'Li', '010-23456789', 'info@thu.edu', 'Top university', 1);

-- Create classrooms
INSERT IGNORE INTO t_classroom (classroom_no, classroom_name, school_id, building, floor, capacity, classroom_type, has_projector, has_computer, has_multimedia, equipment, status)
VALUES
('A101', 'Room A101', 1, 'Building A', '1', 60, 'Classroom', 1, 0, 1, 'Projector', 1),
('A102', 'Room A102', 1, 'Building A', '1', 80, 'Lecture', 1, 0, 1, 'Projector, Mic', 1),
('B301', 'Room B301', 1, 'Building B', '3', 50, 'Lab', 1, 1, 1, 'Computers', 1);

-- Create courses
INSERT IGNORE INTO t_course (course_name, course_code, course_type, credits, hours, school_id, department, description, status)
VALUES
('Data Structures', 'CS201', 'Required', 4, 64, 1, 'CS', 'DS course', 1),
('Networks', 'CS202', 'Required', 3, 48, 1, 'CS', 'Network course', 1),
('OS', 'CS203', 'Required', 4, 64, 1, 'CS', 'OS course', 1),
('Database', 'CS204', 'Required', 3, 48, 1, 'CS', 'DB course', 1),
('Java', 'CS205', 'Elective', 3, 48, 1, 'CS', 'Java course', 1);

-- Display summary
SELECT '============================================' AS '';
SELECT 'College Head Test Data Init Complete!' AS '';
SELECT '============================================' AS '';
SELECT 'Account Info:' AS '';
SELECT CONCAT('Username: college01') AS '';
SELECT CONCAT('Password: 123456') AS '';
SELECT CONCAT('Role: College Head (Li Dean)') AS '';
SELECT '============================================' AS '';
SELECT 'Data Summary:' AS '';
SELECT CONCAT('Schools: ', COUNT(*)) AS '' FROM t_school;
SELECT CONCAT('Classrooms: ', COUNT(*)) AS '' FROM t_classroom;
SELECT CONCAT('Courses: ', COUNT(*)) AS '' FROM t_course;
SELECT CONCAT('Teachers: ', COUNT(*)) AS '' FROM t_teacher;
SELECT CONCAT('Students: ', COUNT(*)) AS '' FROM t_student;
