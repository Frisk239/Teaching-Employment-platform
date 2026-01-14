-- ================================================================
-- 高校教学就业平台 - 测试账号创建脚本
-- ================================================================

USE teaching_employment_platform;

-- ================================================================
-- 1. 创建管理员账号
-- ================================================================
-- 已存在: admin/123456 (user_id=1, role_id=1)

-- ================================================================
-- 2. 创建学院负责人账号
-- ================================================================
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status)
VALUES ('college01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李院长', '13800001001', 'college01@test.com', 2, 1, 1)
ON DUPLICATE KEY UPDATE real_name='李院长';

-- 关联学院负责人到教师表（可选）
-- INSERT INTO t_teacher (user_id, teacher_no, school_id, department, title)
-- SELECT id, CONCAT('T', LPAD(id, 4, '0')), 1, '计算机学院', '院长'
-- FROM t_user WHERE username='college01' AND role_id=2;

-- ================================================================
-- 3. 创建教师账号
-- ================================================================
-- 已存在: teacher01/123456 (user_id=2, teacher_id=1, real_name='Teacher 1')

-- 创建更多教师
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status)
VALUES
('teacher02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王老师', '13800001002', 'teacher02@test.com', 3, 1, 1),
('teacher03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', '13800001003', 'teacher03@test.com', 3, 1, 1)
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name);

-- 为新教师创建教师记录
INSERT INTO t_teacher (user_id, teacher_no, school_id, department, title, education, specialty)
SELECT id, CONCAT('T', LPAD(id, 4, '0')), 1, '计算机学院', '讲师', '博士', '软件工程'
FROM t_user WHERE username IN ('teacher02', 'teacher03') AND role_id=3
AND NOT EXISTS (SELECT 1 FROM t_teacher WHERE user_id IN (SELECT id FROM t_user WHERE username IN ('teacher02', 'teacher03')));

-- ================================================================
-- 4. 创建学生账号
-- ================================================================
-- 已存在: student01/123456 (user_id=3, student_id=1)

-- 创建更多学生
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status)
VALUES
('student02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李明', '13800002001', 'student02@test.com', 4, 1, 1),
('student03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王芳', '13800002002', 'student03@test.com', 4, 1, 1),
('student04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵强', '13800002003', 'student04@test.com', 4, 1, 1),
('student05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈静', '13800002004', 'student05@test.com', 4, 1, 1)
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name);

-- 为新学生创建学生记录
INSERT INTO t_student (user_id, student_no, school_id, class_name, major, grade, gender, id_card, phone, email, enrollment_date)
SELECT
    id,
    CONCAT('S', LPAD(id, 6, '0')),
    1,
    '计算机科学与技术1班',
    '计算机科学与技术',
    '2021级',
    FLOOR(RAND() * 2) + 1,
    CONCAT('11010120000101', LPAD(FLOOR(RAND() * 10000), 4, '0')),
    phone,
    email,
    '2021-09-01'
FROM t_user
WHERE username IN ('student02', 'student03', 'student04', 'student05')
AND role_id=4
AND NOT EXISTS (
    SELECT 1 FROM t_student
    WHERE user_id IN (SELECT id FROM t_user WHERE username IN ('student02', 'student03', 'student04', 'student05'))
);

-- ================================================================
-- 5. 创建企业对接人账号
-- ================================================================
-- 先确保有企业数据
INSERT INTO t_company (company_name, short_name, industry, scale, city, contact_name, contact_phone, contact_email, verify_status, status)
VALUES
('字节跳动', '字节跳动', '互联网', '1000人以上', '北京', '张经理', '13900001001', 'zhang@bytedance.com', 'approved', 1),
('阿里巴巴', '阿里巴巴', '互联网', '1000人以上', '杭州', '李经理', '13900001002', 'li@alibaba.com', 'approved', 1)
ON DUPLICATE KEY UPDATE verify_status='approved';

-- 创建企业对接人账号
INSERT INTO t_user (username, password, real_name, phone, email, role_id, status)
VALUES
('enterprise01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '字节HR', '13900001001', 'hr@bytedance.com', 5, 1),
('enterprise02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '阿里HR', '13900001002', 'hr@alibaba.com', 5, 1)
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name);

-- ================================================================
-- 测试账号汇总（密码均为: 123456）
-- ================================================================
-- 管理员: admin
-- 学院负责人: college01
-- 教师: teacher01, teacher02, teacher03
-- 学生: student01, student02, student03, student04, student05
-- 企业对接人: enterprise01, enterprise02

SELECT '=====================================' AS '';
SELECT '测试账号创建完成!' AS '';
SELECT '=====================================' AS '';
SELECT
    u.username AS '用户名',
    u.real_name AS '姓名',
    CASE u.role_id
        WHEN 1 THEN '管理员'
        WHEN 2 THEN '学院负责人'
        WHEN 3 THEN '教师'
        WHEN 4 THEN '学生'
        WHEN 5 THEN '企业对接人'
    END AS '角色',
    '123456' AS '密码'
FROM t_user u
WHERE u.username IN (
    'admin', 'college01',
    'teacher01', 'teacher02', 'teacher03',
    'student01', 'student02', 'student03', 'student04', 'student05',
    'enterprise01', 'enterprise02'
)
ORDER BY u.role_id, u.id;
