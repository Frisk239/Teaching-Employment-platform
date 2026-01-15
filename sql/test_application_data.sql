-- 插入求职管理测试数据
USE teaching_employment_platform;
SET NAMES utf8mb4;

-- ==========================================
-- 1. 插入学生用户数据
-- ==========================================
INSERT INTO t_user (id, username, password, real_name, phone, email, role_id, school_id, status, create_time, update_time) VALUES
(10, 'student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', '13800138001', 'zhangsan@example.com', 5, 1, 1, NOW(), NOW()),
(11, 'student002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', '13800138002', 'lisi@example.com', 5, 1, 1, NOW(), NOW()),
(12, 'student003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', '13800138003', 'wangwu@example.com', 5, 1, 1, NOW(), NOW()),
(13, 'student004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵六', '13800138004', 'zhaoliu@example.com', 5, 1, 1, NOW(), NOW()),
(14, 'student005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙七', '13800138005', 'sunqi@example.com', 5, 1, 1, NOW(), NOW()),
(15, 'student006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周八', '13800138006', 'zhouba@example.com', 5, 1, 1, NOW(), NOW()),
(16, 'student007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '吴九', '13800138007', 'wujiu@example.com', 5, 1, 1, NOW(), NOW()),
(17, 'student008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '郑十', '13800138008', 'zhengshi@example.com', 5, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  real_name = VALUES(real_name),
  phone = VALUES(phone),
  update_time = NOW();

-- 插入学生基本信息
INSERT INTO t_student (
    id, user_id, student_no, school_id, class_name, major, grade, gender,
    birth_date, id_card, phone, email, address, enrollment_date, graduation_date,
    political_status, nation, guardian_name, guardian_phone,
    is_deleted, create_time, update_time
) VALUES
(1, 10, '2021001', 1, 'CS2101', 'Computer Science', '2021', 1, '2000-05-15', '110101200005151234', '13800138001', 'zhangsan@example.com', 'Beijing', '2021-09-01', '2025-06-30', 'Member', 'Han', 'Zhang Father', '13900000001', 0, NOW(), NOW()),
(2, 11, '2021002', 1, 'CS2101', 'Computer Science', '2021', 1, '2000-08-22', '110102200008221234', '13800138002', 'lisi@example.com', 'Shanghai', '2021-09-01', '2025-06-30', 'Party Member', 'Han', 'Li Father', '13900000002', 0, NOW(), NOW()),
(3, 12, '2021003', 1, 'SE2101', 'Software Engineering', '2021', 2, '2001-03-10', '110103200103101234', '13800138003', 'wangwu@example.com', 'Guangzhou', '2021-09-01', '2025-06-30', 'Member', 'Han', 'Wang Father', '13900000003', 0, NOW(), NOW()),
(4, 13, '2021004', 1, 'SE2101', 'Software Engineering', '2021', 1, '2000-11-28', '110104200011281234', '13800138004', 'zhaoliu@example.com', 'Shenzhen', '2021-09-01', '2025-06-30', 'Mass', 'Han', 'Zhao Father', '13900000004', 0, NOW(), NOW()),
(5, 14, '2021005', 1, 'CS2102', 'Computer Science', '2021', 2, '2001-06-05', '110105200106051234', '13800138005', 'sunqi@example.com', 'Hangzhou', '2021-09-01', '2025-06-30', 'Member', 'Han', 'Sun Father', '13900000005', 0, NOW(), NOW()),
(6, 15, '2021006', 1, 'CS2102', 'Computer Science', '2021', 1, '2000-09-18', '110106200009181234', '13800138006', 'zhouba@example.com', 'Chengdu', '2021-09-01', '2025-06-30', 'Member', 'Han', 'Zhou Father', '13900000006', 0, NOW(), NOW()),
(7, 16, '2021007', 1, 'SE2102', 'Software Engineering', '2021', 2, '2001-01-30', '110107200101301234', '13800138007', 'wujiu@example.com', 'Nanjing', '2021-09-01', '2025-06-30', 'Mass', 'Han', 'Wu Father', '13900000007', 0, NOW(), NOW()),
(8, 17, '2021008', 1, 'SE2102', 'Software Engineering', '2021', 1, '2000-12-12', '110108200012121234', '13800138008', 'zhengshi@example.com', 'Wuhan', '2021-09-01', '2025-06-30', 'Member', 'Han', 'Zheng Father', '13900000008', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  phone = VALUES(phone),
  email = VALUES(email),
  update_time = NOW();

-- ==========================================
-- 2. 插入简历测试数据
-- ==========================================
INSERT INTO t_resume (
    id, student_id, name, gender, phone, email,
    birth_date, education, school, major, graduation_date,
    project_experience, internship_experience, skills, self_evaluation,
    job_intention, expect_salary_min, expect_salary_max, expect_city, status,
    is_deleted, create_time, update_time
) VALUES
(1, 1, 'Zhang San Resume', 1, '13800138001', 'zhangsan@example.com', '2000-05-15',
'bachelor', 'Beijing Univ', 'CS', '2025-06-30',
'Student Management System - Full Stack Developer',
'Internship at Tencent - Backend Developer',
'Java Spring Boot MyBatis MySQL Redis Vue JS',
'Team player quick learner eager to contribute',
'Java Backend', 15000, 20000, 'Beijing', 'active', 0, NOW(), NOW()),

(2, 2, 'Li Si Resume', 1, '13800138002', 'lisi@example.com', '2000-08-22',
'bachelor', 'Shanghai Jiao Tong', 'CS', '2025-06-30',
'Admin System - Vue3 Element Plus',
'Internship at Alibaba - Frontend Developer',
'Vue React TypeScript JavaScript HTML CSS Webpack Vite',
'Responsible quality focused on user experience',
'Frontend Dev', 12000, 18000, 'Shanghai', 'active', 0, NOW(), NOW()),

(3, 3, 'Wang Wu Resume', 2, '13800138003', 'wangwu@example.com', '2001-03-10',
'master', 'Zhejiang Univ', 'SE', '2025-06-30',
'NLP Chatbot - BERT based sentiment analysis',
'Research Assistant at AI Lab',
'Python TensorFlow PyTorch Machine Learning Deep Learning NLP Scikit-learn',
'Research oriented innovative mind for AI',
'Algorithm Engineer', 20000, 30000, 'Shenzhen', 'active', 0, NOW(), NOW()),

(4, 4, 'Zhao Liu Resume', 1, '13800138004', 'zhaoliu@example.com', '2000-11-28',
'bachelor', 'HUST', 'SE', '2025-06-30',
'E-commerce Microservices Platform',
'Internship at ByteDance - Backend Developer',
'Java Go Spring Cloud Docker Kubernetes Microservices DevOps',
'Tech enthusiast problem solver',
'Backend Dev', 18000, 25000, 'Hangzhou', 'active', 0, NOW(), NOW()),

(5, 5, 'Sun Qi Resume', 2, '13800138005', 'sunqi@example.com', '2001-06-05',
'bachelor', 'Nanjing Univ', 'CS', '2025-06-30',
'Personal Blog System - Full Stack',
'Freelance Developer',
'Vue React Node.js Express MongoDB TypeScript',
'Fullstack learner curious about new technologies',
'Fullstack Dev', 15000, 22000, 'Nanjing', 'active', 0, NOW(), NOW()),

(6, 6, 'Zhou Ba Resume', 1, '13800138006', 'zhouba@example.com', '2000-09-18',
'bachelor', 'Sichuan Univ', 'SE', '2025-06-30',
'User Behavior Analysis Platform',
'Data Analyst Intern',
'Python Pandas NumPy Matplotlib MySQL Data Analysis Machine Learning',
'Data driven analytical thinker',
'Data Analyst', 12000, 18000, 'Chengdu', 'active', 0, NOW(), NOW()),

(7, 7, 'Wu Jiu Resume', 2, '13800138007', 'wujiu@example.com', '2001-01-30',
'bachelor', 'Wuhan Univ', 'CS', '2025-06-30',
'Automated Testing Framework',
'QA Intern at Startup',
'Python Selenium JUnit TestNG Software Testing Automated Testing',
'Detail oriented quality focused',
'QA Engineer', 10000, 15000, 'Wuhan', 'active', 0, NOW(), NOW()),

(8, 8, 'Zheng Shi Resume', 1, '13800138008', 'zhengshi@example.com', '2000-12-12',
'master', 'Xidian Univ', 'Network Engineering', '2025-06-30',
'Microservice Monitoring System',
'DevOps Intern',
'Linux Docker Kubernetes Jenkins Shell Monitoring DevOps Automation',
'Stability focused troubleshooter',
'DevOps Engineer', 13000, 20000, 'Xian', 'active', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  update_time = NOW();

-- ==========================================
-- 3. 插入求职申请测试数据
-- ==========================================
INSERT INTO t_job_application (
    position_id, student_id, resume_id, status, current_stage,
    application_time, hr_remark, is_deleted, create_time, update_time
) VALUES
-- 张三申请Java开发工程师 (待审核)
(13, 1, 1, 'pending', 'resume', '2024-01-18 10:30:00', 'Resume complete waiting tech interview', 0, '2024-01-18 10:30:00', '2024-01-18 10:30:00'),

-- 李四申请前端开发工程师 (面试通过)
(14, 2, 2, 'interview_passed', 'interview', '2024-01-17 14:20:00', 'Written test passed good interview', 0, '2024-01-17 14:20:00', '2024-01-19 09:15:00'),

-- 王五申请Python算法工程师 (已发offer)
(15, 3, 3, 'offered', 'offer', '2024-01-15 11:00:00', 'Interview passed offer sent', 0, '2024-01-15 11:00:00', '2024-01-20 16:30:00'),

-- 赵六申请Java开发工程师 (已拒绝)
(13, 4, 4, 'rejected', 'resume', '2024-01-16 09:45:00', 'Tech stack mismatch', 0, '2024-01-16 09:45:00', '2024-01-17 10:20:00'),

-- 孙七申请前端开发工程师 (待审核)
(14, 5, 5, 'pending', 'resume', '2024-01-18 15:10:00', 'Resume submitted waiting review', 0, '2024-01-18 15:10:00', '2024-01-18 15:10:00'),

-- 周八申请Python算法工程师 (笔试失败)
(15, 6, 6, 'test_failed', 'test', '2024-01-14 13:30:00', 'Weak algorithm basics written test failed', 0, '2024-01-14 13:30:00', '2024-01-15 14:00:00'),

-- 吴九申请软件测试实习生 (面试通过)
(16, 7, 7, 'interview_passed', 'interview', '2024-01-12 10:00:00', 'Written test passed waiting HR interview', 0, '2024-01-12 10:00:00', '2024-01-16 11:30:00'),

-- 郑十申请DevOps运维工程师 (待审核)
(18, 8, 8, 'pending', 'resume', '2024-01-18 16:45:00', 'Resume under review', 0, '2024-01-18 16:45:00', '2024-01-18 16:45:00'),

-- 张三申请前端开发工程师 (已筛选)
(14, 1, 1, 'screened', 'test', '2024-01-17 11:20:00', 'Resume screened waiting for written test', 0, '2024-01-17 11:20:00', '2024-01-18 09:00:00'),

-- 李四申请Python算法工程师 (面试失败)
(15, 2, 2, 'interview_failed', 'interview', '2024-01-13 14:50:00', 'Poor interview performance', 0, '2024-01-13 14:50:00', '2024-01-14 15:20:00'),

-- 王五申请数据分析师 (已发offer)
(19, 3, 3, 'offered', 'offer', '2024-01-10 10:30:00', 'Excellent data skills offer sent', 0, '2024-01-10 10:30:00', '2024-01-17 11:00:00'),

-- 赵六申请全栈开发工程师 (待审核)
(21, 4, 4, 'pending', 'resume', '2024-01-18 08:30:00', 'Pending technical evaluation', 0, '2024-01-18 08:30:00', '2024-01-18 08:30:00'),

-- 孙七申请UI/UX设计师 (面试失败)
(20, 5, 5, 'interview_failed', 'interview', '2024-01-11 15:40:00', 'Design portfolio not suitable', 0, '2024-01-11 15:40:00', '2024-01-12 10:15:00'),

-- 周八申请前端开发工程师 (笔试通过)
(14, 6, 6, 'test_passed', 'interview', '2024-01-16 13:20:00', 'Excellent written test score waiting interview', 0, '2024-01-16 13:20:00', '2024-01-17 14:30:00'),

-- 吴九申请Java开发工程师 (已入职)
(13, 7, 7, 'hired', 'hired', '2024-01-05 09:00:00', 'Onboarded', 0, '2024-01-05 09:00:00', '2024-01-15 09:00:00')
ON DUPLICATE KEY UPDATE
  update_time = NOW();

-- ==========================================
-- 查询验证数据
-- ==========================================
SELECT
    ja.id,
    u.real_name AS studentName,
    s.phone AS studentPhone,
    p.position_name AS positionName,
    c.company_name AS companyName,
    ja.status,
    DATE_FORMAT(ja.application_time, '%Y-%m-%d') AS applyTime
FROM t_job_application ja
LEFT JOIN t_student s ON ja.student_id = s.id
LEFT JOIN t_user u ON s.user_id = u.id
LEFT JOIN t_recruitment_position p ON ja.position_id = p.id
LEFT JOIN t_company c ON p.company_id = c.id
WHERE ja.is_deleted = 0
ORDER BY ja.application_time DESC;
