-- =============================================
-- Offer测试数据（修正版）
-- 基于实际数据库中的有效ID创建
-- =============================================

USE teaching_employment_platform;

-- 插入测试Offer数据（使用有效的ID）
INSERT INTO t_offer (
    application_id,
    offer_no,
    position_id,
    student_id,
    company_id,
    position_name,
    city,
    salary,
    salary_unit,
    onboard_date,
    accept_deadline,
    accept_time,
    email_status,
    email_send_time,
    attachment_url,
    remark,
    status,
    create_time,
    update_time
) VALUES
-- Offer 1: 学员2的待接受Offer（职位13：Java后端工程师，企业1）
(
    59, -- 申请ID
    'OFF20260117001',
    13, -- 职位ID
    2, -- 学员ID（张三）
    1, -- 企业ID
    'Java后端工程师',
    '北京',
    15000.00,
    'month',
    '2026-03-01',
    '2026-02-01',
    NULL,
    'sent',
    NOW(),
    '/uploads/offer/offer_001.pdf',
    '期待您的加入！我们提供有竞争力的薪资和完善的福利。',
    'pending',
    '2026-01-17 10:00:00',
    '2026-01-17 10:00:00'
),

-- Offer 2: 学员2的已接受Offer（职位15：Python算法工程师，企业2）
(
    60,
    'OFF20260117002',
    15,
    2,
    2,
    'Python算法工程师',
    '深圳',
    20000.00,
    'month',
    '2026-03-15',
    '2026-01-25',
    '2026-01-18 14:30:00',
    'sent',
    '2026-01-18 14:35:00',
    '/uploads/offer/offer_002.pdf',
    '恭喜您通过面试！期待与您共事。',
    'accepted',
    '2026-01-16 09:00:00',
    '2026-01-18 14:30:00'
),

-- Offer 3: 学员3的已拒绝Offer（职位14：前端开发工程师，企业1）
(
    61,
    'OFF20260117003',
    14,
    4,
    1,
    '前端开发工程师',
    '杭州',
    18000.00,
    'month',
    '2026-03-10',
    '2026-01-20',
    NULL,
    'sent',
    '2026-01-17 11:00:00',
    '/uploads/offer/offer_003.pdf',
    '感谢您的认可，但已有其他安排。',
    'rejected',
    '2026-01-17 15:00:00',
    '2026-01-19 09:00:00'
),

-- Offer 4: 学员5的待接受Offer（职位16：测试实习生，企业1）
(
    64,
    'OFF20260117004',
    16,
    5,
    1,
    '测试实习生',
    '北京',
    8000.00,
    'month',
    '2026-02-20',
    '2026-01-30',
    NULL,
    'sent',
    '2026-01-17 12:00:00',
    '/uploads/offer/offer_004.pdf',
    '实习岗位，优秀者有转正机会。',
    'pending',
    '2026-01-17 12:00:00',
    '2026-01-17 12:00:00'
),

-- Offer 5: 学员6的已接受Offer（职位18：DevOps工程师，企业1）
(
    65,
    'OFF20260117005',
    18,
    6,
    1,
    'DevOps工程师',
    '上海',
    22000.00,
    'month',
    '2026-03-12',
    '2026-01-28',
    '2026-01-19 16:00:00',
    'sent',
    '2026-01-19 16:05:00',
    '/uploads/offer/offer_005.pdf',
    '核心基础设施团队，技术挑战性强。',
    'accepted',
    '2026-01-18 10:00:00',
    '2026-01-19 16:00:00'
),

-- Offer 6: 学员7的待接受Offer（职位19：数据分析师，企业3）
(
    66,
    'OFF20260117006',
    19,
    7,
    3,
    '数据分析师',
    '北京',
    17000.00,
    'month',
    '2026-03-08',
    '2026-01-29',
    NULL,
    'sent',
    '2026-01-17 18:00:00',
    '/uploads/offer/offer_006.pdf',
    '大数据平台核心团队，发展前景广阔。',
    'pending',
    '2026-01-17 18:00:00',
    '2026-01-17 18:00:00'
),

-- Offer 7: 学员8的待接受Offer（职位20：UI设计师，企业2）
(
    67,
    'OFF20260117007',
    20,
    8,
    2,
    'UI设计师',
    '深圳',
    16000.00,
    'month',
    '2026-03-05',
    '2026-01-31',
    NULL,
    'sent',
    '2026-01-17 19:00:00',
    '/uploads/offer/offer_007.pdf',
    '用户体验团队，重视设计创新。',
    'pending',
    '2026-01-17 19:00:00',
    '2026-01-17 19:00:00'
),

-- Offer 8: 学员2的另一个Offer（职位21：全栈工程师，企业3）
(
    58,
    'OFF20260117008',
    21,
    2,
    3,
    '全栈工程师',
    '北京',
    25000.00,
    'month',
    '2026-03-20',
    '2026-01-25',
    NULL,
    'sent',
    '2026-01-17 20:00:00',
    '/uploads/offer/offer_008.pdf',
    '前后端全栈开发，能力要求较高。',
    'pending',
    '2026-01-17 20:00:00',
    '2026-01-17 20:00:00'
);

-- 查看插入结果
SELECT '=== Offer数据统计 ===' as '';
SELECT
    status as '状态',
    COUNT(*) as '数量',
    CASE status
        WHEN 'pending' THEN '待接受'
        WHEN 'accepted' THEN '已接受'
        WHEN 'rejected' THEN '已拒绝'
        WHEN 'cancelled' THEN '已取消'
        WHEN 'expired' THEN '已过期'
    END as '状态说明'
FROM t_offer
GROUP BY status
ORDER BY
    FIELD(status, 'pending', 'accepted', 'rejected', 'expired', 'cancelled');

-- 查看详细的Offer列表
SELECT '=== Offer详细列表 ===' as '';
SELECT
    o.offer_no as 'Offer编号',
    c.company_name as '企业',
    o.position_name as '职位',
    CONCAT('¥', o.salary, '/', o.salary_unit) as '薪资',
    o.city as '城市',
    o.onboard_date as '入职日期',
    o.status as '状态',
    DATE_FORMAT(o.create_time, '%Y-%m-%d %H:%i') as '发送时间'
FROM t_offer o
LEFT JOIN t_company c ON o.company_id = c.id
ORDER BY o.create_time DESC;

-- 说明：
-- 1. 创建了8条Offer测试数据
-- 2. 状态分布：5个待接受、2个已接受、1个已拒绝
-- 3. 学员2（张三）有3个Offer：1个已接受、2个待接受
-- 4. 可用于测试学员端"我的Offer"和企业端"Offer管理"页面
-- 5. 所有Offer都关联了有效的申请ID、职位ID、学员ID和企业ID
