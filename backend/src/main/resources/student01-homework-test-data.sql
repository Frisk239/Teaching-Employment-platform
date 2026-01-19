-- 为学员 student01 (ID=1) 插入完整的作业测试数据
-- 日期: 2026-01-19

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE teaching_employment_platform;

-- 首先清理学员1的旧作业数据（可选）
DELETE FROM t_homework_submission WHERE student_id = 1;

-- 插入课程数据（如果还没有课程）
INSERT IGNORE INTO t_course (id, course_name, course_code, credits, hours, description, teacher_id, school_id, status, create_time, update_time)
VALUES
(1, 'Java程序设计', 'CS101', 4.0, 64, '学习Java基础语法、面向对象编程、集合框架等', 1, 1, 'active', NOW(), NOW()),
(2, 'Web前端开发', 'CS102', 3.0, 48, '学习HTML、CSS、JavaScript等前端技术', 1, 1, 'active', NOW(), NOW()),
(3, '数据库原理', 'CS103', 3.0, 48, '学习MySQL数据库设计、SQL语句、事务处理等', 1, 1, 'active', NOW(), NOW()),
(4, '数据结构与算法', 'CS104', 4.0, 64, '学习常用数据结构和算法设计', 1, 1, 'active', NOW(), NOW()),
(5, 'Spring框架开发', 'CS105', 3.0, 48, '学习Spring、SpringMVC、MyBatis等框架', 1, 1, 'active', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    course_name = VALUES(course_name),
    course_code = VALUES(course_code);

-- 为学员1选课（如果还没有）
INSERT IGNORE INTO t_course_student (course_id, student_id, enrollment_date, status, progress, create_time, update_time)
VALUES
(1, 1, '2026-01-01', 1, 75.5, NOW(), NOW()),
(2, 1, '2026-01-01', 1, 60.0, NOW(), NOW()),
(3, 1, '2026-01-01', 1, 45.0, NOW(), NOW()),
(4, 1, '2026-01-01', 1, 30.0, NOW(), NOW()),
(5, 1, '2026-01-01', 1, 20.0, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    enrollment_date = VALUES(enrollment_date),
    progress = VALUES(progress);

-- 插入作业数据
INSERT INTO t_homework (
    id, course_id, title, content, max_score, homework_type, status, attachment_url, deadline, create_time, update_time
) VALUES
-- Java程序设计作业
(1, 1, 'Java基础语法练习', '完成第1-3章的课后练习题，包括变量声明、条件语句、循环语句等基础语法练习', 100.0, 'assignment', 'published', NULL, '2026-01-25 23:59:59', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),

(2, 1, '面向对象编程实践', '设计一个学生类，包含姓名、学号、年龄等属性，实现构造方法、getter/setter方法和toString方法', 100.0, 'project', 'published', NULL, '2026-02-01 23:59:59', '2026-01-12 10:00:00', '2026-01-12 10:00:00'),

(3, 1, '集合框架应用', '使用ArrayList、HashMap等集合类实现一个简单的学生成绩管理系统', 100.0, 'project', 'published', NULL, '2026-02-10 23:59:59', '2026-01-15 10:00:00', '2026-01-15 10:00:00'),

-- Web前端开发作业
(4, 2, 'HTML页面设计', '设计一个个人主页，包含个人信息、联系方式、照片等模块', 100.0, 'assignment', 'published', NULL, '2026-01-28 23:59:59', '2026-01-10 10:00:00', '2026-01-10 10:00:00'),

(5, 2, 'CSS样式练习', '为HTML页面添加样式，实现响应式布局，适配不同屏幕尺寸', 100.0, 'assignment', 'published', NULL, '2026-02-05 23:59:59', '2026-01-14 10:00:00', '2026-01-14 10:00:00'),

(6, 2, 'JavaScript交互开发', '使用JavaScript实现表单验证、轮播图等交互功能', 100.0, 'project', 'published', NULL, '2026-02-15 23:59:59', '2026-01-18 10:00:00', '2026-01-18 10:00:00'),

-- 数据库原理作业
(7, 3, '数据库设计练习', '设计一个图书管理系统的数据库，包含书籍表、读者表、借阅记录表等', 100.0, 'assignment', 'published', NULL, '2026-01-30 23:59:59', '2026-01-11 10:00:00', '2026-01-11 10:00:00'),

(8, 3, 'SQL查询练习', '编写复杂的SQL查询语句，包括多表连接、子查询、聚合函数等', 100.0, 'assignment', 'published', NULL, '2026-02-08 23:59:59', '2026-01-16 10:00:00', '2026-01-16 10:00:00'),

-- 数据结构与算法作业
(9, 4, '链表实现', '使用Java实现单链表，包括插入、删除、查找等操作', 100.0, 'project', 'published', NULL, '2026-02-12 23:59:59', '2026-01-13 10:00:00', '2026-01-13 10:00:00'),

(10, 4, '排序算法比较', '实现冒泡排序、快速排序、归并排序等算法，并比较性能', 100.0, 'project', 'published', NULL, '2026-02-20 23:59:59', '2026-01-17 10:00:00', '2026-01-17 10:00:00'),

-- Spring框架开发作业
(11, 5, 'Spring IOC练习', '使用Spring的IOC容器实现一个简单的用户管理系统', 100.0, 'project', 'published', NULL, '2026-02-18 23:59:59', '2026-01-19 10:00:00', '2026-01-19 10:00:00'),

(12, 5, 'Spring MVC实战', '使用Spring MVC开发一个RESTful API接口', 100.0, 'project', 'published', NULL, '2026-02-25 23:59:59', '2026-01-19 10:00:00', '2026-01-19 10:00:00')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    content = VALUES(content);

-- 插入作业提交记录（不同状态）
INSERT INTO t_homework_submission (
    homework_id, student_id, content, attachment_url,
    submit_time, score, comment, status, create_time, update_time
) VALUES
-- 已批改的作业（高分）
(1, 1, '已完成所有练习题，包括：
1. 变量声明：int、double、String等类型的变量声明和使用
2. 条件语句：if-else、switch-case的使用
3. 循环语句：for、while、do-while循环的应用
代码已上传至附件。', NULL, '2026-01-18 14:30:00', 95, '完成得很好，代码规范，注释清晰。继续保持！', 'graded', '2026-01-18 14:30:00', '2026-01-19 10:00:00'),

(4, 1, '个人主页HTML代码已完成，包含以下模块：
- 个人信息区：姓名、性别、年龄、专业
- 联系方式区：电话、邮箱、微信
- 个人简介区：自我介绍
- 照片展示区：使用img标签
页面结构清晰，使用了语义化标签。', NULL, '2026-01-20 16:45:00', 88, 'HTML结构清晰，语义化标签使用得当。建议进一步优化页面布局。', 'graded', '2026-01-20 16:45:00', '2026-01-21 09:00:00'),

-- 已提交但未批改的作业
(7, 1, '图书管理系统数据库设计如下：

表结构：
1. books表（书籍表）
   - book_id (主键)
   - title (书名)
   - author (作者)
   - isbn (ISBN号)
   - publish_date (出版日期)
   - price (价格)

2. readers表（读者表）
   - reader_id (主键)
   - name (姓名)
   - phone (电话)
   - email (邮箱)

3. borrow_records表（借阅记录表）
   - record_id (主键)
   - book_id (外键)
   - reader_id (外键)
   - borrow_date (借阅日期)
   - return_date (归还日期)

详细的ER图和SQL建表语句请见附件。', NULL, '2026-01-22 10:20:00', NULL, NULL, 'submitted', '2026-01-22 10:20:00', '2026-01-22 10:20:00'),

(9, 1, '单链表实现完成，包含以下功能：
- Node类：节点类，包含数据和next指针
- LinkedList类：链表类
  - add(data): 在链表尾部添加节点
  - remove(index): 删除指定位置的节点
  - get(index): 获取指定位置的节点
  - size(): 返回链表长度
  - print(): 打印链表所有节点

代码已测试通过，详见附件。', NULL, '2026-01-23 15:10:00', NULL, NULL, 'submitted', '2026-01-23 15:10:00', '2026-01-23 15:10:00');

-- 查看插入的数据摘要
SELECT '=== 课程数据 ===' AS info;
SELECT c.id, c.course_name, c.course_code, cs.progress AS enrollment_progress
FROM t_course c
JOIN t_course_student cs ON c.id = cs.course_id
WHERE cs.student_id = 1;

SELECT '=== 作业数据 ===' AS info;
SELECT h.id, h.title, c.course_name, h.max_score, h.deadline,
       CASE
           WHEN hs.status = 'graded' THEN '已批改'
           WHEN hs.status = 'submitted' THEN '已提交'
           ELSE '待提交'
       END AS submit_status,
       hs.score
FROM t_homework h
LEFT JOIN t_course c ON h.course_id = c.id
LEFT JOIN t_homework_submission hs ON h.id = hs.homework_id AND hs.student_id = 1
ORDER BY h.deadline;

SELECT '=== 提交记录 ===' AS info;
SELECT hs.id, h.title, hs.submit_time, hs.score, hs.status
FROM t_homework_submission hs
JOIN t_homework h ON hs.homework_id = h.id
WHERE hs.student_id = 1
ORDER BY hs.submit_time DESC;
