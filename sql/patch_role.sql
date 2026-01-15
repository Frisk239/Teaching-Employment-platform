-- 修复角色编码不一致问题
-- 前端使用 'user' 作为学生角色,数据库使用 'student',需要统一为 'user'

USE teaching_employment_platform;

-- 检查当前角色数据
SELECT '修复前的角色数据:' AS info;
SELECT id, role_code, role_name FROM t_role;

-- 将 student 角色编码改为 user
UPDATE t_role
SET role_code = 'user',
    role_name = '学生'
WHERE role_code = 'student';

-- 验证修改结果
SELECT '修复后的角色数据:' AS info;
SELECT id, role_code, role_name FROM t_role;

COMMIT;
