-- 教师表结构完善脚本
-- 添加教师基本信息字段
-- 执行时间: 2026-01-19

USE teaching_employment_platform;

-- 查看当前表结构
DESC t_teacher;

-- 添加缺失的字段（MySQL不支持IF NOT EXISTS，如果字段存在会报错，但可以忽略）
ALTER TABLE t_teacher
  ADD COLUMN entry_date DATE COMMENT '入职日期' AFTER education,
  ADD COLUMN id_card VARCHAR(18) COMMENT '身份证号' AFTER gender,
  ADD COLUMN birth_date DATE COMMENT '出生日期' AFTER id_card,
  ADD COLUMN address VARCHAR(200) COMMENT '家庭地址' AFTER birth_date;

-- 查看修改后的表结构
DESC t_teacher;

-- 验证字段添加成功
SELECT
  id,
  teacher_no,
  name,
  gender,
  id_card,
  birth_date,
  entry_date,
  address,
  phone,
  email,
  department,
  title,
  education,
  specialization
FROM t_teacher
LIMIT 5;
