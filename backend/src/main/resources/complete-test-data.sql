-- 完整测试数据脚本
-- 创建日期: 2025-01-18
-- 说明: 包含所有角色的完整测试数据

SET NAMES utf8mb4;

-- ===================================
-- 1. 角色数据
-- ===================================
INSERT INTO t_role (role_code, role_name, description) VALUES
('admin', '管理员', '拥有系统所有权限'),
('college_head', '学院负责人', '管理教学和就业'),
('teacher', '教师', '管理课程和学员'),
('user', '学员', '学员身份'),
('enterprise_contact', '企业对接人', '管理企业招聘');

-- ===================================
-- 2. 学校数据
-- ===================================
INSERT INTO t_school (school_name, school_code, address, contact_person, contact_phone) VALUES
('清华大学', 'TSINGHUA', '北京市海淀区双清路30号', '张老师', '010-62785000'),
('北京大学', 'PKU', '北京市海淀区颐和园路5号', '李老师', '010-62751234'),
('浙江大学', 'ZJU', '浙江省杭州市西湖区余杭塘路866号', '王老师', '0571-88981234');

-- ===================================
-- 3. 管理员账号
-- ===================================
INSERT INTO t_user (username, password, real_name, phone, email, role_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', '13800000000', 'admin@teaching-employment.com', 1, 1);

-- ===================================
-- 4. 学院负责人账号
-- ===================================
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status) VALUES
('college_head1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张院长', '13800000001', 'zhang@tsinghua.edu.cn', 2, 1, 1),
('college_head2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李主任', '13800000002', 'li@pku.edu.cn', 2, 2, 1);

