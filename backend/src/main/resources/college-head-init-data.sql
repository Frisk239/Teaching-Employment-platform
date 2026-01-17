-- ================================================================
-- 高校教学就业平台 - 学院负责人测试数据初始化脚本
-- ================================================================

USE teaching_employment_platform;

-- ================================================================
-- 1. 创建学院负责人账号（如果不存在）
-- ================================================================
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status)
VALUES ('college01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李院长', '13800001001', 'college01@test.com', 2, 1, 1)
ON DUPLICATE KEY UPDATE
    real_name='李院长',
    phone='13800001001',
    email='college01@test.com';

-- ================================================================
-- 2. 创建学校数据
-- ================================================================
INSERT INTO t_school (school_name, school_type, address, province, city, establish_date, website, description, status)
VALUES
('北京大学', '本科', '北京市海淀区颐和园路5号', '北京', '北京', '1898-07-03', 'www.pku.edu.cn', '中国著名高等学府', 1),
('清华大学', '本科', '北京市海淀区双清路30号', '北京', '北京', '1911-04-29', 'www.tsinghua.edu.cn', '中国著名高等学府', 1),
('北京理工大学', '本科', '北京市海淀区中关村南大街5号', '北京', '北京', '1940-01-01', 'www.bit.edu.cn', '工业和信息化部直属高校', 1)
ON DUPLICATE KEY UPDATE school_name=VALUES(school_name);

-- ================================================================
-- 3. 创建教室数据
-- ================================================================
INSERT INTO t_classroom (room_no, building, floor, capacity, room_type, school_id, facilities, status)
VALUES
('A101', 'A座教学楼', 1, 60, '普通教室', 1, '投影仪,音响,空调', 1),
('A102', 'A座教学楼', 1, 80, '阶梯教室', 1, '投影仪,音响,空调,麦克风', 1),
('A201', 'A座教学楼', 2, 40, '普通教室', 1, '投影仪,音响,空调', 1),
('B301', 'B座实验楼', 3, 50, '机房', 1, '电脑50台,投影仪,空调', 1),
('B302', 'B座实验楼', 3, 50, '机房', 1, '电脑50台,投影仪,空调', 1),
('C101', 'C座办公楼', 1, 30, '会议室', 1, '投影仪,音响,空调,视频会议系统', 1)
ON DUPLICATE KEY UPDATE room_no=VALUES(room_no);

-- ================================================================
-- 4. 创建教师账号和数据
-- ================================================================
-- 删除已存在的教师用户（为了重新创建）
DELETE FROM t_teacher WHERE user_id IN (SELECT id FROM t_user WHERE username LIKE 'teacher%');
DELETE FROM t_user WHERE username LIKE 'teacher%' AND role_id = 3;

-- 创建教师账号
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status)
VALUES
('teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王强', '13800002001', 'teacher01@test.com', 3, 1, 1),
('teacher02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李娜', '13800002002', 'teacher02@test.com', 3, 1, 1),
('teacher03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张伟', '13800002003', 'teacher03@test.com', 3, 1, 1),
('teacher04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘芳', '13800002004', 'teacher04@test.com', 3, 1, 1),
('teacher05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈明', '13800002005', 'teacher05@test.com', 3, 1, 1);

-- 创建教师记录
INSERT INTO t_teacher (user_id, teacher_no, school_id, department, title, education, specialty, gender, phone, email, entry_date)
SELECT
    id,
    CONCAT('T', LPAD(id, 4, '0')),
    1,
    '计算机科学与技术学院',
    CASE ROW_NUMBER() OVER (ORDER BY id)
        WHEN 1 THEN '教授'
        WHEN 2 THEN '副教授'
        WHEN 3 THEN '讲师'
        WHEN 4 THEN '副教授'
        WHEN 5 THEN '讲师'
    END,
    CASE ROW_NUMBER() OVER (ORDER BY id)
        WHEN 1 THEN '博士'
        WHEN 2 THEN '博士'
        WHEN 3 THEN '硕士'
        WHEN 4 THEN '博士'
        WHEN 5 THEN '硕士'
    END,
    '计算机科学与技术',
    CASE ROW_NUMBER() OVER (ORDER BY id) % 2
        WHEN 0 THEN 2
        ELSE 1
    END,
    phone,
    email,
    '2020-09-01'
FROM t_user
WHERE username LIKE 'teacher%' AND role_id = 3;

