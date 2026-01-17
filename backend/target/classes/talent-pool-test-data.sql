-- =============================================
-- 人才库测试数据
-- 为企业对接人功能创建测试用的人才库记录
-- =============================================

USE teaching_employment_platform;

-- 清空现有数据（先禁用外键检查）
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM t_talent_pool WHERE id > 0;
SET FOREIGN_KEY_CHECKS = 1;

-- 插入人才库测试数据
INSERT INTO t_talent_pool (
    company_id,
    student_id,
    source,
    position_name,
    tags,
    category,
    skill_tags,
    experience_level,
    education,
    expected_salary_min,
    expected_salary_max,
    expected_city,
    rating,
    priority,
    status,
    last_contact_date,
    last_contact_method,
    next_contact_date,
    remark,
    contact_count,
    create_time,
    update_time
) VALUES
-- 企业1的人才库
(
    1, -- 腾讯科技
    1, -- 学员1
    'application',
    'Java后端工程师',
    'Java,Spring Boot,MySQL,应届生',
    'backend',
    'Java, Spring Boot, MySQL, Redis, MyBatis',
    'entry',
    '本科',
    12000.00,
    18000.00,
    '北京,深圳',
    5,
    'high',
    'active',
    '2026-01-15',
    'email',
    '2026-01-25',
    '技术基础扎实，有项目经验，优先考虑',
    2,
    '2026-01-10 10:00:00',
    '2026-01-17 10:00:00'
),
(
    1,
    3,
    'application',
    '前端开发工程师',
    'Vue,React,JavaScript,有经验',
    'frontend',
    'Vue.js, React, TypeScript, Webpack',
    'intermediate',
    '本科',
    15000.00,
    22000.00,
    '北京,上海',
    4,
    'normal',
    'contacted',
    '2026-01-12',
    'phone',
    '2026-01-20',
    '已安排面试，等待反馈',
    3,
    '2026-01-08 14:00:00',
    '2026-01-15 14:00:00'
),
(
    1,
    5,
    'application',
    'Python算法工程师',
    'Python,Machine Learning, TensorFlow',
    'backend',
    'Python, TensorFlow, PyTorch, NLP',
    'entry',
    '硕士',
    18000.00,
    25000.00,
    '北京',
    5,
    'urgent',
    'active',
    NULL,
    NULL,
    '2026-01-18',
    '重点人才，算法能力强',
    0,
    '2026-01-12 09:00:00',
    '2026-01-12 09:00:00'
),
(
    1,
    7,
    'manual',
    '全栈工程师',
    'Node.js,React,Java,三年经验',
    'frontend',
    'Node.js, React, Java, MySQL, Docker',
    'senior',
    '本科',
    20000.00,
    30000.00,
    '深圳,杭州',
    4,
    'high',
    'active',
    '2026-01-14',
    'message',
    '2026-01-22',
    '综合能力强，沟通能力好',
    1,
    '2026-01-11 16:00:00',
    '2026-01-16 16:00:00'
),
(
    1,
    2,
    'application',
    '移动端开发工程师',
    'iOS,Swift,React Native',
    'mobile',
    'iOS, Swift, React Native, Objective-C',
    'intermediate',
    '本科',
    14000.00,
    20000.00,
    '上海',
    3,
    'normal',
    'contacted',
    '2026-01-10',
    'interview',
    '2026-01-19',
    '面试通过，等待Offer审批',
    4,
    '2026-01-05 11:00:00',
    '2026-01-14 11:00:00'
),

-- 企业2的人才库
(
    2, -- 字节跳动
    2,
    'application',
    'Java后端工程师',
    'Java,微服务,Docker,Kubernetes',
    'backend',
    'Java, Spring Cloud, Docker, Kubernetes, MySQL',
    'intermediate',
    '本科',
    15000.00,
    22000.00,
    '北京',
    4,
    'high',
    'active',
    '2026-01-16',
    'email',
    '2026-01-24',
    '有微服务项目经验',
    1,
    '2026-01-13 10:00:00',
    '2026-01-16 10:00:00'
),
(
    2,
    4,
    'application',
    '前端架构师',
    'Vue,React,架构设计,团队管理',
    'frontend',
    'Vue.js, React, TypeScript, Node.js, 架构设计',
    'senior',
    '本科',
    25000.00,
    35000.00,
    '北京,杭州',
    5,
    'urgent',
    'active',
    NULL,
    NULL,
    '2026-01-17',
    '有架构经验，可带团队',
    0,
    '2026-01-14 15:00:00',
    '2026-01-14 15:00:00'
),
(
    2,
    6,
    'manual',
    '数据分析师',
    'Python,SQL,Tableau,数据分析',
    'other',
    'Python, SQL, Tableau, Power BI, Excel',
    'entry',
    '本科',
    10000.00,
    15000.00,
    '上海',
    3,
    'normal',
    'active',
    '2026-01-13',
    'email',
    '2026-01-21',
    '数据分析思维清晰',
    1,
    '2026-01-09 14:00:00',
    '2026-01-13 14:00:00'
),
(
    2,
    8,
    'application',
    'UI设计师',
    'UI/UX,Figma,Sketch,移动端设计',
    'design',
    'Figma, Sketch, Adobe XD, Photoshop, Illustrator',
    'intermediate',
    '本科',
    12000.00,
    18000.00,
    '北京,深圳',
    4,
    'normal',
    'contacted',
    '2026-01-11',
    'phone',
    '2026-01-20',
    '作品集优秀',
    2,
    '2026-01-07 10:00:00',
    '2026-01-11 10:00:00'
),

