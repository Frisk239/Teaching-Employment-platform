-- 简化的测试数据脚本（避免外键问题）
-- 创建日期: 2025-01-18

SET NAMES utf8mb4;

-- 1. 角色数据
INSERT INTO t_role (role_code, role_name, description) VALUES
('admin', '管理员', '拥有系统所有权限'),
('college_head', '学院负责人', '管理教学和就业'),
('teacher', '教师', '管理课程和学员'),
('user', '学员', '学员身份'),
('enterprise_contact', '企业对接人', '管理企业招聘');

-- 2. 学校数据
INSERT INTO t_school (school_name, school_code, address, contact_person, contact_phone) VALUES
('清华大学', 'TSINGHUA', '北京市海淀区', '张老师', '010-62785000'),
('北京大学', 'PKU', '北京市海淀区', '李老师', '010-62751234');

-- 3. 管理员
INSERT INTO t_user (id, username, password, real_name, phone, email, role_id, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', '13800000000', 'admin@teaching.com', 1, 1);

-- 4. 学院负责人
INSERT INTO t_user (id, username, password, real_name, phone, email, role_id, school_id, status) VALUES
(2, 'college_head1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张院长', '13800000001', 'zhang@tsinghua.edu.cn', 2, 1, 1),
(3, 'college_head2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李主任', '13800000002', 'li@pku.edu.cn', 2, 2, 1);

-- 5. 教师
INSERT INTO t_user (id, username, password, real_name, phone, email, role_id, school_id, status) VALUES
(4, 'teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王教授', '13800001001', 'wang@tsinghua.edu.cn', 3, 1, 1),
(5, 'teacher02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李老师', '13800001002', 'li2@tsinghua.edu.cn', 3, 1, 1);

INSERT INTO t_teacher (id, user_id, teacher_no, name, gender, phone, email, department, title) VALUES
(1, 4, 'T2021001', '王教授', 1, '13800001001', 'wang@tsinghua.edu.cn', '计算机系', '教授'),
(2, 5, 'T2021002', '李老师', 2, '13800001002', 'li2@tsinghua.edu.cn', '软件学院', '讲师');

-- 6. 学员（10个）
INSERT INTO t_user (id, username, password, real_name, phone, email, role_id, school_id, student_number, status) VALUES
(7, 'student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张三', '13800002001', 'zhangsan@student.com', 4, 1, '2021001001', 1),
(8, 'student02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李四', '13800002002', 'lisi@student.com', 4, 1, '2021001002', 1),
(9, 'student03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王五', '13800002003', 'wangwu@student.com', 4, 1, '2021001003', 1),
(10, 'student04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '赵六', '13800002004', 'zhaoliu@student.com', 4, 2, '2021002001', 1),
(11, 'student05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '钱七', '13800002005', 'qianqi@student.com', 4, 2, '2021002002', 1),
(12, 'student06', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '孙八', '13800002006', 'sunba@student.com', 4, 2, '2021002003', 1),
(13, 'student07', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '周九', '13800002007', 'zhoujiu@student.com', 4, 2, '2021002004', 1),
(14, 'student08', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '吴十', '13800002008', 'wushi@student.com', 4, 1, '2021001004', 1),
(15, 'student09', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '郑一', '13800002009', 'zhengyi@student.com', 4, 2, '2021002005', 1),
(16, 'student10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王二', '13800002010', 'wanger@student.com', 4, 2, '2021002006', 1);

INSERT INTO t_student (id, user_id, student_no, gender, phone, email, grade, major, class_name, enrollment_date) VALUES
(1, 7, '2021001001', 1, '13800002001', 'zhangsan@student.com', '2021级', '计算机科学与技术', '计科2101', '2021-09-01'),
(2, 8, '2021001002', 2, '13800002002', 'lisi@student.com', '2021级', '软件工程', '软工2101', '2021-09-01'),
(3, 9, '2021001003', 1, '13800002003', 'wangwu@student.com', '2021级', '计算机科学与技术', '计科2102', '2021-09-01'),
(4, 10, '2021002001', 2, '13800002004', 'zhaoliu@student.com', '2021级', '人工智能', '人工智能2101', '2021-09-01'),
(5, 11, '2021002002', 1, '13800002005', 'qianqi@student.com', '2021级', '数据科学', '数据科学2101', '2021-09-01'),
(6, 12, '2021002003', 2, '13800002006', 'sunba@student.com', '2021级', '软件工程', '软工2102', '2021-09-01'),
(7, 13, '2021002004', 1, '13800002007', 'zhoujiu@student.com', '2021级', '计算机科学与技术', '计科2103', '2021-09-01'),
(8, 14, '2021001004', 2, '13800002008', 'wushi@student.com', '2021级', '人工智能', '人工智能2102', '2021-09-01'),
(9, 15, '2021002005', 1, '13800002009', 'zhengyi@student.com', '2021级', '数据科学', '数据科学2102', '2021-09-01'),
(10, 16, '2021002006', 2, '13800002010', 'wanger@student.com', '2021级', '软件工程', '软工2103', '2021-09-01');

-- 7. 教育经历
INSERT INTO t_education (user_id, school_name, major, degree, start_date, end_date, description) VALUES
(7, '清华大学', '计算机科学与技术', '本科', '2021-09-01', '2025-06-30', 'GPA 3.8'),
(7, '北京四中', '理科', '高中', '2018-09-01', '2021-06-30', '理科班'),
(8, '清华大学', '软件工程', '本科', '2021-09-01', '2025-06-30', 'GPA 3.6'),
(9, '清华大学', '计算机科学与技术', '本科', '2021-09-01', '2025-06-30', '主修AI'),
(10, '北京大学', '人工智能', '硕士', '2021-09-01', '2024-06-30', '机器学习方向');

-- 8. 实习经历
INSERT INTO t_internship (user_id, company_name, position, department, start_date, end_date, description) VALUES
(7, '阿里巴巴', '前端开发实习生', '淘宝', '2024-07-01', '2024-09-30', 'Vue组件开发'),
(7, '字节跳动', '后端开发实习生', '今日头条', '2023-07-01', '2023-09-30', 'Go后端开发'),
(8, '腾讯', '前端开发实习生', '微信', '2024-07-01', '2024-10-31', '小程序开发'),
(9, '华为', '算法工程师实习生', '2012实验室', '2024-06-01', '2024-09-30', '推荐算法'),
(10, '美团', '数据分析师实习生', '商业分析', '2023-07-01', '2023-12-31', '用户行为分析');

-- 9. 企业
INSERT INTO t_company (id, company_name, industry, scale, location, website, contact_person, contact_phone) VALUES
(1, '阿里巴巴集团', '互联网', '大型企业', '杭州', 'https://www.alibaba.com', '张HR', '0571-12345678'),
(2, '腾讯科技', '互联网', '大型企业', '深圳', 'https://www.tencent.com', '李HR', '0755-12345678'),
(3, '字节跳动', '互联网', '大型企业', '北京', 'https://www.bytedance.com', '王HR', '010-12345678'),
(4, '华为技术', '科技', '大型企业', '深圳', 'https://www.huawei.com', '赵HR', '0755-87654321'),
(5, '美团', '互联网', '大型企业', '北京', 'https://www.meituan.com', '钱HR', '010-87654321');

-- 10. 企业对接人
INSERT INTO t_user (id, username, password, real_name, phone, email, role_id, company_id, status) VALUES
(17, 'enterprise01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张HR', '13800003001', 'zhanghr@alibaba.com', 5, 1, 1),
(18, 'enterprise02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李HR', '13800003002', 'lihr@tencent.com', 5, 2, 1),
(19, 'enterprise03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王HR', '13800003003', 'wanghr@bytedance.com', 5, 3, 1),
(20, 'enterprise04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '赵HR', '13800003004', 'zhaohr@huawei.com', 5, 4, 1),
(21, 'enterprise05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '钱HR', '13800003005', 'qianhr@meituan.com', 5, 5, 1);

-- 11. 职位
INSERT INTO t_position (id, company_id, position_name, position_type, salary_min, salary_max, location, requirements, description, recruitment_count) VALUES
(1, 1, 'Java后端开发工程师', 'fulltime', 15000, 25000, '杭州', '熟练掌握Java和Spring框架', '负责后端系统开发', 5),
(2, 1, '前端开发工程师', 'fulltime', 12000, 20000, '杭州', '熟练掌握Vue/React', '负责前端页面开发', 3),
(3, 2, 'Python开发工程师', 'fulltime', 18000, 28000, '深圳', '精通Python和Django/Flask', '负责AI平台开发', 4),
(4, 3, '算法工程师', 'fulltime', 25000, 40000, '北京', '熟悉机器学习算法', '负责推荐算法优化', 3),
(5, 4, '嵌入式开发工程师', 'fulltime', 18000, 28000, '深圳', '熟悉C/C++和嵌入式开发', '负责嵌入式系统开发', 6),
(6, 5, '数据分析师', 'fulltime', 15000, 22000, '北京', '熟悉SQL和数据分析', '负责业务数据分析', 4);

-- 12. 课程
INSERT INTO t_course (id, course_code, course_name, course_type, credits, hours, description, teacher_id) VALUES
(1, 'CS2101', '数据结构与算法', '专业必修', 4, 64, '学习基本数据结构', 1),
(2, 'CS2102', '计算机网络', '专业必修', 3, 48, '学习网络原理', 1),
(3, 'CS2103', '操作系统', '专业必修', 4, 64, '学习操作系统设计', 1),
(4, 'SE2101', '软件工程', '专业必修', 3, 48, '学习软件开发方法', 2),
(5, 'AI2101', '机器学习基础', '专业选修', 3, 48, '学习机器学习算法', 1);

-- 13. 课程学生关联
INSERT INTO t_course_student (course_id, student_id, enrollment_date, status) VALUES
(1, 1, '2024-02-01', 'active'),
(1, 2, '2024-02-01', 'active'),
(1, 3, '2024-02-01', 'active'),
(2, 1, '2024-02-01', 'active'),
(2, 2, '2024-02-01', 'active'),
(3, 1, '2024-02-01', 'active'),
(3, 3, '2024-02-01', 'active'),
(4, 2, '2024-02-01', 'active'),
(5, 1, '2024-02-01', 'active'),
(5, 3, '2024-02-01', 'active');

-- 14. 教室
INSERT INTO t_classroom (id, school_id, building_name, room_number, capacity, room_type, status) VALUES
(1, 1, '信息楼', '101', 60, '多媒体教室', 1),
(2, 1, '信息楼', '102', 60, '多媒体教室', 1),
(3, 2, '理科楼', '301', 80, '阶梯教室', 1);

-- 15. 课程表
INSERT INTO t_timetable (course_id, student_id, teacher_id, classroom_id, week_number, day_of_week, start_period, end_period, semester, school_year) VALUES
(1, 1, 1, 1, 1, 1, 1, 2, '2024-2025-1', '2024-2025'),
(2, 1, 1, 1, 1, 3, 1, 2, '2024-2025-1', '2024-2025'),
(3, 1, 1, 2, 1, 2, 1, 2, '2024-2025-1', '2024-2025'),
(1, 1, 1, 1, 2, 1, 3, 4, '2024-2025-1', '2024-2025');

-- 16. 作业
INSERT INTO t_homework (course_id, title, content, deadline) VALUES
(1, '链表实现练习', '实现单向链表的基本操作', '2025-01-25 23:59:59'),
(2, 'TCP/IP协议分析', '分析TCP三次握手过程', '2025-01-28 23:59:59');

-- 17. 日报
INSERT INTO t_daily_report (student_id, report_date, today_content, today_progress, problems, tomorrow_plan, study_hours, status, teacher_comment, rating) VALUES
(1, '2025-01-15', '学习了链表', '完成了链表增删改查', '需要多练习', '继续学习', 6.0, 'reviewed', '学习很好', 5),
(2, '2025-01-15', '学习了TCP协议', '理解了三次握手', '四次挥手要复习', '复习TCP', 5.5, 'reviewed', '掌握不错', 4);

-- 18. 考试
INSERT INTO t_exam (course_id, title, description, duration, start_time, end_time, full_score, passing_score, status) VALUES
(1, '数据结构期中考试', '考察链表栈队列', 90, '2025-01-20 10:00:00', '2025-01-20 11:30:00', 100, 60, 'published'),
(2, '网络协议测试', 'TCP/IP知识测试', 60, '2025-01-25 14:00:00', '2025-01-25 15:00:00', 100, 60, 'published');

-- 19. 考试记录
INSERT INTO t_student_exam_record (exam_id, student_id, start_time, end_time, score, status) VALUES
(1, 1, '2025-01-20 10:00:00', '2025-01-20 11:25:00', 85.0, 'completed'),
(1, 2, '2025-01-20 10:00:00', '2025-01-20 11:30:00', 78.0, 'completed');

-- 20. 求职申请
INSERT INTO t_job_application (position_id, student_id, company_id, status, apply_time) VALUES
(1, 1, 1, 'interview_passed', '2025-01-10 10:00:00'),
(3, 3, 3, 'hired', '2025-01-08 09:00:00'),
(4, 4, 4, 'submitted', '2025-01-15 11:00:00');

-- 21. 面试
INSERT INTO t_interview (id, student_id, company_id, position_id, interview_type, interview_time, duration, status, result, score, feedback) VALUES
(1, 1, 1, 1, 'online', '2025-01-12 14:00:00', 60, 'completed', 'pass', 85.0, '技术扎实'),
(3, 3, 3, 3, 'offline', '2025-01-10 14:00:00', 90, 'completed', 'pass', 92.0, '算法很好'),
(4, 4, 4, 4, 'online', '2025-01-20 14:00:00', 60, 'scheduled', 'pending', NULL, NULL);

-- 22. Offer
INSERT INTO t_offer (student_id, company_id, position_id, position_name, salary, start_date, status) VALUES
(3, 3, 3, '算法工程师', 35000.00, '2025-03-01', 'accepted'),
(1, 1, 1, 'Java后端开发工程师', 20000.00, '2025-03-15', 'pending');

-- 23. 人才库
INSERT INTO t_talent_pool (student_id, company_id, source, status, notes) VALUES
(2, 1, 'interview', 'active', '技术不错'),
(5, 2, 'application', 'inactive', '暂不匹配');

SELECT '========================================' AS divider;
SELECT '测试数据插入完成！' AS message;
SELECT '========================================' AS divider;
SELECT '' AS empty_line;
SELECT '测试账号（密码均为：123456）:' AS accounts;
SELECT '1. 管理员：admin' AS a1;
SELECT '2. 学院负责人：college_head1, college_head2' AS a2;
SELECT '3. 教师：teacher01, teacher02' AS a3;
SELECT '4. 学员：student01 ~ student10' AS a4;
SELECT '5. 企业对接人：enterprise01 ~ enterprise05' AS a5;
SELECT '========================================' AS divider;