-- ================================================================
-- 5. 创建学生账号和数据
-- ================================================================
-- 删除已存在的学生用户
DELETE FROM t_student WHERE user_id IN (SELECT id FROM t_user WHERE username LIKE 'student%');
DELETE FROM t_user WHERE username LIKE 'student%' AND role_id = 4;

-- 创建学生账号（30个学生）
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status)
VALUES
('student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', '13900001001', 'student01@test.com', 4, 1, 1),
('student02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', '13900001002', 'student02@test.com', 4, 1, 1),
('student03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', '13900001003', 'student03@test.com', 4, 1, 1),
('student04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵六', '13900001004', 'student04@test.com', 4, 1, 1),
('student05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '钱七', '13900001005', 'student05@test.com', 4, 1, 1),
('student06', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙八', '13900001006', 'student06@test.com', 4, 1, 1),
('student07', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周九', '13900001007', 'student07@test.com', 4, 1, 1),
('student08', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '吴十', '13900001008', 'student08@test.com', 4, 1, 1),
('student09', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '郑十一', '13900001009', 'student09@test.com', 4, 1, 1),
('student10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王小二', '13900001010', 'student10@test.com', 4, 1, 1),
('student11', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李小三', '13900001011', 'student11@test.com', 4, 1, 1),
('student12', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张小四', '13900001012', 'student12@test.com', 4, 1, 1),
('student13', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '刘小五', '13900001013', 'student13@test.com', 4, 1, 1),
('student14', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈小六', '13900001014', 'student14@test.com', 4, 1, 1),
('student15', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '杨小七', '13900001015', 'student15@test.com', 4, 1, 1),
('student16', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '黄小八', '13900001016', 'student16@test.com', 4, 1, 1),
('student17', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵小九', '13900001017', 'student17@test.com', 4, 1, 1),
('student18', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '吴小十', '13900001018', 'student18@test.com', 4, 1, 1),
('student19', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周小一', '13900001019', 'student19@test.com', 4, 1, 1),
('student20', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '徐小二', '13900001020', 'student20@test.com', 4, 1, 1),
('student21', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙小三', '13900001021', 'student21@test.com', 4, 1, 1),
('student22', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '马小四', '13900001022', 'student22@test.com', 4, 1, 1),
('student23', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '朱小五', '13900001023', 'student23@test.com', 4, 1, 1),
('student24', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '胡小六', '13900001024', 'student24@test.com', 4, 1, 1),
('student25', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '林小七', '13900001025', 'student25@test.com', 4, 1, 1),
('student26', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '何小八', '13900001026', 'student26@test.com', 4, 1, 1),
('student27', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '高小九', '13900001027', 'student27@test.com', 4, 1, 1),
('student28', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '罗小十', '13900001028', 'student28@test.com', 4, 1, 1),
('student29', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '梁一一', '13900001029', 'student29@test.com', 4, 1, 1),
('student30', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '宋一二', '13900001030', 'student30@test.com', 4, 1, 1);

-- 创建学生记录
INSERT INTO t_student (user_id, student_no, school_id, class_name, major, grade, gender, id_card, phone, email, enrollment_date, graduation_date, education, political_status, health_status, seeking_status)
SELECT
    id,
    CONCAT('2021', LPAD(id, 4, '0')),
    1,
    CASE ((id - 1) % 3)
        WHEN 0 THEN '计算机科学与技术1班'
        WHEN 1 THEN '计算机科学与技术2班'
        ELSE '软件工程1班'
    END,
    CASE ((id - 1) % 2)
        WHEN 0 THEN '计算机科学与技术'
        ELSE '软件工程'
    END,
    '2021级',
    ((id - 1) % 2) + 1,
    CONCAT('11010120000101', LPAD(id, 4, '0')),
    phone,
    email,
    '2021-09-01',
    '2025-06-30',
    '本科',
    CASE ((id - 1) % 4)
        WHEN 0 THEN '党员'
        WHEN 1 THEN '团员'
        ELSE '群众'
    END,
    '健康',
    CASE ((id - 1) % 5)
        WHEN 0 THEN 'employed'
        WHEN 1 THEN 'seeking'
        WHEN 2 THEN 'admitted'
        ELSE 'actively_seeking'
    END
FROM t_user
WHERE username LIKE 'student%' AND role_id = 4;

-- ================================================================
-- 6. 创建课程数据
-- ================================================================
INSERT INTO t_course (course_name, course_code, course_type, credits, hours, school_id, department, description, status)
VALUES
('数据结构与算法', 'CS201', '专业必修', 4, 64, 1, '计算机科学与技术学院', '介绍基本数据结构和算法设计方法', 1),
('计算机网络', 'CS202', '专业必修', 3, 48, 1, '计算机科学与技术学院', '介绍计算机网络的基本原理和技术', 1),
('操作系统', 'CS203', '专业必修', 4, 64, 1, '计算机科学与技术学院', '介绍操作系统的基本原理和设计', 1),
('数据库系统', 'CS204', '专业必修', 3, 48, 1, '计算机科学与技术学院', '介绍数据库的设计与应用', 1),
('软件工程', 'CS205', '专业必修', 3, 48, 1, '计算机科学与技术学院', '介绍软件工程的基本概念和方法', 1),
('Java程序设计', 'CS206', '专业选修', 3, 48, 1, '计算机科学与技术学院', 'Java语言编程基础与应用', 1),
('Python程序设计', 'CS207', '专业选修', 3, 48, 1, '计算机科学与技术学院', 'Python语言编程基础与应用', 1),
('前端开发技术', 'CS208', '专业选修', 3, 48, 1, '计算机科学与技术学院', 'Web前端开发技术', 1),
('人工智能导论', 'CS209', '专业选修', 2, 32, 1, '计算机科学与技术学院', '人工智能基础理论与应用', 1),
('大数据技术', 'CS210', '专业选修', 2, 32, 1, '计算机科学与技术学院', '大数据处理技术与应用', 1)
ON DUPLICATE KEY UPDATE course_code=VALUES(course_code);

-- ================================================================
-- 7. 创建就业相关数据
-- ================================================================

-- 7.1 创建企业数据（如果不存在）
INSERT INTO t_company (company_name, short_name, industry, scale, city, address, contact_name, contact_phone, contact_email, verify_status, status)
VALUES
('腾讯科技', '腾讯', '互联网', '1000人以上', '深圳', '深圳市南山区科技园', '张经理', '13800003001', 'zhang@tencent.com', 'approved', 1),
('阿里巴巴', '阿里巴巴', '互联网', '1000人以上', '杭州', '杭州市余杭区', '李经理', '13800003002', 'li@alibaba.com', 'approved', 1),
('字节跳动', '字节跳动', '互联网', '1000人以上', '北京', '北京市海淀区', '王经理', '13800003003', 'wang@bytedance.com', 'approved', 1),
('美团', '美团', '互联网', '1000人以上', '北京', '北京市朝阳区', '赵经理', '13800003004', 'zhao@meituan.com', 'approved', 1),
('京东', '京东', '电商', '1000人以上', '北京', '北京市大兴区', '钱经理', '13800003005', 'qian@jd.com', 'approved', 1),
('华为技术', '华为', '通信', '1000人以上', '深圳', '深圳市龙岗区', '孙经理', '13800003006', 'sun@huawei.com', 'approved', 1),
('网易', '网易', '互联网', '1000人以上', '杭州', '杭州市滨江区', '周经理', '13800003007', 'zhou@netease.com', 'approved', 1),
('小米科技', '小米', '互联网', '1000人以上', '北京', '北京市海淀区', '吴经理', '13800003008', 'wu@xiaomi.com', 'approved', 1),
('百度', '百度', '互联网', '1000人以上', '北京', '北京市海淀区', '郑经理', '13800003009', 'zheng@baidu.com', 'approved', 1),
('滴滴出行', '滴滴', '互联网', '1000人以上', '北京', '北京市海淀区', '陈经理', '13800003010', 'chen@didiglobal.com', 'approved', 1)
ON DUPLICATE KEY UPDATE company_name=VALUES(company_name);

-- 7.2 创建职位数据
INSERT INTO t_position (position_name, company_id, department, position_type, city, district, address, salary_min, salary_max, salary_unit, recruit_count, responsibility, requirement, education, experience, status, creator_id)
SELECT
    CONCAT(CASE (company.id % 10)
        WHEN 1 THEN 'Java开发工程师'
        WHEN 2 THEN '前端开发工程师'
        WHEN 3 THEN 'Python开发工程师'
        WHEN 4 THEN '算法工程师'
        WHEN 5 THEN '产品经理'
        WHEN 6 THEN '测试工程师'
        WHEN 7 THEN '运维工程师'
        WHEN 8 THEN '数据分析师'
        WHEN 9 THEN 'UI设计师'
        ELSE '全栈开发工程师'
    END, CASE (company.id % 5)
        WHEN 0 THEN '校招'
        ELSE '社招'
    END),
    company.id,
    '技术部',
    '全职',
    CASE (company.id % 3)
        WHEN 0 THEN '深圳'
        WHEN 1 THEN '北京'
        WHEN 2 THEN '杭州'
        ELSE '上海'
    END,
    '海淀区',
    '详细地址',
    8000 + (company.id % 10) * 2000,
    12000 + (company.id % 10) * 3000,
    'month',
    5,
    '负责相关模块的开发工作',
    '本科及以上学历，计算机相关专业',
    CASE (company.id % 3)
        WHEN 0 THEN '本科'
        WHEN 1 THEN '硕士'
        ELSE '本科及以上'
    END,
    CASE (company.id % 4)
        WHEN 0 THEN '应届生'
        WHEN 1 THEN '1年'
        WHEN 2 THEN '2年'
        ELSE '3年以上'
    END,
    'active',
    (SELECT id FROM t_user WHERE username = 'admin' LIMIT 1)
FROM t_company
WHERE company.id <= 10
ON DUPLICATE KEY UPDATE position_name=VALUES(position_name);

-- 7.3 创建求职申请数据
INSERT INTO t_application (student_id, position_id, resume_url, status, apply_time)
SELECT
    s.id,
    p.id,
    CONCAT('/resumes/resume_', s.id, '.pdf'),
    CASE ((s.id - 1) % 10)
        WHEN 0 THEN 'pending'
        WHEN 1 THEN 'screened'
        WHEN 2 THEN 'test_passed'
        WHEN 3 THEN 'test_failed'
        WHEN 4 THEN 'interview_passed'
        WHEN 5 THEN 'interview_failed'
        WHEN 6 THEN 'offered'
        WHEN 7 THEN 'hired'
        WHEN 8 THEN 'rejected'
        ELSE 'pending'
    END,
    DATE_ADD('2024-01-01', INTERVAL (s.id - 1) DAY)
FROM t_student s
CROSS JOIN (SELECT id FROM t_position LIMIT 10) p
WHERE s.id <= 20
AND NOT EXISTS (
    SELECT 1 FROM t_application a
    WHERE a.student_id = s.id AND a.position_id = p.id
)
LIMIT 100;

-- 7.4 创建笔试数据
INSERT INTO t_written_test (application_id, test_type, test_date, test_duration, test_score, pass_score, status)
SELECT
    a.id,
    CASE (a.id % 3)
        WHEN 0 THEN '在线笔试'
        WHEN 1 THEN '现场笔试'
        ELSE '编程测试'
    END,
    DATE_ADD(a.apply_time, INTERVAL 7 DAY),
    120,
    CASE (a.id % 20)
        WHEN 0 THEN 95
        WHEN 1 THEN 90
        WHEN 2 THEN 85
        WHEN 3 THEN 80
        WHEN 4 THEN 75
        WHEN 5 THEN 70
        WHEN 6 THEN 60
        ELSE 65
    END,
    60,
    CASE (a.id % 20)
        WHEN 0 THEN 'passed'
        WHEN 1 THEN 'passed'
        WHEN 2 THEN 'passed'
        WHEN 3 THEN 'passed'
        WHEN 4 THEN 'passed'
        WHEN 5 THEN 'passed'
        WHEN 6 THEN 'passed'
        WHEN 7 THEN 'passed'
        WHEN 8 THEN 'passed'
        WHEN 9 THEN 'passed'
        WHEN 10 THEN 'passed'
        WHEN 11 THEN 'passed'
        WHEN 12 THEN 'passed'
        WHEN 13 THEN 'passed'
        WHEN 14 THEN 'passed'
        WHEN 15 THEN 'passed'
        ELSE 'failed'
    END
FROM t_application a
WHERE a.id % 2 = 0
AND a.id <= 50
AND NOT EXISTS (
    SELECT 1 FROM t_written_test wt
    WHERE wt.application_id = a.id
);

-- 7.5 创建面试数据
INSERT INTO t_interview (application_id, interview_type, interview_date, interview_time, interviewer, location, status, result, feedback)
SELECT
    a.id,
    CASE (a.id % 3)
        WHEN 0 THEN '技术面试'
        WHEN 1 THEN 'HR面试'
        ELSE '综合面试'
    END,
    DATE_ADD(a.apply_time, INTERVAL 14 DAY),
    '14:00',
    CASE (a.id % 10)
        WHEN 0 THEN '张面试官'
        WHEN 1 THEN '李面试官'
        WHEN 2 THEN '王面试官'
        WHEN 3 THEN '赵面试官'
        WHEN 4 THEN '钱面试官'
        WHEN 5 THEN '孙面试官'
        WHEN 6 THEN '周面试官'
        WHEN 7 THEN '吴面试官'
        WHEN 8 THEN '郑面试官'
        ELSE '陈面试官'
    END,
    '公司会议室',
    'completed',
    CASE (a.id % 20)
        WHEN 0 THEN 'passed'
        WHEN 1 THEN 'passed'
        WHEN 2 THEN 'passed'
        WHEN 3 THEN 'passed'
        WHEN 4 THEN 'passed'
        WHEN 5 THEN 'passed'
        WHEN 6 THEN 'passed'
        WHEN 7 THEN 'passed'
        WHEN 8 THEN 'passed'
        WHEN 9 THEN 'passed'
        WHEN 10 THEN 'passed'
        WHEN 11 THEN 'passed'
        WHEN 12 THEN 'passed'
        ELSE 'failed'
    END,
    '面试表现良好'
FROM t_application a
WHERE a.id % 3 = 0
AND a.id <= 40
AND NOT EXISTS (
    SELECT 1 FROM t_interview i
    WHERE i.application_id = a.id
);

-- 7.6 创建Offer数据
INSERT INTO t_offer (application_id, position_id, student_id, company_id, salary, salary_unit, start_date, status, accept_deadline, offer_file_url)
SELECT
    a.id,
    a.position_id,
    a.student_id,
    p.company_id,
    p.salary_min + 1000,
    p.salary_unit,
    '2024-07-01',
    CASE (a.id % 5)
        WHEN 0 THEN 'pending'
        WHEN 1 THEN 'accepted'
        WHEN 2 THEN 'rejected'
        WHEN 3 THEN 'expired'
        ELSE 'pending'
    END,
    DATE_ADD(CURDATE(), INTERVAL 7 DAY),
    CONCAT('/offers/offer_', a.id, '.pdf')
FROM t_application a
JOIN t_position p ON a.position_id = p.id
WHERE a.id % 4 = 0
AND a.id <= 30
AND NOT EXISTS (
    SELECT 1 FROM t_offer o
    WHERE o.application_id = a.id
);

-- ================================================================
-- 8. 创建日报数据
-- ================================================================
INSERT INTO t_daily_report (student_id, report_date, content, difficulties, tomorrow_plan, study_hours, completed_tasks, status)
SELECT
    s.id,
    DATE_SUB(CURDATE(), INTERVAL ((s.id - 1) % 30) DAY),
    CONCAT('今日学习了', CASE ((s.id - 1) % 5)
        WHEN 0 THEN '数据结构'
        WHEN 1 THEN '算法'
        WHEN 2 THEN '数据库'
        WHEN 3 THEN '前端开发'
        ELSE 'Java编程'
    END, '相关知识'),
    CASE ((s.id - 1) % 3)
        WHEN 0 THEN '理解有些困难'
        WHEN 1 THEN '需要多练习'
        ELSE '基本掌握'
    END,
    '继续深入学习相关知识',
    6 + ((s.id - 1) % 4),
    3 + ((s.id - 1) % 5),
    'submitted'
FROM t_student s
WHERE s.id <= 20
AND NOT EXISTS (
    SELECT 1 FROM t_daily_report dr
    WHERE dr.student_id = s.id
    AND dr.report_date = DATE_SUB(CURDATE(), INTERVAL ((s.id - 1) % 30) DAY)
);

-- ================================================================
-- 9. 创建作业数据
-- ================================================================
-- 创建作业
INSERT INTO t_homework (course_id, title, description, homework_type, due_date, total_score, status, creator_id)
SELECT
    c.id,
    CONCAT(c.course_name, ' - 作业', ((c.id - 1) % 5) + 1),
    CONCAT('完成', c.course_name, '相关练习题'),
    CASE ((c.id - 1) % 3)
        WHEN 0 THEN '练习'
        WHEN 1 THEN '实验'
        ELSE '项目'
    END,
    DATE_ADD(CURDATE(), INTERVAL 14 DAY),
    100,
    'published',
    (SELECT id FROM t_user WHERE username = 'teacher01' LIMIT 1)
FROM t_course c
WHERE c.id <= 5
AND NOT EXISTS (
    SELECT 1 FROM t_homework h
    WHERE h.course_id = c.id
    AND h.title = CONCAT(c.course_name, ' - 作业', ((c.id - 1) % 5) + 1)
);

-- 创建作业提交
INSERT INTO t_homework_submit (homework_id, student_id, submit_time, content, file_url, score, status, comment)
SELECT
    h.id,
    s.id,
    DATE_SUB(h.due_date, INTERVAL 2 DAY),
    '作业内容已完成',
    CONCAT('/homework/submission_', h.id, '_', s.id, '.pdf'),
    80 + ((s.id - 1) % 20),
    'submitted',
    CASE ((s.id - 1) % 4)
        WHEN 0 THEN '完成得很好'
        WHEN 1 THEN '继续努力'
        WHEN 2 THEN '需要改进'
        ELSE '优秀'
    END
FROM t_homework h
CROSS JOIN t_student s
WHERE h.id <= 5
AND s.id <= 20
AND NOT EXISTS (
    SELECT 1 FROM t_homework_submit hs
    WHERE hs.homework_id = h.id
    AND hs.student_id = s.id
);

-- ================================================================
-- 10. 创建人才库数据
-- ================================================================
INSERT INTO t_talent_pool (company_id, student_id, source, status, notes, contact_date)
SELECT
    c.id,
    s.id,
    CASE ((s.id - 1) % 4)
        WHEN 0 THEN '求职申请'
        WHEN 1 THEN '面试推荐'
        WHEN 2 THEN '校园招聘'
        ELSE '内部推荐'
    END,
    CASE ((s.id - 1) % 3)
        WHEN 0 THEN 'active'
        WHEN 1 THEN 'contacted'
        ELSE 'new'
    END,
    '优秀人才，值得跟进',
    CURDATE()
FROM t_company c
CROSS JOIN t_student s
WHERE c.id <= 5
AND s.id <= 15
AND ((c.id + s.id) % 3 = 0)
AND NOT EXISTS (
    SELECT 1 FROM t_talent_pool tp
    WHERE tp.company_id = c.id
    AND tp.student_id = s.id
);

-- ================================================================
-- 测试账号信息
-- ================================================================
SELECT '=====================================' AS '';
SELECT '学院负责人测试数据初始化完成!' AS '';
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
WHERE u.username IN ('college01', 'teacher01', 'student01')
ORDER BY u.role_id;

SELECT '=====================================' AS '';
SELECT '统计信息：' AS '';
SELECT '=====================================' AS '';
SELECT CONCAT('学校数量: ', COUNT(*)) AS '统计' FROM t_school;
SELECT CONCAT('教室数量: ', COUNT(*)) AS '统计' FROM t_classroom;
SELECT CONCAT('课程数量: ', COUNT(*)) AS '统计' FROM t_course;
SELECT CONCAT('教师数量: ', COUNT(*)) AS '统计' FROM t_teacher;
SELECT CONCAT('学生数量: ', COUNT(*)) AS '统计' FROM t_student;
SELECT CONCAT('企业数量: ', COUNT(*)) AS '统计' FROM t_company;
SELECT CONCAT('职位数量: ', COUNT(*)) AS '统计' FROM t_position;
SELECT CONCAT('求职申请: ', COUNT(*)) AS '统计' FROM t_application;
SELECT CONCAT('笔试记录: ', COUNT(*)) AS '统计' FROM t_written_test;
SELECT CONCAT('面试记录: ', COUNT(*)) AS '统计' FROM t_interview;
SELECT CONCAT('Offer数量: ', COUNT(*)) AS '统计' FROM t_offer;
SELECT CONCAT('日报数量: ', COUNT(*)) AS '统计' FROM t_daily_report;
SELECT CONCAT('作业数量: ', COUNT(*)) AS '统计' FROM t_homework;
SELECT CONCAT('人才库: ', COUNT(*)) AS '统计' FROM t_talent_pool;
