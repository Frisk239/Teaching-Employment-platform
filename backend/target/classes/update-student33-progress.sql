-- 为学生ID=33(测试学员student01)设置不同的课程进度
-- 模拟真实学习场景:有的课程刚开始,有的进行中,有的快完成,有的已完成

UPDATE t_course_student SET progress = 85.5 WHERE course_id = 2 AND student_id = 33;  -- Java Programming: 85.5%
UPDATE t_course_student SET progress = 45.0 WHERE course_id = 3 AND student_id = 33;  -- Web Development: 45%
UPDATE t_course_student SET progress = 60.0 WHERE course_id = 4 AND student_id = 33;  -- Java编程: 60%
UPDATE t_course_student SET progress = 100.0 WHERE course_id = 5 AND student_id = 33;  -- Web开发: 已完成
UPDATE t_course_student SET progress = 30.5 WHERE course_id = 6 AND student_id = 33;  -- 数据库: 30.5%
UPDATE t_course_student SET progress = 0 WHERE course_id = 7 AND student_id = 33;  -- 算法设计: 未开始
UPDATE t_course_student SET progress = 55.5 WHERE course_id = 8 AND student_id = 33;  -- 操作系统: 55.5%
UPDATE t_course_student SET progress = 92.0 WHERE course_id = 14 AND student_id = 33;  -- Java开发基础: 92%
UPDATE t_course_student SET progress = 78.5 WHERE course_id = 15 AND student_id = 33;  -- 数据库原理: 78.5%
UPDATE t_course_student SET progress = 40.0 WHERE course_id = 16 AND student_id = 33;  -- Web前端开发: 40%
UPDATE t_course_student SET progress = 65.0 WHERE course_id = 17 AND student_id = 33;  -- 数据结构与算法: 65%
UPDATE t_course_student SET progress = 20.0 WHERE course_id = 18 AND student_id = 33;  -- 项目实战: 20%
UPDATE t_course_student SET progress = 95.0 WHERE course_id = 19 AND student_id = 33;  -- Java开发基础(重复): 95%
UPDATE t_course_student SET progress = 88.5 WHERE course_id = 20 AND student_id = 33;  -- 数据库原理(重复): 88.5%
UPDATE t_course_student SET progress = 35.0 WHERE course_id = 21 AND student_id = 33;  -- Web前端开发(重复): 35%
UPDATE t_course_student SET progress = 50.0 WHERE course_id = 22 AND student_id = 33;  -- 数据结构与算法(重复): 50%
UPDATE t_course_student SET progress = 15.0 WHERE course_id = 23 AND student_id = 33;  -- 项目实战(重复): 15%
