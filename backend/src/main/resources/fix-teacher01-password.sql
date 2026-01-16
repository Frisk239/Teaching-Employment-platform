-- 修复teacher01账号的密码
USE teaching_employment_platform;

-- 直接更新teacher01的密码
UPDATE t_user SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi' WHERE username = 'teacher01';

-- 验证修复结果
SELECT id, username, real_name, role_id, status FROM t_user WHERE username = 'teacher01';

SELECT 'teacher01密码已修复!' AS '成功';
