-- 初始化学员测试账号
-- 账号: student01
-- 密码: 123456

-- 1. 先清理已存在的测试用户
DELETE FROM student WHERE user_id IN (SELECT id FROM user WHERE username = 'student01');
DELETE FROM user WHERE username = 'student01';

-- 2. 插入用户记录 (角色ID=4 表示学员)
INSERT INTO user (username, password, real_name, email, phone, status, role_id, create_time)
VALUES ('student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试学员', 'student01@test.com', '13800000001', '1', 4, NOW());

-- 3. 插入学员记录
INSERT INTO student (user_id, student_no, enrollment_date, graduation_date, major, class_name, education_level, status, create_time)
SELECT id, CONCAT('S', DATE_FORMAT(NOW(), '%Y%m%d'), '001'), NOW(), DATE_ADD(NOW(), INTERVAL 6 MONTH), 'Java开发', '202401班', '本科', '在读', NOW()
FROM user WHERE username = 'student01';

-- 4. 验证创建结果
SELECT '创建成功!' AS message;
SELECT u.username, u.real_name, u.email, s.student_no, s.major, s.class_name
FROM user u
LEFT JOIN student s ON u.id = s.user_id
WHERE u.username = 'student01';
