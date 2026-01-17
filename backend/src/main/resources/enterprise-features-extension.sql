-- =============================================
-- 企业负责人功能扩展 - 数据库表结构
-- 包含：人才库表、企业表扩展字段
-- =============================================

USE teaching_employment_platform;

-- =============================================
-- 1. 人才库表
-- =============================================
DROP TABLE IF EXISTS t_talent_pool;
CREATE TABLE t_talent_pool (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '人才库ID',
    company_id BIGINT NOT NULL COMMENT '企业ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    resume_id BIGINT COMMENT '简历ID',
    source VARCHAR(50) DEFAULT 'manual' COMMENT '来源：manual-手动添加 application-求职申请 interview-面试推荐',
    source_id BIGINT COMMENT '来源ID（如申请ID、面试ID）',
    position_name VARCHAR(100) COMMENT '意向职位',
    tags VARCHAR(500) COMMENT '人才标签（多个标签用逗号分隔，如：前端专家,Vue,应届生）',
    category VARCHAR(50) COMMENT '人才分类：frontend-前端 backend-后端 mobile-移动端 product-产品 design-设计 operation-运营 other-其他',
    skill_tags VARCHAR(500) COMMENT '技能标签（JSON数组格式）',
    experience_level VARCHAR(50) COMMENT '经验级别：junior-初级 middle-中级 senior-高级 expert-专家',
    education VARCHAR(50) COMMENT '学历：junior_college-大专 bachelor-本科 master-硕士 doctor-博士 unlimited-不限',
    expected_salary_min DECIMAL(10,2) COMMENT '期望薪资最低',
    expected_salary_max DECIMAL(10,2) COMMENT '期望薪资最高',
    expected_city VARCHAR(100) COMMENT '期望城市',
    rating INT DEFAULT 3 COMMENT '人才评分（1-5星）',
    priority VARCHAR(20) DEFAULT 'normal' COMMENT '优先级：low-低 normal-中 high-高 urgent-紧急',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-激活 contacted-已联系 locked-已锁定 inactive-未激活',
    last_contact_date DATE COMMENT '最后联系日期',
    last_contact_method VARCHAR(50) COMMENT '最后联系方式：email-邮件 phone-电话 message-站内信 interview-面试',
    next_contact_date DATE COMMENT '下次联系日期',
    remark TEXT COMMENT '备注信息',
    contact_count INT DEFAULT 0 COMMENT '联系次数',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (company_id) REFERENCES t_company(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    FOREIGN KEY (resume_id) REFERENCES t_resume(id),
    INDEX idx_company_id (company_id),
    INDEX idx_student_id (student_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    UNIQUE KEY uk_company_student (company_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人才库表';

-- =============================================
-- 2. Offer模板表
-- =============================================
DROP TABLE IF EXISTS t_offer_template;
CREATE TABLE t_offer_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    company_id BIGINT NOT NULL COMMENT '企业ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_type VARCHAR(50) DEFAULT 'general' COMMENT '模板类型：general-通用 internship-实习 campus-校园社招 social-社招',
    template_content TEXT NOT NULL COMMENT '模板内容（支持变量占位符）',
    template_variables JSON COMMENT '模板变量定义（JSON格式）',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认模板：1-是 0-否',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用 0-停用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (company_id) REFERENCES t_company(id),
    INDEX idx_company_id (company_id),
    INDEX idx_template_type (template_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Offer模板表';

-- =============================================
-- 3. 更新企业表 - 添加相册和视频字段
-- =============================================
ALTER TABLE t_company
ADD COLUMN company_albums JSON COMMENT '企业相册（JSON数组，包含图片URL和描述）' AFTER description,
ADD COLUMN recruitment_video VARCHAR(255) COMMENT '招聘宣传片URL' AFTER company_albums,
ADD COLUMN video_cover VARCHAR(255) COMMENT '视频封面URL' AFTER recruitment_video;

-- =============================================
-- 4. 人才联系记录表（可选，用于记录联系历史）
-- =============================================
DROP TABLE IF EXISTS t_talent_contact_log;
CREATE TABLE t_talent_contact_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    talent_pool_id BIGINT NOT NULL COMMENT '人才库ID',
    company_id BIGINT NOT NULL COMMENT '企业ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    contact_type VARCHAR(50) NOT NULL COMMENT '联系方式：email-邮件 phone-电话 message-站内信 interview-面试 other-其他',
    contact_content TEXT COMMENT '联系内容',
    contact_result VARCHAR(50) COMMENT '联系结果：success-成功 failed-失败 pending-待回复',
    next_follow_up_date DATE COMMENT '下次跟进日期',
    contact_person VARCHAR(50) COMMENT '联系人',
    remark TEXT COMMENT '备注',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (talent_pool_id) REFERENCES t_talent_pool(id),
    FOREIGN KEY (company_id) REFERENCES t_company(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    INDEX idx_talent_pool_id (talent_pool_id),
    INDEX idx_company_id (company_id),
    INDEX idx_contact_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人才联系记录表';

-- =============================================
-- 插入默认Offer模板数据（可选）
-- =============================================
INSERT INTO t_offer_template (company_id, template_name, template_type, template_content, template_variables, is_default) VALUES
(1, '通用Offer模板', 'general',
'尊敬的{student_name}同学：

您好！

经过我们双方的深入沟通与了解，{company_name}非常高兴地向您发出录用邀请！

一、录用信息
==================
职位名称：{position_name}
入职城市：{city}
薪资待遇：{salary}{salary_unit}
入职日期：{onboard_date}

二、工作职责
==================
{job_responsibilities}

三、福利待遇
==================
{benefits}

四、接受说明
==================
请您在{accept_deadline}前确认是否接受此Offer。确认后，我们将为您办理入职手续。

如有任何疑问，请随时联系我们：
联系人：{contact_person}
联系电话：{contact_phone}
电子邮箱：{contact_email}

我们期待您的加入，与{company_name}共同成长！

此致
敬礼！

{company_name}
{create_date}',
'["student_name", "company_name", "position_name", "city", "salary", "salary_unit", "onboard_date", "job_responsibilities", "benefits", "accept_deadline", "contact_person", "contact_phone", "contact_email", "create_date"]',
1);

-- =============================================
-- 索引优化（可选）
-- =============================================
-- 为人才库表添加全文索引（用于搜索）
ALTER TABLE t_talent_pool ADD FULLTEXT INDEX ft_search (tags, skill_tags, remark);

-- =============================================
-- 说明
-- =============================================
-- 1. t_talent_pool: 人才库主表，存储企业收藏的候选人信息
-- 2. t_offer_template: Offer模板表，支持企业自定义Offer模板
-- 3. t_talent_contact_log: 人才联系记录表，记录与候选人的沟通历史
-- 4. 企业表扩展字段：
--    - company_albums: 企业相册（JSON数组格式）
--    - recruitment_video: 招聘宣传片URL
--    - video_cover: 视频封面URL
--
-- 使用方法：
-- 1. 执行此SQL文件创建表结构
-- 2. 重启后端服务以加载新的Entity
-- 3. 前端实现对应的管理页面
