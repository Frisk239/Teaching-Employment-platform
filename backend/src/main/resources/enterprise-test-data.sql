-- =============================================
-- 企业负责人测试数据脚本
-- 用于企业负责人功能测试
-- =============================================

-- 说明：
-- 1. 企业负责人角色ID为5 (enterprise_contact)
-- 2. 密码都是123456的BCrypt加密
-- 3. 每个企业配置1-2名企业负责人
-- 4. 用户名格式: enterprise+公司ID+编号 (如 enterprise11, enterprise12)

-- =============================================
-- 1. 插入企业负责人用户数据
-- =============================================
INSERT INTO t_user (username, password, real_name, phone, email, role_id, status, create_time, update_time) VALUES
-- 阿里巴巴集团 (company_id=1)
('enterprise11', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张伟', '13900001001', 'zhangwei@alibaba.com', 5, 1, NOW(), NOW()),
('enterprise12', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李娜', '13900001002', 'lina@alibaba.com', 5, 1, NOW(), NOW()),

-- 腾讯科技 (company_id=2)
('enterprise21', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王强', '13900002001', 'wangqiang@tencent.com', 5, 1, NOW(), NOW()),
('enterprise22', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '刘洋', '13900002002', 'liuyang@tencent.com', 5, 1, NOW(), NOW()),

-- 字节跳动 (company_id=3)
('enterprise31', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '陈明', '13900003001', 'chenming@bytedance.com', 5, 1, NOW(), NOW()),

-- 华为技术 (company_id=4)
('enterprise41', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '杨秀兰', '13900004001', 'yangxiulan@huawei.com', 5, 1, NOW(), NOW()),
('enterprise42', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '赵军', '13900004002', 'zhaojun@huawei.com', 5, 1, NOW(), NOW()),

-- 美团 (company_id=5)
('enterprise51', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '孙丽', '13900005001', 'sunli@meituan.com', 5, 1, NOW(), NOW()),

-- 京东集团 (company_id=6)
('enterprise61', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '周涛', '13900006001', 'zhoutao@jd.com', 5, 1, NOW(), NOW()),
('enterprise62', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '吴杰', '13900006002', 'wujie@jd.com', 5, 1, NOW(), NOW()),

-- 网易 (company_id=7)
('enterprise71', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '郑敏', '13900007001', 'zhengmin@netease.com', 5, 1, NOW(), NOW()),

-- 百度 (company_id=8)
('enterprise81', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '王磊', '13900008001', 'wanglei@baidu.com', 5, 1, NOW(), NOW()),
('enterprise82', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '冯静', '13900008002', 'fengjing@baidu.com', 5, 1, NOW(), NOW()),

-- 小米科技 (company_id=9)
('enterprise91', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '陈超', '13900009001', 'chenchao@xiaomi.com', 5, 1, NOW(), NOW()),

-- 滴滴出行 (company_id=10)
('enterprise101', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '褚欣', '13900010001', 'chuxin@didichuxing.com', 5, 1, NOW(), NOW()),
('enterprise102', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5E', '卫强', '13900010002', 'weiqiang@didichuxing.com', 5, 1, NOW(), NOW());

-- =============================================
-- 2. 更新企业信息中的联系人
-- =============================================
UPDATE t_company SET contact_name = '张伟', contact_position = 'HR经理', contact_phone = '13900001001', contact_email = 'zhangwei@alibaba.com' WHERE id = 1;
UPDATE t_company SET contact_name = '王强', contact_position = '招聘负责人', contact_phone = '13900002001', contact_email = 'wangqiang@tencent.com' WHERE id = 2;
UPDATE t_company SET contact_name = '陈明', contact_position = '技术总监', contact_phone = '13900003001', contact_email = 'chenming@bytedance.com' WHERE id = 3;
UPDATE t_company SET contact_name = '杨秀兰', contact_position = 'HR总监', contact_phone = '13900004001', contact_email = 'yangxiulan@huawei.com' WHERE id = 4;
UPDATE t_company SET contact_name = '孙丽', contact_position = '招聘经理', contact_phone = '13900005001', contact_email = 'sunli@meituan.com' WHERE id = 5;
UPDATE t_company SET contact_name = '周涛', contact_position = '人力资源部', contact_phone = '13900006001', contact_email = 'zhoutao@jd.com' WHERE id = 6;
UPDATE t_company SET contact_name = '郑敏', contact_position = '校园招聘', contact_phone = '13900007001', contact_email = 'zhengmin@netease.com' WHERE id = 7;
UPDATE t_company SET contact_name = '王磊', contact_position = '技术招聘', contact_phone = '13900008001', contact_email = 'wanglei@baidu.com' WHERE id = 8;
UPDATE t_company SET contact_name = '陈超', contact_position = 'HRBP', contact_phone = '13900009001', contact_email = 'chenchao@xiaomi.com' WHERE id = 9;
UPDATE t_company SET contact_name = '褚欣', contact_position = '人才发展', contact_phone = '13900010001', contact_email = 'chuxin@didichuxing.com' WHERE id = 10;

-- =============================================
-- 3. 为现有职位添加更多招聘信息
-- =============================================
UPDATE t_recruitment_position SET recruit_num = 8, description = '负责后端系统开发和维护，参与系统架构设计' WHERE id = 1;
UPDATE t_recruitment_position SET recruit_num = 5, description = '负责前端页面开发和优化，参与产品需求讨论' WHERE id = 2;
UPDATE t_recruitment_position SET recruit_num = 6, description = '负责AI平台开发，参与算法优化工作' WHERE id = 3;
UPDATE t_recruitment_position SET recruit_num = 3, description = '负责后端服务开发，参与微服务架构建设' WHERE id = 4;
UPDATE t_recruitment_position SET recruit_num = 4, description = '负责推荐算法优化，提升用户体验' WHERE id = 5;
UPDATE t_recruitment_position SET recruit_num = 10, description = '负责嵌入式系统开发，参与硬件驱动开发' WHERE id = 6;
UPDATE t_recruitment_position SET recruit_num = 5, description = '负责业务数据分析，提供数据支持决策' WHERE id = 7;
UPDATE t_recruitment_position SET recruit_num = 6, description = '负责全栈开发工作，独立完成功能模块' WHERE id = 8;
UPDATE t_recruitment_position SET recruit_num = 4, description = '负责游戏客户端开发，参与游戏策划' WHERE id = 9;
UPDATE t_recruitment_position SET recruit_num = 3, description = '负责AI模型开发，参与深度学习研究' WHERE id = 10;

-- =============================================
-- 4. 插入更多求职申请测试数据
-- =============================================
INSERT INTO t_job_application (student_id, position_id, company_id, status, apply_time, create_time, update_time) VALUES
-- student1 申请阿里巴巴的职位
(1, 1, 1, 'interview_scheduled', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), NOW()),
-- student2 申请腾讯的职位
(2, 3, 2, 'written_test_passed', DATE_SUB(NOW(), INTERVAL 7 DAY), NOW(), NOW()),
-- student3 申请字节跳动的职位
(3, 5, 3, 'interview_passed', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), NOW()),
-- student4 申请华为的职位
(4, 6, 4, 'pending', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), NOW()),
-- student5 申请美团的职位
(5, 7, 5, 'written_test_scheduled', DATE_SUB(NOW(), INTERVAL 4 DAY), NOW(), NOW()),
-- student6 申请京东的职位
(6, 8, 6, 'interview_failed', DATE_SUB(NOW(), INTERVAL 10 DAY), NOW(), NOW()),
-- student7 申请网易的职位
(7, 9, 7, 'pending', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), NOW()),
-- student8 申请百度的职位
(8, 10, 8, 'written_test_failed', DATE_SUB(NOW(), INTERVAL 8 DAY), NOW(), NOW()),
-- student9 申请小米的职位
(9, 1, 1, 'rejected', DATE_SUB(NOW(), INTERVAL 15 DAY), NOW(), NOW()),
-- student10 申请滴滴的职位
(10, 3, 2, 'interview_scheduled', DATE_SUB(NOW(), INTERVAL 6 DAY), NOW(), NOW());

