-- 修复 t_notification 表，改为物理删除
ALTER TABLE t_notification DROP COLUMN is_deleted;
