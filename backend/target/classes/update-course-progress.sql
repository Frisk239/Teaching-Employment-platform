-- 更新课程学员关联表的学习进度
-- 为不同学员设置不同的学习进度,用于测试前端显示

-- 为学生ID=1的学员设置不同课程的进度
UPDATE t_course_student SET progress = 85.5 WHERE course_id = 1 AND student_id = 1;  -- 数据结构与算法: 85.5%
UPDATE t_course_student SET progress = 45.0 WHERE course_id = 2 AND student_id = 1;  -- Web开发基础: 45%
UPDATE t_course_student SET progress = 100.0 WHERE course_id = 3 AND student_id = 1;  -- 数据库系统: 已完成
UPDATE t_course_student SET progress = 30.5 WHERE course_id = 4 AND student_id = 1;  -- 软件工程: 30.5%
UPDATE t_course_student SET progress = 0 WHERE course_id = 5 AND student_id = 1;  -- 人工智能导论: 未开始
UPDATE t_course_student SET progress = 60.0 WHERE course_id = 6 AND student_id = 1;  -- 计算机网络: 60%

-- 为学生ID=2的学员设置不同课程的进度
UPDATE t_course_student SET progress = 95.0 WHERE course_id = 1 AND student_id = 2;  -- 数据结构与算法: 95%
UPDATE t_course_student SET progress = 70.0 WHERE course_id = 2 AND student_id = 2;  -- Web开发基础: 70%
UPDATE t_course_student SET progress = 88.5 WHERE course_id = 3 AND student_id = 2;  -- 数据库系统: 88.5%
UPDATE t_course_student SET progress = 55.5 WHERE course_id = 4 AND student_id = 2;  -- 软件工程: 55.5%
UPDATE t_course_student SET progress = 20.0 WHERE course_id = 5 AND student_id = 2;  -- 人工智能导论: 20%

-- 为学生ID=3的学员设置不同课程的进度
UPDATE t_course_student SET progress = 60.5 WHERE course_id = 1 AND student_id = 3;  -- 数据结构与算法: 60.5%
UPDATE t_course_student SET progress = 35.0 WHERE course_id = 2 AND student_id = 3;  -- Web开发基础: 35%
UPDATE t_course_student SET progress = 75.5 WHERE course_id = 3 AND student_id = 3;  -- 数据库系统: 75.5%
UPDATE t_course_student SET progress = 90.0 WHERE course_id = 4 AND student_id = 3;  -- 软件工程: 90%
UPDATE t_course_student SET progress = 50.0 WHERE course_id = 5 AND student_id = 3;  -- 人工智能导论: 50%

-- 为学生ID=4的学员设置不同课程的进度
UPDATE t_course_student SET progress = 40.0 WHERE course_id = 1 AND student_id = 4;  -- 数据结构与算法: 40%
UPDATE t_course_student SET progress = 85.5 WHERE course_id = 2 AND student_id = 4;  -- Web开发基础: 85.5%
UPDATE t_course_student SET progress = 65.0 WHERE course_id = 3 AND student_id = 4;  -- 数据库系统: 65%
UPDATE t_course_student SET progress = 100.0 WHERE course_id = 4 AND student_id = 4;  -- 软件工程: 已完成

-- 为学生ID=5的学员设置不同课程的进度
UPDATE t_course_student SET progress = 75.0 WHERE course_id = 1 AND student_id = 5;  -- 数据结构与算法: 75%
UPDATE t_course_student SET progress = 55.5 WHERE course_id = 2 AND student_id = 5;  -- Web开发基础: 55.5%
UPDATE t_course_student SET progress = 30.0 WHERE course_id = 3 AND student_id = 5;  -- 数据库系统: 30%
UPDATE t_course_student SET progress = 100.0 WHERE course_id = 4 AND student_id = 5;  -- 软件工程: 已完成
UPDATE t_course_student SET progress = 80.0 WHERE course_id = 5 AND student_id = 5;  -- 人工智能导论: 80%