-- =============================================
-- 5. 插入笔试测试数据
-- =============================================
INSERT INTO t_written_test (application_id, company_id, position_id, test_date, test_location, test_score, test_status, examiner_id, create_time, update_time) VALUES
(2, 2, 3, DATE_ADD(NOW(), INTERVAL 2 DAY), '线上测试', 85, 'passed', 21, NOW(), NOW()),
(4, 2, 4, DATE_ADD(NOW(), INTERVAL 1 DAY), '深圳总部', NULL, 'scheduled', 21, NOW(), NOW()),
(5, 5, 7, DATE_SUB(NOW(), INTERVAL 2 DAY), '线上测试', 72, 'passed', 51, NOW(), NOW()),
(8, 8, 10, DATE_SUB(NOW(), INTERVAL 5 DAY), '北京总部', 58, 'failed', 81, NOW(), NOW()),
(10, 2, 3, DATE_ADD(NOW(), INTERVAL 3 DAY), '线上测试', NULL, 'scheduled', 21, NOW(), NOW());

-- =============================================
-- 6. 插入面试测试数据
-- =============================================
INSERT INTO t_interview (application_id, company_id, position_id, interview_round, interview_date, interview_location, interviewer, interview_status, interview_score, feedback, create_time, update_time) VALUES
(1, 1, 1, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), '杭州总部', '张伟', 'scheduled', NULL, NULL, NOW(), NOW()),
(3, 3, 5, 2, DATE_SUB(NOW(), INTERVAL 3 DAY), '北京总部', '陈明', 'passed', 88, '技术扎实，沟通能力强', NOW(), NOW()),
(6, 6, 8, 1, DATE_SUB(NOW(), INTERVAL 8 DAY), '北京总部', '周涛', 'failed', 62, '项目经验不足', NOW(), NOW()),
(10, 2, 3, 1, DATE_ADD(NOW(), INTERVAL 5 DAY), '深圳总部', '王强', 'scheduled', NULL, NULL, NOW(), NOW());

