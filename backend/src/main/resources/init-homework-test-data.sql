-- =============================================
-- 作业管理系统测试数据初始化脚本
-- =============================================
-- 功能:
-- 1. 为教师1(teacher01)创建多个作业
-- 2. 为学生33(student01)创建作业提交记录
-- 3. 覆盖各种状态: 已发布、已截止、已提交、已批改、未提交等
-- =============================================

USE teaching_employment_platform;

-- 清理现有测试数据(可选,如果需要重新初始化)
-- DELETE FROM t_homework_submission WHERE student_id = 33;
-- DELETE FROM t_homework WHERE teacher_id = 1 AND id >= 100;

-- =============================================
-- 1. 为教师1的课程创建作业
-- =============================================

-- 课程2: Java Programming - 作业1 (已发布,已提交,已批改)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (100, 2, 1, 'Java基础语法练习',
'请完成以下Java基础练习:
1. 变量声明与数据类型
2. 条件语句与循环
3. 数组操作
4. 方法定义与调用

要求: 代码规范,添加注释,包含测试用例。',
'assignment', '2026-01-15 23:59:00', 100, 'published', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 课程2: Java Programming - 作业2 (已发布,已提交,待批改)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (101, 2, 1, 'Java面向对象编程',
'实现一个简单的学生管理系统:
1. 创建Student类(包含姓名、学号、成绩等属性)
2. 实现构造方法、getter/setter
3. 重写toString()和equals()方法
4. 创建StudentManager类实现增删改查功能

提交要求: 完整的Java源代码,包含README文档',
'assignment', '2026-01-20 23:59:00', 100, 'published', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 课程2: Java Programming - 作业3 (已发布,未提交,即将截止)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (102, 2, 1, 'Java集合框架应用',
'使用Java集合框架实现以下功能:
1. 使用ArrayList存储学生信息
2. 使用HashMap实现按学号快速查找
3. 使用TreeSet进行排序
4. 使用LinkedList实现队列操作

提交要求: 源代码 + 运行截图',
'assignment', DATE_ADD(NOW(), INTERVAL 3 DAY), 100, 'published', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 课程2: Java Programming - 作业4 (已发布,未提交,截止日期较远)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (103, 2, 1, 'Java IO流与文件操作',
'实现一个文件管理工具:
1. 文本文件的读写
2. 文件夹遍历
3. 文件复制与移动
4. 序列化与反序列化

扩展功能: 支持大文件分块传输',
'assignment', '2026-02-15 23:59:00', 100, 'published', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 课程3: Web Development - 作业1 (已发布,未提交)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (104, 3, 1, 'HTML&CSS静态页面制作',
'使用HTML和CSS制作个人主页:
1. HTML语义化标签
2. CSS布局(Flexbox/Grid)
3. 响应式设计
4. 动画效果

技术要求: 纯HTML+CSS,不使用框架',
'assignment', DATE_ADD(NOW(), INTERVAL 5 DAY), 100, 'published', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 课程3: Web Development - 作业2 (已发布,已提交,已批改)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (105, 3, 1, 'JavaScript交互开发',
'实现一个待办事项管理应用:
1. 添加、删除、编辑任务
2. 任务状态切换
3. 数据持久化(localStorage)
4. 动画过渡效果

技术要求: 原生JavaScript,不使用框架',
'assignment', '2026-01-18 23:59:00', 100, 'published', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 课程4: 数据库 - 作业1 (已截止,已提交,已批改)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (106, 4, 1, 'SQL查询练习',
'编写SQL查询语句完成以下任务:
1. 单表查询、条件查询、排序
2. 多表连接查询
3. 分组与聚合函数
4. 子查询

数据库: 使用提供的school_db数据库',
'assignment', '2026-01-12 23:59:00', 100, 'closed', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 课程4: 数据库 - 作业2 (已发布,未提交)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (107, 4, 1, '数据库设计项目',
'设计一个图书馆管理系统数据库:
1. 需求分析与ER图
2. 表结构设计(至少5张表)
3. 索引优化
4. 视图与存储过程

提交要求: 设计文档 + SQL脚本',
'project', '2026-01-30 23:59:00', 150, 'published', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 课程14: Java高级 - 作业1 (草稿状态,未发布)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (108, 14, 1, '多线程编程实战',
'实现生产者-消费者模型:
1. 使用wait/notify实现
2. 使用Lock/Condition实现
3. 使用BlockingQueue实现
4. 性能对比分析

注意: 本作业尚未发布,等待课程进度',
'assignment', '2026-02-20 23:59:00', 100, 'draft', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 课程15: 算法与数据结构 - 作业1 (已发布,已提交,已批改)
INSERT INTO t_homework (id, course_id, teacher_id, title, description, homework_type, deadline, max_score, status, create_time, update_time)
VALUES (109, 15, 1, '排序算法实现与对比',
'实现并对比以下排序算法:
1. 冒泡排序、选择排序、插入排序
2. 快速排序、归并排序、堆排序
3. 时间复杂度测试与对比
4. 空间复杂度分析

提交要求: 源代码 + 测试报告 + 可视化图表',
'assignment', '2026-01-14 23:59:00', 100, 'published', NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- =============================================
-- 2. 为学生33创建作业提交记录
-- =============================================

-- 作业100: Java基础语法练习 (已提交,已批改)
INSERT INTO t_homework_submission (homework_id, student_id, content, attachment_url, score, comment, status, submit_date, grade_time, create_time, update_time)
VALUES (100, 33,
'完成了所有练习题,代码规范,注释清晰。
1. 实现了整数、浮点数、字符串等多种数据类型的变量声明
2. 使用if-else和switch完成了条件判断练习
3. 实现了for、while、do-while循环
4. 创建了多个方法,包括带参数和不带参数的方法

附件包含完整的Java源代码文件。',
'/uploads/homework/submissions/student33/hw100_java_basic.zip',
85.5,
'完成得很好!代码结构清晰,注释完整。
建议:
1. 可以增加更多边界条件的测试
2. 部分方法可以进一步优化
3. 建议学习单元测试框架JUnit',
'graded',
'2026-01-10 14:30:00',
'2026-01-11 10:15:00',
NOW(), NOW())
ON DUPLICATE KEY UPDATE score=VALUES(score);

-- 作业101: Java面向对象编程 (已提交,待批改)
INSERT INTO t_homework_submission (homework_id, student_id, content, attachment_url, score, comment, status, submit_date, create_time, update_time)
VALUES (101, 33,
'实现了学生管理系统,包含Student类和StudentManager类。
主要功能:
1. Student类包含name、studentId、score等属性
2. 实现了构造方法和所有getter/setter
3. 重写了toString()、equals()和hashCode()
4. StudentManager实现了增删改查功能,使用ArrayList存储
5. 添加了数据验证和异常处理

附件: 完整的源代码和README文档',
'/uploads/homework/submissions/student33/hw101_oop.zip',
NULL, NULL, 'submitted',
'2026-01-13 16:45:00',
NOW(), NOW())
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 作业102: Java集合框架应用 (未提交)
-- (不插入记录,表示未提交)

-- 作业105: JavaScript交互开发 (已提交,已批改)
INSERT INTO t_homework_submission (homework_id, student_id, content, attachment_url, score, comment, status, submit_date, grade_time, create_time, update_time)
VALUES (105, 33,
'实现了一个功能完整的待办事项管理应用。
功能特性:
1. 添加任务: 支持任务标题和描述
2. 删除任务: 单个删除和批量删除
3. 编辑任务: 点击编辑按钮修改任务内容
4. 状态切换: 点击完成任务
5. 数据持久化: 使用localStorage保存数据
6. 动画效果: 添加了流畅的过渡动画

技术亮点:
- 使用模块化的JavaScript代码结构
- 实现了事件委托优化性能
- 响应式设计,支持移动端
- 添加了键盘快捷键支持

附件: HTML文件,包含所有CSS和JavaScript代码',
'/uploads/homework/submissions/student33/hw105_js_todo.html',
92.0,
'优秀的作品!功能完整,代码规范,用户体验良好。
亮点:
1. 代码结构清晰,使用了现代JavaScript语法
2. 动画效果流畅,提升了用户体验
3. 响应式设计做得很好
4. 键盘快捷键是个很好的创新

建议:
1. 可以添加任务分类功能
2. 考虑添加拖拽排序功能
3. 可以添加主题切换(暗色模式)',
'graded',
'2026-01-12 20:30:00',
'2026-01-13 09:20:00',
NOW(), NOW())
ON DUPLICATE KEY UPDATE score=VALUES(score);

-- 作业106: SQL查询练习 (已提交,已批改,逾期提交)
INSERT INTO t_homework_submission (homework_id, student_id, content, attachment_url, score, comment, status, submit_date, grade_time, create_time, update_time)
VALUES (106, 33,
'完成了所有SQL查询练习。
内容包括:
1. 单表查询: SELECT、WHERE、ORDER BY、LIMIT
2. 多表查询: INNER JOIN、LEFT JOIN、RIGHT JOIN
3. 分组查询: GROUP BY、HAVING
4. 子查询: 标量子查询、列子查询、行子查询

所有查询语句都经过测试,结果正确。
附件: SQL脚本文件和查询结果截图',
'/uploads/homework/submissions/student33/hw106_sql_queries.zip',
78.0,
'基本完成要求,查询语句正确。
注意: 本次作业逾期提交,已扣减5分。
建议:
1. 部分复杂查询可以进一步优化
2. 建议学习查询执行计划分析
3. 可以尝试使用窗口函数',
'graded',
'2026-01-13 22:00:00',
'2026-01-14 11:30:00',
NOW(), NOW())
ON DUPLICATE KEY UPDATE score=VALUES(score);

-- 作业109: 排序算法实现与对比 (已提交,已批改)
INSERT INTO t_homework_submission (homework_id, student_id, content, attachment_url, score, comment, status, submit_date, grade_time, create_time, update_time)
VALUES (109, 33,
'实现了6种排序算法,并进行了详细的性能对比。
实现内容:
1. 冒泡排序、选择排序、插入排序(基础算法)
2. 快速排序、归并排序、堆排序(高级算法)
3. 对每种算法进行了时间复杂度测试
4. 生成了性能对比图表
5. 分析了各算法的适用场景

测试环境: Intel i5, 8GB RAM
测试数据: 1000/10000/100000个随机整数

附件: 源代码、测试报告、性能图表、README文档',
'/uploads/homework/submissions/student33/hw109_sorting.zip',
95.0,
'非常优秀!算法实现正确,测试报告详尽,图表制作精美。
特别值得表扬:
1. 代码质量高,注释清晰
2. 测试用例设计合理,覆盖面广
3. 性能分析深入,数据可视化做得很好
4. 文档完整,可读性强

建议:
1. 可以尝试优化快速排序的pivot选择策略
2. 可以添加更多算法(如希尔排序、基数排序)
3. 考虑使用性能分析工具进行更精确的测试',
'graded',
'2026-01-11 18:20:00',
'2026-01-12 14:00:00',
NOW(), NOW())
ON DUPLICATE KEY UPDATE score=VALUES(score);

-- =============================================
-- 3. 为其他学生添加部分提交记录(模拟真实场景)
-- =============================================

-- 为多个学生添加作业100的提交记录
INSERT INTO t_homework_submission (homework_id, student_id, content, attachment_url, score, comment, status, submit_date, grade_time, create_time, update_time)
VALUES
(100, 1, '完成了Java基础练习,代码规范。', '/uploads/hw100_s1.zip', 88.0, '完成得很好!', 'graded', '2026-01-09 10:00:00', '2026-01-10 09:00:00', NOW(), NOW()),
(100, 2, 'Java基础练习完成。', '/uploads/hw100_s2.zip', 76.5, '基本完成,需要加强练习。', 'graded', '2026-01-09 15:30:00', '2026-01-10 14:00:00', NOW(), NOW()),
(100, 3, '作业内容详见附件。', '/uploads/hw100_s3.zip', 92.0, '优秀!', 'graded', '2026-01-08 14:20:00', '2026-01-09 10:30:00', NOW(), NOW()),
(100, 4, '完成了所有练习题。', '/uploads/hw100_s4.zip', 81.0, '完成得不错,继续努力。', 'graded', '2026-01-10 11:00:00', '2026-01-11 09:30:00', NOW(), NOW()),
(100, 5, 'Java基础作业。', '/uploads/hw100_s5.zip', 69.0, '需要加强基础练习。', 'graded', '2026-01-10 16:45:00', '2026-01-11 15:00:00', NOW(), NOW())
ON DUPLICATE KEY UPDATE score=VALUES(score);

-- 为多个学生添加作业101的提交记录(部分已批改,部分待批改)
INSERT INTO t_homework_submission (homework_id, student_id, content, attachment_url, score, comment, status, submit_date, grade_time, create_time, update_time)
VALUES
(101, 1, '实现了面向对象编程作业。', '/uploads/hw101_s1.zip', 90.0, '设计合理,代码规范!', 'graded', '2026-01-12 10:00:00', '2026-01-13 09:00:00', NOW(), NOW()),
(101, 2, '面向对象作业完成。', '/uploads/hw101_s2.zip', NULL, NULL, 'submitted', '2026-01-13 15:30:00', NULL, NOW(), NOW()),
(101, 3, 'Student类和Manager类实现完成。', '/uploads/hw101_s3.zip', NULL, NULL, 'submitted', '2026-01-14 14:20:00', NULL, NOW(), NOW()),
(101, 4, 'OOP作业内容。', '/uploads/hw101_s4.zip', 85.0, '完成得不错,继续加油!', 'graded', '2026-01-12 11:00:00', '2026-01-13 10:30:00', NOW(), NOW()),
(101, 5, '完成了面向对象练习。', '/uploads/hw101_s5.zip', NULL, NULL, 'submitted', '2026-01-13 16:45:00', NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- =============================================
-- 4. 验证数据
-- =============================================

SELECT '========================================' AS '';
SELECT '作业初始化完成!' AS '';
SELECT '========================================' AS '';

SELECT '教师1的作业列表:' AS '';
SELECT
    h.id AS '作业ID',
    c.course_name AS '课程名称',
    h.title AS '作业标题',
    h.status AS '状态',
    h.deadline AS '截止时间',
    h.max_score AS '满分',
    (SELECT COUNT(*) FROM t_homework_submission WHERE homework_id = h.id) AS '提交数'
FROM t_homework h
LEFT JOIN t_course c ON h.course_id = c.id
WHERE h.teacher_id = 1 AND h.id >= 100
ORDER BY h.id;

SELECT '' AS '';
SELECT '学生33的作业提交记录:' AS '';
SELECT
    h.id AS '作业ID',
    h.title AS '作业标题',
    hs.status AS '提交状态',
    hs.score AS '分数',
    hs.submit_date AS '提交时间',
    hs.grade_time AS '批改时间'
FROM t_homework h
LEFT JOIN t_homework_submission hs ON h.id = hs.homework_id AND hs.student_id = 33
WHERE h.teacher_id = 1 AND h.id >= 100
ORDER BY h.id;

SELECT '' AS '';
SELECT '统计信息:' AS '';
SELECT
    COUNT(DISTINCT h.id) AS '总作业数',
    (SELECT COUNT(*) FROM t_homework_submission WHERE homework_id IN (SELECT id FROM t_homework WHERE teacher_id = 1 AND id >= 100)) AS '总提交数',
    (SELECT COUNT(*) FROM t_homework_submission WHERE homework_id IN (SELECT id FROM t_homework WHERE teacher_id = 1 AND id >= 100) AND status = 'submitted') AS '待批改数',
    (SELECT COUNT(*) FROM t_homework_submission WHERE homework_id IN (SELECT id FROM t_homework WHERE teacher_id = 1 AND id >= 100) AND status = 'graded') AS '已批改数'
FROM t_homework
WHERE teacher_id = 1 AND id >= 100;

SELECT '========================================' AS '';
SELECT '测试数据初始化完成!' AS '成功';
SELECT '========================================' AS '';
