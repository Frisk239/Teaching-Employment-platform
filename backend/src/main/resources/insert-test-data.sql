-- 清理并重新插入测试数据
USE teaching_employment_platform;

-- 删除现有测试数据(按外键依赖顺序)
DELETE FROM t_notification WHERE type = 'notice';
DELETE FROM t_offer WHERE student_id <= 10;
DELETE FROM t_job_application WHERE student_id <= 10;
DELETE FROM t_course_student WHERE student_id <= 10;
DELETE FROM t_course WHERE id <= 10;
DELETE FROM t_position WHERE company_id <= 10;
DELETE FROM t_company WHERE id <= 10;
DELETE FROM t_student WHERE user_id <= 10;
DELETE FROM t_user WHERE id <= 10;

-- 1. 插入用户测试数据 (学生角色,密码都是123456的BCrypt加密)
INSERT INTO t_user (id, username, password, real_name, phone, email, role_id, status, create_time, update_time) VALUES
(1, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张三', '13800138001', 'zhangsan@example.com', 4, 1, NOW(), NOW()),
(2, 'student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李四', '13800138002', 'lisi@example.com', 4, 1, NOW(), NOW()),
(3, 'student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王五', '13800138003', 'wangwu@example.com', 4, 1, NOW(), NOW()),
(4, 'student4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '赵六', '13800138004', 'zhaoliu@example.com', 4, 1, NOW(), NOW()),
(5, 'student5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '钱七', '13800138005', 'qianqi@example.com', 4, 1, NOW(), NOW()),
(6, 'student6', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '孙八', '13800138006', 'sunba@example.com', 4, 1, NOW(), NOW()),
(7, 'student7', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '周九', '13800138007', 'zhoujiu@example.com', 4, 1, NOW(), NOW()),
(8, 'student8', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '吴十', '13800138008', 'wushi@example.com', 4, 1, NOW(), NOW()),
(9, 'student9', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '郑一', '13800138009', 'zhengshiyi@example.com', 4, 1, NOW(), NOW()),
(10, 'student10', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王二', '13800138010', 'wangshier@example.com', 4, 1, NOW(), NOW());

-- 2. 插入学生测试数据
INSERT INTO t_student (id, user_id, student_no, gender, phone, email, grade, major, class_name, enrollment_date, create_time, update_time) VALUES
(1, 1, '2021001', 1, '13800138001', 'zhangsan@example.com', '2021', '计算机科学与技术', '计科2101', '2021-09-01', NOW(), NOW()),
(2, 2, '2021002', 2, '13800138002', 'lisi@example.com', '2021', '软件工程', '软工2101', '2021-09-01', NOW(), NOW()),
(3, 3, '2021003', 1, '13800138003', 'wangwu@example.com', '2021', '计算机科学与技术', '计科2101', '2021-09-01', NOW(), NOW()),
(4, 4, '2021004', 2, '13800138004', 'zhaoliu@example.com', '2021', '软件工程', '软工2101', '2021-09-01', NOW(), NOW()),
(5, 5, '2021005', 1, '13800138005', 'qianqi@example.com', '2021', '人工智能', '人工智能2101', '2021-09-01', NOW(), NOW()),
(6, 6, '2021006', 2, '13800138006', 'sunba@example.com', '2021', '计算机科学与技术', '计科2102', '2021-09-01', NOW(), NOW()),
(7, 7, '2021007', 1, '13800138007', 'zhoujiu@example.com', '2021', '软件工程', '软工2102', '2021-09-01', NOW(), NOW()),
(8, 8, '2021008', 2, '13800138008', 'wushi@example.com', '2021', '人工智能', '人工智能2101', '2021-09-01', NOW(), NOW()),
(9, 9, '2021009', 1, '13800138009', 'zhengshiyi@example.com', '2021', '数据科学', '数据科学2101', '2021-09-01', NOW(), NOW()),
(10, 10, '2021010', 2, '13800138010', 'wangshier@example.com', '2021', '软件工程', '软工2101', '2021-09-01', NOW(), NOW());

-- 3. 插入企业测试数据
INSERT INTO t_company (id, company_name, industry, scale, location, website, description, contact_person, contact_phone, contact_email, status, create_time, update_time) VALUES
(1, '阿里巴巴集团', '互联网', '大型企业', '杭州', 'https://www.alibaba.com', '全球领先的互联网公司', '张经理', '0571-12345678', 'hr@alibaba.com', 'active', NOW(), NOW()),
(2, '腾讯科技', '互联网', '大型企业', '深圳', 'https://www.tencent.com', '互联网增值服务提供商', '李经理', '0755-12345678', 'hr@tencent.com', 'active', NOW(), NOW()),
(3, '字节跳动', '互联网', '大型企业', '北京', 'https://www.bytedance.com', '技术驱动的移动互联网公司', '王经理', '010-12345678', 'hr@bytedance.com', 'active', NOW(), NOW()),
(4, '华为技术', '科技', '大型企业', '深圳', 'https://www.huawei.com', '全球领先的ICT解决方案提供商', '赵经理', '0755-87654321', 'hr@huawei.com', 'active', NOW(), NOW()),
(5, '美团', '互联网', '大型企业', '北京', 'https://www.meituan.com', '吃喝玩乐全都有', '钱经理', '010-87654321', 'hr@meituan.com', 'active', NOW(), NOW()),
(6, '京东集团', '电商', '大型企业', '北京', 'https://www.jd.com', '中国最大的电商平台之一', '孙经理', '010-11111111', 'hr@jd.com', 'active', NOW(), NOW()),
(7, '网易', '互联网', '大型企业', '广州', 'https://www.163.com', '中国领先的互联网技术公司', '周经理', '020-11111111', 'hr@netease.com', 'active', NOW(), NOW()),
(8, '百度', '互联网', '大型企业', '北京', 'https://www.baidu.com', '全球最大的中文搜索引擎', '吴经理', '010-22222222', 'hr@baidu.com', 'active', NOW(), NOW()),
(9, '小米科技', '科技', '大型企业', '北京', 'https://www.mi.com', '让每个人都能享受科技的乐趣', '郑经理', '010-22222222', 'hr@xiaomi.com', 'active', NOW(), NOW()),
(10, '滴滴出行', '互联网', '大型企业', '北京', 'https://www.didiglobal.com', '一站式移动出行平台', '王经理', '010-33333333', 'hr@didichuxing.com', 'active', NOW(), NOW());

-- 4. 插入职位测试数据
INSERT INTO t_position (id, company_id, position_name, position_type, salary_min, salary_max, location, requirements, description, recruitment_count, status, create_time, update_time) VALUES
(1, 1, 'Java后端开发工程师', 'fulltime', 15000, 25000, '杭州', '1. 熟练掌握Java\n2. 熟悉Spring框架\n3. 有良好的编码习惯', '负责后端系统开发和维护', 5, 'active', NOW(), NOW()),
(2, 1, '前端开发工程师', 'fulltime', 12000, 20000, '杭州', '1. 熟练掌握Vue/React\n2. 精通HTML/CSS/JS\n3. 有大型项目经验', '负责前端页面开发和优化', 3, 'active', NOW(), NOW()),
(3, 2, 'Python开发工程师', 'fulltime', 18000, 28000, '深圳', '1. 精通Python\n2. 熟悉Django/Flask\n3. 有AI项目经验优先', '负责AI平台开发', 4, 'active', NOW(), NOW()),
(4, 2, 'Go开发工程师', 'fulltime', 20000, 30000, '深圳', '1. 熟悉Go语言\n2. 有分布式系统经验\n3. 熟悉微服务架构', '负责后端服务开发', 2, 'active', NOW(), NOW()),
(5, 3, '算法工程师', 'fulltime', 25000, 40000, '北京', '1. 计算机/数学相关专业\n2. 熟悉机器学习算法\n3. 有论文发表优先', '负责推荐算法优化', 3, 'active', NOW(), NOW()),
(6, 4, '嵌入式开发工程师', 'fulltime', 18000, 28000, '深圳', '1. 熟悉C/C++\n2. 有嵌入式开发经验\n3. 熟悉ARM架构', '负责嵌入式系统开发', 6, 'active', NOW(), NOW()),
(7, 5, '数据分析师', 'fulltime', 15000, 22000, '北京', '1. 熟悉SQL\n2. 有数据分析经验\n3. 熟练使用Python/R', '负责业务数据分析', 4, 'active', NOW(), NOW()),
(8, 6, '全栈开发工程师', 'fulltime', 16000, 24000, '北京', '1. 精通前后端开发\n2. 熟悉Node.js\n3. 有完整项目经验', '负责全栈开发工作', 5, 'active', NOW(), NOW()),
(9, 7, '游戏客户端开发', 'fulltime', 17000, 26000, '广州', '1. 熟悉Unity/Unreal\n2. 有游戏开发经验\n3. 热爱游戏', '负责游戏客户端开发', 3, 'active', NOW(), NOW()),
(10, 8, '机器学习工程师', 'fulltime', 22000, 35000, '北京', '1. 熟悉深度学习\n2. 有TensorFlow/PyTorch经验\n3. 有NLP/CV项目经验', '负责AI模型开发', 2, 'active', NOW(), NOW());

-- 5. 插入课程测试数据(需要先检查教师ID)
-- 注意: 这里假设已经有教师数据,如果没有可能需要先插入教师
INSERT IGNORE INTO t_course (id, course_code, course_name, course_type, credits, hours, description, teacher_id, status, create_time, update_time) VALUES
(1, 'CS2101', '数据结构与算法', '专业必修', 4, 64, '学习基本的数据结构和算法设计', 1, 'active', NOW(), NOW()),
(2, 'CS2102', '计算机网络', '专业必修', 3, 48, '学习计算机网络的基本原理', 2, 'active', NOW(), NOW()),
(3, 'CS2103', '操作系统', '专业必修', 4, 64, '学习操作系统的设计与实现', 3, 'active', NOW(), NOW()),
(4, 'SE2101', '软件工程', '专业必修', 3, 48, '学习软件开发的工程方法', 4, 'active', NOW(), NOW()),
(5, 'AI2101', '机器学习基础', '专业选修', 3, 48, '学习机器学习的基本算法和应用', 5, 'active', NOW(), NOW()),
(6, 'AI2102', '深度学习', '专业选修', 3, 48, '学习深度学习的原理和实践', 6, 'active', NOW(), NOW()),
(7, 'DS2101', '数据科学导论', '专业必修', 2, 32, '学习数据处理和分析的基本方法', 7, 'active', NOW(), NOW()),
(8, 'CS2104', '数据库系统', '专业必修', 3, 48, '学习数据库的设计和管理', 8, 'active', NOW(), NOW()),
(9, 'SE2102', 'Web开发技术', '专业选修', 2, 32, '学习前后端开发技术', 9, 'active', NOW(), NOW()),
(10, 'AI2103', '自然语言处理', '专业选修', 3, 48, '学习NLP的基本理论和应用', 10, 'active', NOW(), NOW());

-- 6. 插入课程学生关联数据
INSERT INTO t_course_student (course_id, student_id, enrollment_date, status, create_time, update_time) VALUES
(1, 1, '2024-02-01', 'active', NOW(), NOW()),
(1, 2, '2024-02-01', 'active', NOW(), NOW()),
(1, 3, '2024-02-01', 'active', NOW(), NOW()),
(1, 4, '2024-02-01', 'active', NOW(), NOW()),
(1, 5, '2024-02-01', 'active', NOW(), NOW()),
(2, 1, '2024-02-01', 'active', NOW(), NOW()),
(2, 2, '2024-02-01', 'active', NOW(), NOW()),
(2, 6, '2024-02-01', 'active', NOW(), NOW()),
(2, 7, '2024-02-01', 'active', NOW(), NOW()),
(3, 1, '2024-02-01', 'active', NOW(), NOW()),
(3, 3, '2024-02-01', 'active', NOW(), NOW()),
(3, 5, '2024-02-01', 'active', NOW(), NOW()),
(3, 8, '2024-02-01', 'active', NOW(), NOW()),
(3, 9, '2024-02-01', 'active', NOW(), NOW()),
(4, 2, '2024-02-01', 'active', NOW(), NOW()),
(4, 4, '2024-02-01', 'active', NOW(), NOW()),
(4, 6, '2024-02-01', 'active', NOW(), NOW()),
(4, 7, '2024-02-01', 'active', NOW(), NOW()),
(4, 10, '2024-02-01', 'active', NOW(), NOW()),
(5, 1, '2024-02-01', 'active', NOW(), NOW()),
(5, 3, '2024-02-01', 'active', NOW(), NOW()),
(5, 5, '2024-02-01', 'active', NOW(), NOW()),
(5, 8, '2024-02-01', 'active', NOW(), NOW()),
(5, 9, '2024-02-01', 'active', NOW(), NOW());

-- 7. 插入求职申请测试数据
INSERT INTO t_job_application (student_id, position_id, company_id, resume_id, status, apply_time, create_time, update_time) VALUES
(1, 1, 1, 1, 'pending', NOW(), NOW(), NOW()),
(1, 2, 1, 1, 'written_test_passed', NOW(), NOW(), NOW()),
(2, 3, 2, 2, 'interview_scheduled', NOW(), NOW(), NOW()),
(2, 4, 2, 2, 'pending', NOW(), NOW(), NOW()),
(3, 5, 3, 3, 'interview_passed', NOW(), NOW(), NOW()),
(3, 6, 4, 3, 'pending', NOW(), NOW(), NOW()),
(4, 7, 5, 4, 'pending', NOW(), NOW(), NOW()),
(4, 8, 6, 4, 'written_test_passed', NOW(), NOW(), NOW()),
(5, 9, 7, 5, 'interview_scheduled', NOW(), NOW(), NOW()),
(5, 10, 8, 5, 'pending', NOW(), NOW(), NOW());

-- 8. 插入Offer测试数据 (模拟最近6个月的就业数据)
INSERT INTO t_offer (student_id, position_id, company_id, salary, offer_date, status, create_time, update_time) VALUES
(1, 2, 1, 20000, '2025-12-15', 'accepted', '2025-12-10 10:00:00', NOW(), NOW()),
(2, 3, 2, 25000, '2025-12-20', 'accepted', '2025-12-15 14:00:00', NOW(), NOW()),
(3, 5, 3, 35000, '2025-11-25', 'accepted', '2025-11-20 09:00:00', NOW(), NOW()),
(4, 7, 5, 22000, '2025-10-18', 'accepted', '2025-10-12 16:00:00', NOW(), NOW()),
(5, 9, 7, 26000, '2025-10-22', 'accepted', '2025-10-15 11:00:00', NOW(), NOW()),
(6, 1, 1, 18000, '2025-09-20', 'accepted', '2025-09-15 10:00:00', NOW(), NOW()),
(7, 5, 3, 32000, '2025-08-25', 'accepted', '2025-08-18 14:00:00', NOW(), NOW()),
(8, 10, 8, 30000, '2025-08-28', 'accepted', '2025-08-20 09:00:00', NOW(), NOW());

-- 9. 插入公告测试数据
INSERT INTO t_notification (user_id, type, title, content, related_id, is_read, read_time, create_time, update_time) VALUES
(NULL, 'notice', '2026届春季校园招聘启动', '各位同学请注意,2026届春季校园招聘正式启动,欢迎各位同学积极投递简历!', NULL, 0, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(NULL, 'notice', '阿里巴巴集团校园宣讲会通知', '阿里巴巴集团将于下周三下午2点在大礼堂举办校园宣讲会,欢迎参加!', 1, 0, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(NULL, 'notice', '腾讯技术开放日报名开始', '腾讯技术开放日活动即将举行,现开始接受报名,名额有限,先到先得!', 2, 0, NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(NULL, 'notice', '华为优招计划宣讲会', '华为2026届优招计划宣讲会将于本周五下午3点在报告厅举行', 4, 0, NULL, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(NULL, 'warning', '关于举办编程大赛的通知', '为提高同学们的编程能力,学院将举办第十届编程大赛,欢迎报名参加!', NULL, 0, NULL, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(NULL, 'success', '就业指导讲座安排', '就业指导中心将于下周举办系列就业指导讲座,包括简历制作、面试技巧等内容', NULL, 0, NULL, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
(NULL, 'info', '暑期实习招聘信息汇总', '暑期实习招聘信息汇总已发布,请同学们及时查看!', NULL, 0, NULL, DATE_SUB(NOW(), INTERVAL 12 DAY), NOW()),
(NULL, 'info', '研究生招生宣讲会', '多所知名高校将举办研究生招生宣讲会,有意向的同学请关注', NULL, 0, NULL, DATE_SUB(NOW(), INTERVAL 15 DAY), NOW());

-- 提示信息
SELECT '测试数据插入完成!' AS message;
SELECT '已插入数据统计:' AS summary;
SELECT CONCAT('学生: ', COUNT(*), ' 条') AS info FROM t_student WHERE user_id <= 10;
SELECT CONCAT('企业: ', COUNT(*), ' 条') AS info FROM t_company WHERE id <= 10;
SELECT CONCAT('职位: ', COUNT(*), ' 条') AS info FROM t_position WHERE id <= 10;
SELECT CONCAT('课程: ', COUNT(*), ' 条') AS info FROM t_course WHERE id <= 10;
SELECT CONCAT('课程学生关联: ', COUNT(*), ' 条') AS info FROM t_course_student WHERE student_id <= 10;
SELECT CONCAT('求职申请: ', COUNT(*), ' 条') AS info FROM t_job_application WHERE student_id <= 10;
SELECT CONCAT('Offer: ', COUNT(*), ' 条') AS info FROM t_offer WHERE student_id <= 10;
SELECT CONCAT('公告: ', COUNT(*), ' 条') AS info FROM t_notification WHERE type = 'notice';