-- =============================================
-- 7. 插入更多Offer测试数据
-- =============================================
INSERT INTO t_offer (student_id, position_id, company_id, salary, offer_date, status, create_time, update_time) VALUES
(3, 5, 3, 35000, DATE_SUB(NOW(), INTERVAL 1 DAY), 'pending', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), NOW());

-- =============================================
-- 8. 插入企业负责人通知数据
-- =============================================
INSERT INTO t_notification (user_id, type, title, content, related_id, is_read, read_time, create_time, update_time) VALUES
-- 通知给阿里巴巴的企业负责人
(11, 'info', '新简历投递提醒', '张三同学申请了Java后端开发工程师岗位，请及时查看简历', 1, 0, NULL, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
(11, 'success', '面试安排成功', '已成功安排与张三同学的面试，时间：明天下午2点', 1, 0, NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(12, 'info', '新职位发布提醒', '前端开发工程师职位已成功发布，正在接受投递', 2, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),

-- 通知给腾讯的企业负责人
(13, 'info', '新简历投递提醒', '李四同学申请了Python开发工程师岗位', 3, 0, NULL, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(13, 'info', '笔试成绩已出', '李四同学的笔试成绩为85分，已通过笔试', 3, 0, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(14, 'warning', '面试提醒', '请准备王二同学的面试材料，面试时间：后天上午10点', 3, 0, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),

-- 通知给字节跳动的企业负责人
(15, 'success', '候选人通过面试', '王五同学通过了两轮技术面试，可以发送Offer', 5, 1, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(15, 'info', 'Offer待确认', '王五同学的Offer已发送，等待对方确认', 5, 0, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

-- 通知给华为的企业负责人
(16, 'info', '新简历投递提醒', '赵六同学申请了嵌入式开发工程师岗位', 6, 0, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(17, 'warning', '笔试安排提醒', '请及时安排赵六同学的笔试时间', 6, 0, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),

-- 通知给美团的企业负责人
(18, 'info', '简历投递统计', '本周共收到5份新简历，请登录系统查看', 7, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(18, 'success', '笔试完成通知', '钱七同学的笔试已完成，请查看成绩', 7, 0, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW());

-- =============================================
-- 提示信息
-- =============================================
SELECT '=============================================' AS '';
SELECT '企业负责人测试数据插入完成!' AS message;
SELECT '=============================================' AS '';
SELECT '已插入数据统计:' AS summary;
SELECT CONCAT('企业负责人用户: ', COUNT(*), ' 条') AS info FROM t_user WHERE role_id = 5;
SELECT CONCAT('企业信息已更新: ', COUNT(*), ' 条') AS info FROM t_company WHERE contact_name IS NOT NULL;
SELECT CONCAT('求职申请: ', COUNT(*), ' 条') AS info FROM t_job_application;
SELECT CONCAT('笔试记录: ', COUNT(*), ' 条') AS info FROM t_written_test;
SELECT CONCAT('面试记录: ', COUNT(*), ' 条') AS info FROM t_interview;
SELECT CONCAT('Offer记录: ', COUNT(*), ' 条') AS info FROM t_offer;
SELECT CONCAT('通知消息: ', COUNT(*), ' 条') AS info FROM t_notification WHERE user_id IN (11, 12, 13, 14, 15, 16, 17, 18);
SELECT '=============================================' AS '';
SELECT '测试账号信息:' AS '';
SELECT '阿里巴巴: enterprise11 / 123456 (张伟)' AS account;
SELECT '阿里巴巴: enterprise12 / 123456 (李娜)' AS account;
SELECT '腾讯: enterprise21 / 123456 (王强)' AS account;
SELECT '腾讯: enterprise22 / 123456 (刘洋)' AS account;
SELECT '字节跳动: enterprise31 / 123456 (陈明)' AS account;
SELECT '华为: enterprise41 / 123456 (杨秀兰)' AS account;
SELECT '华为: enterprise42 / 123456 (赵军)' AS account;
SELECT '美团: enterprise51 / 123456 (孙丽)' AS account;
SELECT '京东: enterprise61 / 123456 (周涛)' AS account;
SELECT '网易: enterprise71 / 123456 (郑敏)' AS account;
SELECT '百度: enterprise81 / 123456 (王磊)' AS account;
SELECT '小米: enterprise91 / 123456 (陈超)' AS account;
SELECT '滴滴: enterprise101 / 123456 (褚欣)' AS account;
SELECT '=============================================' AS '';
