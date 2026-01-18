-- 清除所有表的数据（保留表结构）
-- 按照外键依赖顺序删除

SET NAMES utf8mb4;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 删除就业管理相关数据
DELETE FROM t_talent_pool;
DELETE FROM t_offer;
DELETE FROM t_interview;
DELETE FROM t_written_test;
DELETE FROM t_job_application;
DELETE FROM t_position;

-- 删除学员相关扩展数据
DELETE FROM t_internship;
DELETE FROM t_education;

-- 删除学习管理相关数据
DELETE FROM t_student_exam_record;
DELETE FROM t_exam;
DELETE FROM t_teaching_material;
DELETE FROM t_daily_report;
DELETE FROM t_homework_submission;
DELETE FROM t_homework;

-- 删除课程相关数据
DELETE FROM t_timetable;
DELETE FROM t_course_student;

-- 删除用户和学校相关数据
DELETE FROM t_student;
DELETE FROM t_teacher;
DELETE FROM t_course;
DELETE FROM t_classroom;
DELETE FROM t_company;
DELETE FROM t_user;

-- 删除权限和菜单相关数据
DELETE FROM t_role_menu_relation;
DELETE FROM t_role_permission_relation;
DELETE FROM t_menu;
DELETE FROM t_permission;
DELETE FROM t_role;

-- 重置自增ID
ALTER TABLE t_talent_pool AUTO_INCREMENT = 1;
ALTER TABLE t_offer AUTO_INCREMENT = 1;
ALTER TABLE t_interview AUTO_INCREMENT = 1;
ALTER TABLE t_written_test AUTO_INCREMENT = 1;
ALTER TABLE t_job_application AUTO_INCREMENT = 1;
ALTER TABLE t_position AUTO_INCREMENT = 1;
ALTER TABLE t_internship AUTO_INCREMENT = 1;
ALTER TABLE t_education AUTO_INCREMENT = 1;
ALTER TABLE t_student_exam_record AUTO_INCREMENT = 1;
ALTER TABLE t_exam AUTO_INCREMENT = 1;
ALTER TABLE t_teaching_material AUTO_INCREMENT = 1;
ALTER TABLE t_daily_report AUTO_INCREMENT = 1;
ALTER TABLE t_homework_submission AUTO_INCREMENT = 1;
ALTER TABLE t_homework AUTO_INCREMENT = 1;
ALTER TABLE t_timetable AUTO_INCREMENT = 1;
ALTER TABLE t_course_student AUTO_INCREMENT = 1;
ALTER TABLE t_student AUTO_INCREMENT = 1;
ALTER TABLE t_teacher AUTO_INCREMENT = 1;
ALTER TABLE t_course AUTO_INCREMENT = 1;
ALTER TABLE t_classroom AUTO_INCREMENT = 1;
ALTER TABLE t_company AUTO_INCREMENT = 1;
ALTER TABLE t_user AUTO_INCREMENT = 1;
ALTER TABLE t_role_menu_relation AUTO_INCREMENT = 1;
ALTER TABLE t_role_permission_relation AUTO_INCREMENT = 1;
ALTER TABLE t_menu AUTO_INCREMENT = 1;
ALTER TABLE t_permission AUTO_INCREMENT = 1;
ALTER TABLE t_role AUTO_INCREMENT = 1;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

SELECT '所有数据已清除，数据库已准备好插入新的测试数据' as status;
