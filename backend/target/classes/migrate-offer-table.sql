-- Offer表结构完善脚本
-- 添加缺失的字段以匹配实体类
-- 执行时间: 2026-01-19

USE teaching_employment_platform;

-- 查看当前表结构
DESC t_offer;

-- 添加缺失的字段
ALTER TABLE t_offer
  ADD COLUMN application_id BIGINT COMMENT '申请ID' AFTER id,
  ADD COLUMN offer_no VARCHAR(50) COMMENT 'Offer编号' AFTER position_id,
  ADD COLUMN city VARCHAR(100) COMMENT '入职城市' AFTER position_name,
  ADD COLUMN salary_unit VARCHAR(20) COMMENT '薪资单位：month-按月 year-按年' AFTER salary,
  ADD COLUMN accept_deadline DATE COMMENT '接受截止日期' AFTER status,
  ADD COLUMN accept_time DATETIME COMMENT '接受时间' AFTER accept_deadline,
  ADD COLUMN email_status VARCHAR(20) COMMENT '邮件发送状态：pending-待发送 sent-已发送 failed-发送失败' AFTER accept_time,
  ADD COLUMN email_send_time DATETIME COMMENT '邮件发送时间' AFTER email_status,
  ADD COLUMN attachment_url VARCHAR(255) COMMENT '附件URL（Offer PDF）' AFTER email_send_time,
  ADD COLUMN remark TEXT COMMENT '备注' AFTER attachment_url;

-- 重命名字段 start_date 为 onboard_date
ALTER TABLE t_offer CHANGE COLUMN start_date onboard_date DATE COMMENT '入职日期';

-- 重命名字段 offer_file_url 为 offerFileUrl (实际上数据库应该用下划线，保持一致性)
ALTER TABLE t_offer CHANGE COLUMN offer_file_url offer_file_url VARCHAR(255) COMMENT 'Offer文件URL';

-- 删除 is_deleted 字段（使用物理删除）
ALTER TABLE t_offER DROP COLUMN is_deleted;

-- 查看修改后的表结构
DESC t_offer;

-- 验证字段添加成功
SELECT
  id,
  application_id,
  student_id,
  company_id,
  position_id,
  offer_no,
  position_name,
  city,
  salary,
  salary_unit,
  onboard_date,
  status,
  accept_deadline,
  accept_time,
  email_status,
  email_send_time,
  attachment_url,
  remark
FROM t_offer
LIMIT 5;