-- ===================================
-- 5. 教师账号
-- ===================================
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, status) VALUES
('teacher01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王教授', '13800001001', 'wang@tsinghua.edu.cn', 3, 1, 1),
('teacher02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李老师', '13800001002', 'li2@tsinghua.edu.cn', 3, 1, 1),
('teacher03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '赵副教授', '13800001003', 'zhao@pku.edu.cn', 3, 2, 1);

INSERT INTO t_teacher (user_id, teacher_no, name, gender, phone, email, department, title) VALUES
(4, 'T2021001', '王教授', 1, '13800001001', 'wang@tsinghua.edu.cn', '计算机系', '教授'),
(5, 'T2021002', '李老师', 2, '13800001002', 'li2@tsinghua.edu.cn', '软件学院', '讲师'),
(6, 'T2021003', '赵副教授', 1, '13800001003', 'zhao@pku.edu.cn', '信息学院', '副教授');

-- ===================================
-- 6. 学员账号（10个）
-- ===================================
INSERT INTO t_user (username, password, real_name, phone, email, role_id, school_id, student_number, status) VALUES
('student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张三', '13800002001', 'zhangsan@student.com', 4, 1, '2021001001', 1),
('student02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李四', '13800002002', 'lisi@student.com', 4, 1, '2021001002', 1),
('student03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王五', '13800002003', 'wangwu@student.com', 4, 1, '2021001003', 1),
('student04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '赵六', '13800002004', 'zhaoliu@student.com', 4, 2, '2021002001', 1),
('student05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '钱七', '13800002005', 'qianqi@student.com', 4, 2, '2021002002', 1),
('student06', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '孙八', '13800002006', 'sunba@student.com', 4, 3, '2021003001', 1),
('student07', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '周九', '13800002007', 'zhoujiu@student.com', 4, 3, '2021003002', 1),
('student08', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '吴十', '13800002008', 'wushi@student.com', 4, 1, '2021001004', 1),
('student09', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '郑一', '13800002009', 'zhengyi@student.com', 4, 2, '2021002003', 1),
('student10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王二', '13800002010', 'wanger@student.com', 4, 3, '2021003003', 1);

INSERT INTO t_student (user_id, student_no, gender, phone, email, grade, major, class_name, enrollment_date) VALUES
(7, '2021001001', 1, '13800002001', 'zhangsan@student.com', '2021级', '计算机科学与技术', '计科2101', '2021-09-01'),
(8, '2021001002', 2, '13800002002', 'lisi@student.com', '2021级', '软件工程', '软工2101', '2021-09-01'),
(9, '2021001003', 1, '13800002003', 'wangwu@student.com', '2021级', '计算机科学与技术', '计科2102', '2021-09-01'),
(10, '2021002001', 2, '13800002004', 'zhaoliu@student.com', '2021级', '人工智能', '人工智能2101', '2021-09-01'),
(11, '2021002002', 1, '13800002005', 'qianqi@student.com', '2021级', '数据科学', '数据科学2101', '2021-09-01'),
(12, '2021003001', 2, '13800002006', 'sunba@student.com', '2021级', '计算机科学与技术', '计科3101', '2021-09-01'),
(13, '2021003002', 1, '13800002007', 'zhoujiu@student.com', '2021级', '软件工程', '软工3101', '2021-09-01'),
(14, '2021001004', 2, '13800002008', 'wushi@student.com', '2021级', '人工智能', '人工智能2102', '2021-09-01'),
(15, '2021002003', 1, '13800002009', 'zhengyi@student.com', '2021级', '数据科学', '数据科学2102', '2021-09-01'),
(16, '2021003003', 2, '13800002010', 'wanger@student.com', '2021级', '计算机科学与技术', '计科3102', '2021-09-01');

-- ===================================
-- 7. 教育经历数据（为前5个学员添加）
-- ===================================
INSERT INTO t_education (user_id, school_name, major, degree, start_date, end_date, description) VALUES
(7, '清华大学', '计算机科学与技术', '本科', '2021-09-01', '2025-06-30', 'GPA: 3.8/4.0，主修计算机基础课程'),
(7, '北京四中', '理科', '高中', '2018-09-01', '2021-06-30', '理科班，多次获得校级奖学金'),
(8, '清华大学', '软件工程', '本科', '2021-09-01', '2025-06-30', 'GPA: 3.6/4.0，擅长前端开发'),
(9, '清华大学', '计算机科学与技术', '本科', '2021-09-01', '2025-06-30', '主修人工智能方向，参与过多个项目'),
(10, '北京大学', '人工智能', '硕士', '2021-09-01', '2024-06-30', '研究方向:机器学习，发表SCI论文1篇'),
(10, '复旦大学', '数学与应用数学', '本科', '2017-09-01', '2021-06-30', '数学基础扎实'),
(11, '北京大学', '数据科学', '本科', '2021-09-01', '2025-06-30', '专注于大数据分析和可视化');

-- ===================================
-- 8. 实习经历数据（为前5个学员添加）
-- ===================================
INSERT INTO t_internship (user_id, company_name, position, department, start_date, end_date, description) VALUES
(7, '阿里巴巴', '前端开发实习生', '淘宝事业部', '2024-07-01', '2024-09-30', '负责Vue组件开发，参与重构项目'),
(7, '字节跳动', '后端开发实习生', '今日头条', '2023-07-01', '2023-09-30', '使用Go开发后端API接口'),
(8, '腾讯', '前端开发实习生', '微信事业群', '2024-07-01', '2024-10-31', '负责小程序开发，优化用户体验'),
(9, '华为', '算法工程师实习生', '2012实验室', '2024-06-01', '2024-09-30', '参与推荐算法优化项目'),
(10, '美团', '数据分析师实习生', '商业分析部', '2023-07-01', '2023-12-31', '负责用户行为数据分析，产出报告'),
(11, '京东', '数据开发工程师实习生', '京东科技', '2024-07-01', '2024-09-30', '参与数据仓库建设，使用Spark和Hive');

-- ===================================
-- 9. 企业数据
-- ===================================
INSERT INTO t_company (company_name, industry, scale, location, website, description, contact_person, contact_phone, contact_email) VALUES
('阿里巴巴集团', '互联网', '大型企业', '杭州', 'https://www.alibaba.com', '全球领先的互联网公司', '张经理', '0571-12345678', 'hr@alibaba.com'),
('腾讯科技', '互联网', '大型企业', '深圳', 'https://www.tencent.com', '互联网增值服务提供商', '李经理', '0755-12345678', 'hr@tencent.com'),
('字节跳动', '互联网', '大型企业', '北京', 'https://www.bytedance.com', '技术驱动的移动互联网公司', '王经理', '010-12345678', 'hr@bytedance.com'),
('华为技术', '科技', '大型企业', '深圳', 'https://www.huawei.com', '全球领先的ICT解决方案提供商', '赵经理', '0755-87654321', 'hr@huawei.com'),
('美团', '互联网', '大型企业', '北京', 'https://www.meituan.com', '吃喝玩乐全都有', '钱经理', '010-87654321', 'hr@meituan.com');

-- ===================================
-- 10. 企业对接人账号
-- ===================================
INSERT INTO t_user (username, password, real_name, phone, email, role_id, company_id, status) VALUES
('enterprise01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张HR', '13800003001', 'zhanghr@alibaba.com', 5, 1, 1),
('enterprise02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李HR', '13800003002', 'lihr@tencent.com', 5, 2, 1),
('enterprise03', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王HR', '13800003003', 'wanghr@bytedance.com', 5, 3, 1),
('enterprise04', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '赵HR', '13800003004', 'zhaohr@huawei.com', 5, 4, 1),
('enterprise05', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '钱HR', '13800003005', 'qianhr@meituan.com', 5, 5, 1);

-- ===================================
-- 11. 职位数据
-- ===================================
INSERT INTO t_position (company_id, position_name, position_type, salary_min, salary_max, location, requirements, description, recruitment_count) VALUES
(1, 'Java后端开发工程师', 'fulltime', 15000, 25000, '杭州', '1. 熟练掌握Java\n2. 熟悉Spring框架\n3. 有良好的编码习惯', '负责后端系统开发和维护', 5),
(1, '前端开发工程师', 'fulltime', 12000, 20000, '杭州', '1. 熟练掌握Vue/React\n2. 精通HTML/CSS/JS\n3. 有大型项目经验', '负责前端页面开发和优化', 3),
(2, 'Python开发工程师', 'fulltime', 18000, 28000, '深圳', '1. 精通Python\n2. 熟悉Django/Flask\n3. 有AI项目经验优先', '负责AI平台开发', 4),
(2, 'Go开发工程师', 'fulltime', 20000, 30000, '深圳', '1. 熟悉Go语言\n2. 有分布式系统经验\n3. 熟悉微服务架构', '负责后端服务开发', 2),
(3, '算法工程师', 'fulltime', 25000, 40000, '北京', '1. 计算机/数学相关专业\n2. 熟悉机器学习算法\n3. 有论文发表优先', '负责推荐算法优化', 3),
(4, '嵌入式开发工程师', 'fulltime', 18000, 28000, '深圳', '1. 熟悉C/C++\n2. 有嵌入式开发经验\n3. 熟悉ARM架构', '负责嵌入式系统开发', 6),
(5, '数据分析师', 'fulltime', 15000, 22000, '北京', '1. 熟悉SQL\n2. 有数据分析经验\n3. 熟练使用Python/R', '负责业务数据分析', 4);

-- ===================================
-- 12. 课程数据
-- ===================================
INSERT INTO t_course (course_code, course_name, course_type, credits, hours, description, teacher_id) VALUES
('CS2101', '数据结构与算法', '专业必修', 4, 64, '学习基本的数据结构和算法设计', 1),
('CS2102', '计算机网络', '专业必修', 3, 48, '学习计算机网络的基本原理', 1),
('CS2103', '操作系统', '专业必修', 4, 64, '学习操作系统的设计与实现', 1),
('SE2101', '软件工程', '专业必修', 3, 48, '学习软件开发的工程方法', 2),
('AI2101', '机器学习基础', '专业选修', 3, 48, '学习机器学习的基本算法和应用', 1),
('AI2102', '深度学习', '专业选修', 3, 48, '学习深度学习的原理和实践', 1),
('DS2101', '数据科学导论', '专业必修', 2, 32, '学习数据处理和分析的基本方法', 2),
('CS2104', '数据库系统', '专业必修', 3, 48, '学习数据库的设计和管理', 2),
('SE2102', 'Web开发技术', '专业选修', 2, 32, '学习前后端开发技术', 2),
('AI2103', '自然语言处理', '专业选修', 3, 48, '学习NLP的基本理论和应用', 1);

-- ===================================
-- 13. 课程学生关联数据
-- ===================================
-- 注意：student_id 对应 t_student 表的 id，而不是 user_id
INSERT INTO t_course_student (course_id, student_id, enrollment_date, status) VALUES
(1, 1, '2024-02-01', 'active'),
(1, 2, '2024-02-01', 'active'),
(1, 3, '2024-02-01', 'active'),
(1, 4, '2024-02-01', 'active'),
(2, 1, '2024-02-01', 'active'),
(2, 2, '2024-02-01', 'active'),
(2, 5, '2024-02-01', 'active'),
(2, 6, '2024-02-01', 'active'),
(3, 1, '2024-02-01', 'active'),
(3, 3, '2024-02-01', 'active'),
(3, 4, '2024-02-01', 'active'),
(3, 7, '2024-02-01', 'active'),
(4, 2, '2024-02-01', 'active'),
(4, 6, '2024-02-01', 'active'),
(4, 8, '2024-02-01', 'active'),
(4, 9, '2024-02-01', 'active'),
(5, 1, '2024-02-01', 'active'),
(5, 3, '2024-02-01', 'active'),
(5, 4, '2024-02-01', 'active'),
(5, 8, '2024-02-01', 'active'),
(6, 1, '2024-02-01', 'active'),
(6, 3, '2024-02-01', 'active'),
(7, 5, '2024-02-01', 'active'),
(7, 8, '2024-02-01', 'active'),
(7, 9, '2024-02-01', 'active'),
(8, 2, '2024-02-01', 'active'),
(8, 6, '2024-02-01', 'active'),
(8, 7, '2024-02-01', 'active');

-- ===================================
-- 14. 教室数据
-- ===================================
INSERT INTO t_classroom (school_id, building_name, room_number, capacity, room_type, status) VALUES
(1, '信息楼', '101', 60, '多媒体教室', 1),
(1, '信息楼', '102', 60, '多媒体教室', 1),
(1, '信息楼', '201', 40, '普通教室', 1),
(1, '信息楼', '202', 40, '普通教室', 1),
(2, '理科楼', '301', 80, '阶梯教室', 1),
(2, '理科楼', '302', 80, '阶梯教室', 1),
(3, '工科楼', '401', 50, '实验室', 1);

-- ===================================
-- 15. 课程表数据（按周次）
-- ===================================
INSERT INTO t_timetable (course_id, student_id, teacher_id, classroom_id, week_number, day_of_week, start_period, end_period, semester, school_year) VALUES
-- 第1周课程
(1, 7, 1, 1, 1, 1, 1, 2, '2024-2025-1', '2024-2025'),
(2, 7, 1, 1, 1, 1, 3, 4, '2024-2025-1', '2024-2025'),
(3, 7, 1, 2, 1, 2, 1, 2, '2024-2025-1', '2024-2025'),
(4, 8, 2, 2, 1, 2, 3, 4, '2024-2025-1', '2024-2025'),
-- 第2周课程（略有不同）
(1, 7, 1, 1, 2, 1, 1, 2, '2024-2025-1', '2024-2025'),
(2, 7, 1, 1, 2, 3, 1, 2, '2024-2025-1', '2024-2025'),
(3, 7, 1, 3, 2, 2, 3, 4, '2024-2025-1', '2024-2025'),
-- 其他学员课程
(1, 8, 1, 1, 1, 1, 3, 4, '2024-2025-1', '2024-2025'),
(1, 9, 1, 1, 1, 3, 1, 2, '2024-2025-1', '2024-2025'),
(5, 10, 1, 3, 1, 2, 1, 2, '2024-2025-1', '2024-2025'),
(7, 11, 2, 5, 1, 3, 3, 4, '2024-2025-1', '2024-2025');

-- ===================================
-- 16. 作业数据
-- ===================================
INSERT INTO t_homework (course_id, title, content, deadline) VALUES
(1, '链表实现练习', '实现单向链表的基本操作，包括插入、删除、查找', '2025-01-25 23:59:59'),
(1, '二叉树遍历', '实现二叉树的前序、中序、后序遍历算法', '2025-02-01 23:59:59'),
(2, 'TCP/IP协议分析', '分析TCP三次握手和四次挥手过程', '2025-01-28 23:59:59'),
(3, '进程调度算法', '实现FCFS、SJF、RR等进程调度算法', '2025-02-05 23:59:59'),
(4, '敏捷开发实践', '学习Scrum敏捷开发流程，完成一个小项目', '2025-02-10 23:59:59');

-- ===================================
-- 17. 日报数据（示例）
-- ===================================
INSERT INTO t_daily_report (student_id, report_date, today_content, today_progress, problems, tomorrow_plan, study_hours, attachment_url, status, teacher_comment, rating) VALUES
(7, '2025-01-15', '学习了链表的基本概念和实现方法', '完成了单向链表的增删改查', '对指针的理解还不够深入', '继续学习双向链表和循环链表', 6.0, NULL, 'reviewed', '学习态度很好，继续努力！', 5),
(8, '2025-01-15', '学习了TCP协议的三次握手', '理解了SYN、ACK、FIN标志的作用', '四次挥手过程还需要复习', '复习TCP/IP协议', 5.5, NULL, 'reviewed', '掌握得不错', 4),
(9, '2025-01-15', '学习了进程调度算法', '实现了FCFS和SJF算法', 'RR算法的时间片选择不太理解', '继续学习RR算法并实现', 7.0, NULL, 'reviewed', '代码质量很好！', 5);

-- ===================================
-- 18. 考试数据
-- ===================================
INSERT INTO t_exam (course_id, title, description, duration, start_time, end_time, full_score, passing_score, status) VALUES
(1, '数据结构期中考试', '考察链表、栈、队列等基本数据结构', 90, '2025-01-20 10:00:00', '2025-01-20 11:30:00', 100, 60, 'published'),
(2, '网络协议测试', 'TCP/IP协议相关知识测试', 60, '2025-01-25 14:00:00', '2025-01-25 15:00:00', 100, 60, 'published'),
(3, '操作系统期中考试', '进程、线程、内存管理', 120, '2025-01-30 09:00:00', '2025-01-30 11:00:00', 100, 60, 'published');

-- ===================================
-- 19. 学员考试记录
-- ===================================
INSERT INTO t_student_exam_record (exam_id, student_id, start_time, end_time, score, status) VALUES
(1, 7, '2025-01-20 10:00:00', '2025-01-20 11:25:00', 85.0, 'completed'),
(1, 8, '2025-01-20 10:00:00', '2025-01-20 11:30:00', 78.0, 'completed'),
(1, 9, '2025-01-20 10:00:00', '2025-01-20 11:20:00', 92.0, 'completed'),
(2, 7, '2025-01-25 14:00:00', '2025-01-25 14:55:00', 88.0, 'completed');

-- ===================================
-- 20. 求职申请数据
-- ===================================
INSERT INTO t_job_application (position_id, student_id, company_id, status, apply_time) VALUES
(1, 7, 1, 'interview_passed', '2025-01-10 10:00:00'),
(2, 8, 2, 'test_passed', '2025-01-12 14:00:00'),
(3, 9, 3, 'hired', '2025-01-08 09:00:00'),
(4, 10, 4, 'submitted', '2025-01-15 11:00:00'),
(5, 11, 5, 'screened', '2025-01-14 15:00:00'),
(1, 14, 1, 'submitted', '2025-01-16 10:00:00');

-- ===================================
-- 21. 面试数据
-- ===================================
INSERT INTO t_interview (student_id, company_id, position_id, position_name, interview_type, interview_time, duration, location, status, result, score, feedback) VALUES
(7, 1, 1, 'Java后端开发工程师', 'online', '2025-01-12 14:00:00', 60, NULL, 'completed', 'pass', 85.0, '技术基础扎实，项目经验丰富'),
(8, 2, 2, 'Python开发工程师', 'online', '2025-01-15 10:00:00', 45, NULL, 'completed', 'pass', 78.0, 'Python基础不错，需要加强算法'),
(9, 3, 3, '算法工程师', 'offline', '2025-01-10 14:00:00', 90, '北京市海淀区中关村大街1号', 'completed', 'pass', 92.0, '算法思维很好，数学功底扎实'),
(7, 1, 1, 'Java后端开发工程师', 'online', '2025-01-18 10:00:00', 60, NULL, 'scheduled', 'pending', NULL, NULL),
(10, 4, 4, '嵌入式开发工程师', 'online', '2025-01-20 14:00:00', 60, NULL, 'pending', 'pending', NULL, NULL);

-- ===================================
-- 22. Offer数据
-- ===================================
INSERT INTO t_offer (student_id, company_id, position_id, position_name, salary, start_date, status) VALUES
(9, 3, 3, '算法工程师', 35000.00, '2025-03-01', 'accepted'),
(7, 1, 1, 'Java后端开发工程师', 20000.00, '2025-03-15', 'pending'),
(8, 2, 2, 'Python开发工程师', 25000.00, '2025-04-01', 'pending');

-- ===================================
-- 23. 人才库数据
-- ===================================
INSERT INTO t_talent_pool (student_id, company_id, source, status, notes) VALUES
(14, 1, 'interview', 'active', '技术能力不错，可以后续关注'),
(15, 2, 'application', 'inactive', '简历已查看，暂时不匹配'),
(16, 3, 'interview', 'active', '数学基础好，算法能力强');

-- ===================================
-- 完成提示
-- ===================================
SELECT '========== 测试数据插入完成！==========' AS message;
SELECT '账号信息如下（所有密码均为：123456）：' AS info;
SELECT '1. 管理员：admin' AS admin_account;
SELECT '2. 学院负责人：college_head1, college_head2' AS college_accounts;
SELECT '3. 教师：teacher01, teacher02, teacher03' AS teacher_accounts;
SELECT '4. 学员：student01 ~ student10' AS student_accounts;
SELECT '5. 企业对接人：enterprise01 ~ enterprise05' AS enterprise_accounts;
SELECT '==========================================' AS divider;
