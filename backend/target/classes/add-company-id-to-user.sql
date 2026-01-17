-- =============================================
-- 为用户表添加company_id字段
-- 用于关联企业对接人用户和企业
-- =============================================

USE teaching_employment_platform;

-- 添加company_id字段
ALTER TABLE t_user
ADD COLUMN company_id BIGINT NULL COMMENT '企业ID（企业对接人专用）' AFTER school_id,
ADD INDEX idx_company_id (company_id);

-- 为企业对接人用户分配企业ID
-- 这里将测试企业对接人账号分配给企业1
UPDATE t_user
SET company_id = 1
WHERE role_id = 5 AND id = 25;

-- 查看更新结果
SELECT
    u.id,
    u.username,
    u.real_name,
    r.role_name,
    u.company_id,
    c.company_name
FROM t_user u
LEFT JOIN t_role r ON u.role_id = r.id
LEFT JOIN t_company c ON u.company_id = c.id
WHERE u.role_id = 5;

-- 说明：
-- 1. 添加了company_id字段到t_user表
-- 2. 为企业对接人（role_id=5）的用户分配了对应的企业ID
-- 3. 登录时会返回companyId，前端人才库页面会使用此ID