-- 企业3的人才库
(
    3, -- 拼多多
    1,
    'application',
    'Go后端工程师',
    'Go,微服务,高并发',
    'backend',
    'Go, Python, Redis, Kafka, Elasticsearch',
    'senior',
    '本科',
    20000.00,
    28000.00,
    '上海',
    5,
    'urgent',
    'active',
    NULL,
    NULL,
    '2026-01-16',
    '高并发处理经验',
    0,
    '2026-01-15 11:00:00',
    '2026-01-15 11:00:00'
),
(
    3,
    3,
    'manual',
    '产品经理',
    '产品设计,需求分析,原型设计',
    'product',
    'Axure, XMind, Visio, 产品设计, 数据分析',
    'intermediate',
    '本科',
    15000.00,
    22000.00,
    '上海',
    4,
    'high',
    'active',
    '2026-01-14',
    'message',
    '2026-01-23',
    '有B端产品经验',
    1,
    '2026-01-10 09:00:00',
    '2026-01-14 09:00:00'
),
(
    3,
    5,
    'application',
    '测试工程师',
    '自动化测试,Selenium,Python',
    'other',
    'Python, Selenium, JMeter, 接口测试, 性能测试',
    'entry',
    '本科',
    10000.00,
    15000.00,
    '上海,杭州',
    3,
    'normal',
    'locked',
    '2026-01-08',
    'email',
    NULL,
    '已接受其他Offer',
    2,
    '2026-01-05 16:00:00',
    '2026-01-08 16:00:00'
),
(
    3,
    7,
    'application',
    '运维工程师',
    'Linux,Shell,Docker,CI/CD',
    'backend',
    'Linux, Shell, Docker, Kubernetes, Jenkins, Ansible',
    'intermediate',
    '本科',
    15000.00,
    20000.00,
    '上海',
    4,
    'normal',
    'contacted',
    '2026-01-12',
    'interview',
    '2026-01-21',
    '技术栈匹配',
    3,
    '2026-01-09 10:00:00',
    '2026-01-12 10:00:00'
);

-- 查看插入结果
SELECT '=== 人才库数据统计 ===' as '';
SELECT
    category as '分类',
    COUNT(*) as '数量',
    CASE category
        WHEN 'frontend' THEN '前端'
        WHEN 'backend' THEN '后端'
        WHEN 'mobile' THEN '移动端'
        WHEN 'product' THEN '产品'
        WHEN 'design' THEN '设计'
        WHEN 'operation' THEN '运营'
        WHEN 'other' THEN '其他'
    END as '分类说明'
FROM t_talent_pool
GROUP BY category
ORDER BY
    FIELD(category, 'frontend', 'backend', 'mobile', 'product', 'design', 'operation', 'other');

-- 查看优先级分布
SELECT '=== 优先级分布 ===' as '';
SELECT
    priority as '优先级',
    COUNT(*) as '数量',
    CASE priority
        WHEN 'urgent' THEN '紧急'
        WHEN 'high' THEN '高'
        WHEN 'normal' THEN '中'
        WHEN 'low' THEN '低'
    END as '优先级说明'
FROM t_talent_pool
GROUP BY priority
ORDER BY
    FIELD(priority, 'urgent', 'high', 'normal', 'low');

-- 查看状态分布
SELECT '=== 状态分布 ===' as '';
SELECT
    status as '状态',
    COUNT(*) as '数量',
    CASE status
        WHEN 'active' THEN '激活'
        WHEN 'contacted' THEN '已联系'
        WHEN 'locked' THEN '已锁定'
        WHEN 'inactive' THEN '未激活'
    END as '状态说明'
FROM t_talent_pool
GROUP BY status
ORDER BY
    FIELD(status, 'active', 'contacted', 'locked', 'inactive');

-- 查看详细人才列表（关联学员信息）
SELECT '=== 人才库详细列表（企业1） ===' as '';
SELECT
    tp.id,
    u.username as '学员姓名',
    s.major as '专业',
    tp.position_name as '意向职位',
    tp.category as '分类',
    tp.tags as '标签',
    tp.rating as '评分',
    tp.priority as '优先级',
    tp.status as '状态',
    tp.contact_count as '联系次数',
    DATE_FORMAT(tp.create_time, '%Y-%m-%d') as '添加日期'
FROM t_talent_pool tp
LEFT JOIN t_student s ON tp.student_id = s.id
LEFT JOIN t_user u ON s.user_id = u.id
WHERE tp.company_id = 1
ORDER BY tp.create_time DESC;

-- 说明：
-- 1. 创建了13条人才库测试数据
-- 2. 企业1（腾讯）有5个人才
-- 3. 企业2（字节跳动）有4个人才
-- 4. 企业3（拼多多）有4个人才
-- 5. 包含不同分类：前端、后端、移动端、产品、设计、其他
-- 6. 包含不同优先级：紧急、高、中、低
-- 7. 包含不同状态：激活、已联系、已锁定
-- 8. 包含不同的评分（1-5星）和联系次数
-- 9. 可用于测试人才库的筛选、排序、评分、联系等功能
