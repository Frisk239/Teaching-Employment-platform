-- 更新教学资料表结构，添加缺失字段
-- 执行日期: 2026-01-19

USE teaching_employment_platform;

-- 1. 添加文件大小字段
ALTER TABLE t_teaching_material ADD COLUMN file_size BIGINT COMMENT '文件大小(字节)' AFTER file_url;

-- 2. 添加教师ID字段
ALTER TABLE t_teaching_material ADD COLUMN teacher_id BIGINT COMMENT '教师ID' AFTER course_id;

-- 3. 添加分类标签字段
ALTER TABLE t_teaching_material ADD COLUMN category VARCHAR(100) COMMENT '分类标签' AFTER teacher_id;

-- 4. 添加标签字段
ALTER TABLE t_teaching_material ADD COLUMN tags VARCHAR(500) COMMENT '标签(逗号分隔)' AFTER category;

-- 5. 添加是否公开字段
ALTER TABLE t_teaching_material ADD COLUMN is_public TINYINT DEFAULT 0 COMMENT '是否对学员公开：0-私有 1-公开' AFTER description;

-- 6. 添加下载次数字段
ALTER TABLE t_teaching_material ADD COLUMN download_count INT DEFAULT 0 COMMENT '下载次数' AFTER is_public;

-- 7. 添加查看次数字段
ALTER TABLE t_teaching_material ADD COLUMN view_count INT DEFAULT 0 COMMENT '查看次数' AFTER download_count;

-- 8. 添加状态字段
ALTER TABLE t_teaching_material ADD COLUMN status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-激活 inactive-停用' AFTER view_count;

-- 查看更新后的表结构
DESC t_teaching_material;
