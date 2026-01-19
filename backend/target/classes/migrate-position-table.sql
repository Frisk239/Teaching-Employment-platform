-- 职位表结构完善脚本
-- 添加缺失的字段以匹配实体类
-- 执行时间: 2026-01-19

USE teaching_employment_platform;

-- 查看当前表结构
DESC t_position;

-- 添加缺失的字段（不添加is_deleted，使用物理删除）
ALTER TABLE t_position
  ADD COLUMN work_city VARCHAR(200) COMMENT '工作城市' AFTER position_type,
  ADD COLUMN education_require VARCHAR(50) COMMENT '学历要求' AFTER requirements,
  ADD COLUMN experience_require VARCHAR(50) COMMENT '工作年限要求' AFTER education_require,
  ADD COLUMN tech_stack TEXT COMMENT '技术栈（JSON数组）' AFTER experience_require,
  ADD COLUMN capability_radar TEXT COMMENT '能力雷达图数据（JSON对象）' AFTER tech_stack,
  ADD COLUMN recruit_num INT DEFAULT 1 COMMENT '招聘人数' AFTER capability_radar,
  ADD COLUMN hired_count INT DEFAULT 0 COMMENT '已招人数' AFTER recruit_num,
  ADD COLUMN application_count INT DEFAULT 0 COMMENT '简历投递数' AFTER hired_count,
  ADD COLUMN publish_time DATETIME COMMENT '发布时间' AFTER application_count,
  ADD COLUMN deadline DATE COMMENT '截止日期' AFTER publish_time;

-- 更新现有数据：将 location 复制到 work_city，将 recruitment_count 复制到 recruit_num
UPDATE t_position
SET
  work_city = location,
  recruit_num = recruitment_count
WHERE work_city IS NULL;

-- 查看修改后的表结构
DESC t_position;

-- 验证数据迁移
SELECT
  id,
  position_name,
  location,
  work_city,
  recruitment_count,
  recruit_num
FROM t_position
LIMIT 5;
