-- =============================================
-- 测试数据脚本
-- 用于API测试的教师、学生、课程等数据
-- =============================================

USE teaching_employment_platform;

-- =============================================
-- 1. 创建测试用户（教师和学生）
-- =============================================
INSERT INTO t_user (username, password, real_name, role_id, status, phone, email) VALUES
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', 3, 1, '13800138001', 'teacher001@test.com'),
('teacher002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李老师', 3, 1, '13800138002', 'teacher002@test.com'),
('student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王小明', 4, 1, '13900139001', 'student001@test.com'),
('student002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李小红', 4, 1, '13900139002', 'student002@test.com'),
('student003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵小刚', 4, 1, '13900139003', 'student003@test.com');

-- =============================================
-- 2. 创建教师信息
-- =============================================
INSERT INTO t_teacher (user_id, teacher_no, school_id, department, title, education, specialty, gender, status) VALUES
(2, 'T20240001', 1, '计算机系', '教授', '博士', '人工智能', 1, 1),
(3, 'T20240002', 1, '软件工程系', '副教授', '博士', '软件工程', 0, 1);

-- =============================================
-- 3. 创建学生信息
-- =============================================
INSERT INTO t_student (user_id, student_no, school_id, grade, major, class_name, gender, enrollment_date, status) VALUES
(4, 'S20240001', 1, '2021级', '计算机科学与技术', '计科2101', 1, '2021-09-01', 1),
(5, 'S20240002', 1, '2021级', '软件工程', '软件2101', 0, '2021-09-01', 1),
(6, 'S20240003', 1, '2021级', '数据科学', '数据2101', 1, '2021-09-01', 1);

-- =============================================
-- 4. 创建课程信息
-- =============================================
INSERT INTO t_course (course_name, course_code, course_type, school_id, teacher_id, description, credit, total_hours, max_students, status) VALUES
('Java程序设计', 'CS101', 'regular', 1, 1, '学习Java编程基础知识和面向对象编程思想', 4.0, 64, 50, 'ongoing'),
('数据结构与算法', 'CS201', 'regular', 1, 1, '学习常用数据结构和算法设计', 3.5, 56, 40, 'ongoing'),
('Web开发技术', 'CS301', 'regular', 1, 2, '学习前端和后端Web开发技术', 3.0, 48, 30, 'pending');

-- =============================================
-- 5. 创建选课记录
-- =============================================
INSERT INTO t_course_student (course_id, student_id, enrollment_date, status, progress) VALUES
(1, 1, NOW(), 1, 0.00),
(1, 2, NOW(), 1, 0.00),
(2, 1, NOW(), 1, 0.00),
(2, 3, NOW(), 1, 0.00);

-- =============================================
-- 6. 创建作业信息
-- =============================================
INSERT INTO t_homework (course_id, teacher_id, title, description, homework_type, max_score, deadline, status) VALUES
(1, 1, '第一次作业：Java基础', '完成Java基础练习题', 'assignment', 100, '2026-02-28 23:59:59', 'published'),
(1, 1, '第二次作业：面向对象', '完成面向对象编程练习', 'assignment', 100, '2026-03-15 23:59:59', 'published'),
(2, 1, '算法设计作业', '设计并实现常见算法', 'project', 100, '2026-03-31 23:59:59', 'published');

-- =============================================
-- 7. 创建企业信息
-- =============================================
INSERT INTO t_company (company_name, industry, scale, province, city, address, contact_person, contact_phone, email, website, description, status) VALUES
('阿里巴巴集团', '互联网', 'large', '浙江省', '杭州市', '杭州市余杭区', '张HR', '0571-12345678', 'hr@alibaba.com', 'https://www.alibaba.com', '阿里巴巴集团是全球领先的互联网公司', 1),
('腾讯科技', '互联网', 'large', '广东省', '深圳市', '深圳市南山区', '李HR', '0755-87654321', 'hr@tencent.com', 'https://www.tencent.com', '腾讯是中国领先的互联网增值服务提供商', 1),
('字节跳动', '互联网', 'large', '北京市', '北京市', '北京市海淀区', '王HR', '010-12345678', 'hr@bytedance.com', 'https://www.bytedance.com', '字节跳动是全球知名的科技公司', 1);

-- =============================================
-- 8. 创建职位信息
-- =============================================
INSERT INTO t_position (company_id, position_name, position_type, department, salary_min, salary_max, city, recruitment_count, education_requirement, work_experience, description, requirements, status) VALUES
(1, 'Java开发工程师', 'full_time', '技术部', 15000, 25000, '杭州市', 10, '本科', '1-3年', '负责后端系统开发', '熟悉Java、Spring框架，有良好的编程基础', 'active'),
(1, '前端开发工程师', 'full_time', '技术部', 12000, 20000, '杭州市', 5, '本科', '1-3年', '负责前端页面开发', '熟悉Vue、React等前端框架', 'active'),
(2, '软件开发工程师', 'full_time', '技术部', 18000, 30000, '深圳市', 15, '本科', '1-3年', '负责软件开发和维护', '熟悉C++/Java/Python，有良好的编程能力', 'active'),
(3, '算法工程师', 'full_time', 'AI Lab', 20000, 35000, '北京市', 8, '硕士', '1-3年', '负责算法研发和优化', '熟悉机器学习、深度学习算法', 'active');

-- =============================================
-- 9. 创建简历信息
-- =============================================
INSERT INTO t_resume (student_id, name, gender, phone, email, education, school, major, graduation_date, skills, project_experience, internship_experience, status) VALUES
(1, '王小明', 1, '13900139001', 'student001@test.com', '本科', '科技大学', '计算机科学与技术', '2025-06-30', 'Java, Python, MySQL, Vue.js', '校园管理系统、电商平台', '阿里巴巴实习3个月', 1),
(2, '李小红', 0, '13900139002', 'student002@test.com', '本科', '科技大学', '软件工程', '2025-06-30', 'Java, Spring Boot, React, MongoDB', '在线教育平台、社交媒体应用', '腾讯实习4个月', 1),
(3, '赵小刚', 1, '13900139003', 'student003@test.com', '本科', '科技大学', '数据科学', '2025-06-30', 'Python, TensorFlow, PyTorch, SQL', '推荐系统、数据分析项目', '字节跳动实习6个月', 1);

-- =============================================
-- 10. 创建求职申请
-- =============================================
INSERT INTO t_application (position_id, student_id, resume_id, apply_date, status) VALUES
(1, 1, 1, NOW(), 'pending'),
(2, 2, 2, NOW(), 'pending'),
(4, 3, 3, NOW(), 'pending');

COMMIT;

SELECT '测试数据插入完成！' AS message;
SELECT COUNT(*) AS teacher_count FROM t_teacher;
SELECT COUNT(*) AS student_count FROM t_student;
SELECT COUNT(*) AS course_count FROM t_course;
SELECT COUNT(*) AS company_count FROM t_company;
SELECT COUNT(*) AS position_count FROM t_position;
