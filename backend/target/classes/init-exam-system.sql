-- ====================================================
-- 考试系统初始化SQL
-- 包含：表结构、题库数据、技术栈模板、测试数据
-- ====================================================

USE teaching_employment_platform;

-- ====================================================
-- 1. 创建表结构
-- ====================================================

-- 1.1 题库表
CREATE TABLE IF NOT EXISTS `t_question_bank` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `question_type` VARCHAR(20) NOT NULL COMMENT '题型：single_choice-单选 multiple_choice-多选 true_false-判断 short_answer-简答',
  `knowledge_point` VARCHAR(100) NOT NULL COMMENT '知识点分类',
  `difficulty` VARCHAR(20) DEFAULT 'medium' COMMENT '难度：easy-简单 medium-中等 hard-困难',
  `question_text` TEXT NOT NULL COMMENT '题干',
  `options` JSON COMMENT '选项（JSON格式，仅客观题）',
  `correct_answer` TEXT NOT NULL COMMENT '正确答案',
  `analysis` TEXT COMMENT '答案解析',
  `company_id` BIGINT COMMENT '所属企业ID（null表示基础题库）',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-激活 draft-草稿',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_knowledge_point` (`knowledge_point`),
  KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库表';

-- 1.2 试卷表
CREATE TABLE IF NOT EXISTS `t_exam` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `exam_name` VARCHAR(200) NOT NULL COMMENT '考试名称',
  `exam_type` VARCHAR(20) NOT NULL COMMENT '考试类型：course-课程考试 company-企业笔试',
  `ref_id` BIGINT NOT NULL COMMENT '关联ID（课程ID或企业职位ID）',
  `duration` INT NOT NULL COMMENT '考试时长（分钟）',
  `start_time` DATETIME COMMENT '考试开始时间',
  `end_time` DATETIME COMMENT '考试结束时间',
  `pass_score` DECIMAL(5,2) DEFAULT 60.00 COMMENT '及格分数',
  `total_score` DECIMAL(5,2) DEFAULT 100.00 COMMENT '试卷总分',
  `shuffle_questions` TINYINT DEFAULT 1 COMMENT '是否乱序题目：0-否 1-是',
  `shuffle_options` TINYINT DEFAULT 1 COMMENT '是否乱序选项：0-否 1-是',
  `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿 published-已发布 ended-已结束',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam_type` (`exam_type`),
  KEY `idx_ref_id` (`ref_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 1.3 试卷题目关联表
CREATE TABLE IF NOT EXISTS `t_exam_question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `exam_id` BIGINT NOT NULL COMMENT '试卷ID',
  `question_id` BIGINT NOT NULL COMMENT '题目ID',
  `question_score` DECIMAL(5,2) NOT NULL COMMENT '题目分值',
  `sort_order` INT NOT NULL COMMENT '排序',
  `generate_type` VARCHAR(20) DEFAULT 'manual' COMMENT '生成方式：manual-手动 auto-自动',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联表';

-- 1.4 考试记录表
CREATE TABLE IF NOT EXISTS `t_exam_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` BIGINT NOT NULL COMMENT '试卷ID',
  `student_id` BIGINT NOT NULL COMMENT '学生ID',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `submit_time` DATETIME COMMENT '提交时间',
  `objective_score` DECIMAL(5,2) DEFAULT 0.00 COMMENT '客观题得分',
  `subjective_score` DECIMAL(5,2) COMMENT '主观题得分（批改后）',
  `total_score` DECIMAL(5,2) COMMENT '总分',
  `exam_status` VARCHAR(20) DEFAULT 'in_progress' COMMENT '考试状态：in_progress-进行中 submitted-已提交 graded-已批改',
  `question_snapshot` JSON COMMENT '题目快照（保存学生看到的题目顺序）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_exam_status` (`exam_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- 1.5 学生答题表
CREATE TABLE IF NOT EXISTS `t_student_answer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '答题ID',
  `exam_record_id` BIGINT NOT NULL COMMENT '考试记录ID',
  `question_id` BIGINT NOT NULL COMMENT '题目ID',
  `student_answer` TEXT COMMENT '学生答案',
  `score` DECIMAL(5,2) COMMENT '得分',
  `comment` TEXT COMMENT '教师评语',
  `is_correct` TINYINT COMMENT '是否正确：0-错误 1-正确 null-待批改',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam_record_id` (`exam_record_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生答题表';

-- 1.6 技术栈模板表
CREATE TABLE IF NOT EXISTS `t_tech_stack_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `dimensions` JSON NOT NULL COMMENT '技能维度（JSON格式）',
  `position_type` VARCHAR(50) COMMENT '适用岗位类型',
  `is_default` TINYINT DEFAULT 1 COMMENT '是否默认模板：0-否 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术栈模板表';

-- ====================================================
-- 2. 插入技术栈模板
-- ====================================================

INSERT INTO `t_tech_stack_template` (`template_name`, `dimensions`, `position_type`, `is_default`) VALUES
('Java开发工程师', '[{"name":"Java基础","value":8},{"name":"Spring框架","value":7},{"name":"MySQL数据库","value":6},{"name":"Redis缓存","value":5},{"name":"微服务架构","value":6},{"name":"项目经验","value":7}]', 'backend', 1),
('前端开发工程师', '[{"name":"HTML/CSS","value":8},{"name":"JavaScript","value":8},{"name":"Vue框架","value":7},{"name":"工程化工具","value":6},{"name":"性能优化","value":5},{"name":"项目经验","value":6}]', 'frontend', 1),
('Python开发工程师', '[{"name":"Python基础","value":8},{"name":"Django/Flask","value":7},{"name":"数据处理","value":6},{"name":"机器学习","value":5},{"name":"数据库","value":6},{"name":"项目经验","value":6}]', 'backend', 1),
('全栈工程师', '[{"name":"Java/Python","value":6},{"name":"前端开发","value":7},{"name":"数据库","value":6},{"name":"系统设计","value":5},{"name":"DevOps","value":5},{"name":"项目经验","value":7}]', 'fullstack', 1),
('数据分析工程师', '[{"name":"Python","value":8},{"name":"SQL","value":8},{"name":"数据可视化","value":7},{"name":"统计分析","value":6},{"name":"机器学习","value":5},{"name":"业务理解","value":6}]', 'data', 1);

-- ====================================================
-- 3. 插入题库数据
-- ====================================================

-- 3.1 Java基础题库
INSERT INTO `t_question_bank` (`question_type`, `knowledge_point`, `difficulty`, `question_text`, `options`, `correct_answer`, `analysis`, `company_id`) VALUES
-- 单选题
('single_choice', 'Java基础', 'easy', 'Java中基本数据类型int占用多少字节？',
 '["A. 2字节", "B. 4字节", "C. 8字节", "D. 1字节"]',
 'B', 'Java中int类型占用4个字节，共32位。', NULL),

('single_choice', 'Java基础', 'easy', '以下哪个是Java的合法标识符？',
 '["A. 2variable", "B. class", "C. _name", "D. java-name"]',
 'C', '标识符可以包含字母、数字、下划线和美元符号，但不能以数字开头，不能是关键字。', NULL),

('single_choice', 'Java基础', 'medium', '关于Java中的String，下列说法正确的是：',
 '["A. String是基本数据类型", "B. String是不可变的", "C. String可以直接用==比较内容", "D. String的长度可以修改"]',
 'B', 'String是引用类型，不可变对象，应该用equals()比较内容。', NULL),

('single_choice', '面向对象', 'medium', '关于Java继承，下列说法错误的是：',
 '["A. Java只支持单继承", "B. 子类可以继承父类的所有非私有成员", "C. 构造方法不能被继承", "D. final类可以被继承"]',
 'D', 'final类不能被继承，这是final关键字的作用之一。', NULL),

-- 多选题
('multiple_choice', 'Java基础', 'medium', 'Java中哪些关键字用于异常处理？',
 '["A. try", "B. catch", "C. finally", "D. throw"]',
 'A,B,C,D', 'try-catch-finally是异常处理的基本结构，throw用于抛出异常。', NULL),

('multiple_choice', '集合框架', 'medium', '关于ArrayList和LinkedList的区别，以下说法正确的是：',
 '["A. ArrayList基于数组实现", "B. LinkedList基于链表实现", "C. ArrayList查询效率高", "D. LinkedList插入删除效率高"]',
 'A,B,C,D', 'ArrayList底层是数组，查询快但增删慢；LinkedList底层是链表，增删快但查询慢。', NULL),

-- 判断题
('true_false', 'Java基础', 'easy', 'Java中接口可以被多重继承。',
 NULL, 'true', '一个接口可以继承多个父接口，这是Java接口的重要特性。', NULL),

('true_false', '面向对象', 'medium', '抽象类可以有构造方法。',
 NULL, 'true', '抽象类可以有构造方法，主要用于子类初始化时调用。', NULL),

-- 简答题
('short_answer', 'Java基础', 'medium', '请简述Java中==和equals()的区别。',
 NULL, '==是比较运算符，用于比较基本数据类型的值或引用类型的内存地址；equals()是Object类的方法，用于比较对象的内容。String类重写了equals()方法，用于比较字符串内容。',
 '==比较引用或基本类型值，equals()比较对象内容（需重写）', NULL),

('short_answer', '面向对象', 'hard', '请简述Java中重载(Overload)和重写(Override)的区别。',
 NULL, '重载：同一个类中方法名相同但参数列表不同（参数个数、类型、顺序）。重写：子类重新定义父类的方法，方法签名必须完全一致，访问修饰符不能更严格。',
 '重载是同一个类的多态性，重写是子类对父类的重新实现', NULL),

('short_answer', '集合框架', 'hard', '请简述HashMap的实现原理。',
 NULL, 'HashMap基于数组+链表/红黑树实现。通过key的hash值计算数组下标，相同hash值的用链表存储（链地址法）。JDK8后，链表长度超过8时转为红黑树以提高查询效率。',
 '数组+链表+红黑树，通过hash值定位，冲突用链地址法', NULL);

-- 3.2 Web前端题库
INSERT INTO `t_question_bank` (`question_type`, `knowledge_point`, `difficulty`, `question_text`, `options`, `correct_answer`, `analysis`, `company_id`) VALUES
-- 单选题
('single_choice', 'HTML/CSS', 'easy', 'HTML中用于创建超链接的标签是？',
 '["A. <link>", "B. <a>", "C. <href>", "D. <url>"]',
 'B', '<a>标签用于创建超链接，href属性指定链接地址。', NULL),

('single_choice', 'JavaScript', 'medium', 'JavaScript中声明常量的关键字是？',
 '["A. var", "B. let", "C. const", "D. final"]',
 'C', 'const用于声明常量，声明后不可重新赋值。', NULL),

('single_choice', 'Vue基础', 'medium', 'Vue中用于双向数据绑定的指令是？',
 '["A. v-bind", "B. v-model", "C. v-on", "D. v-if"]',
 'B', 'v-model用于表单元素的双向数据绑定。', NULL),

-- 多选题
('multiple_choice', 'CSS', 'medium', 'CSS中三种定位方式包括：',
 '["A. relative", "B. absolute", "C. fixed", "D. static"]',
 'A,B,C', 'static是默认定位方式，relative、absolute、fixed是常用定位方式。', NULL),

-- 判断题
('true_false', 'JavaScript', 'easy', 'JavaScript是Java的脚本版本。',
 NULL, 'false', 'JavaScript和Java是完全不同的两种语言，只是名称相似。', NULL),

-- 简答题
('short_answer', 'Vue基础', 'medium', '请简述Vue的生命周期钩子函数。',
 NULL, '主要钩子：beforeCreate（创建前）、created（创建完成）、beforeMount（挂载前）、mounted（挂载完成）、beforeUpdate（更新前）、updated（更新完成）、beforeDestroy（销毁前）、destroyed（销毁完成）。',
 '从创建到销毁的完整生命周期，每个阶段有对应的钩子函数', NULL);

-- 3.3 数据库题库
INSERT INTO `t_question_bank` (`question_type`, `knowledge_point`, `difficulty`, `question_text`, `options`, `correct_answer`, `analysis`, `company_id`) VALUES
-- 单选题
('single_choice', 'SQL基础', 'easy', 'SQL中用于查询数据的关键字是？',
 '["A. GET", "B. SELECT", "C. QUERY", "D. FIND"]',
 'B', 'SELECT是SQL中用于查询数据的关键字。', NULL),

('single_choice', 'MySQL', 'medium', 'MySQL中用于限制查询结果数量的关键字是？',
 '["A. TOP", "B. LIMIT", "C. ROWCOUNT", "D. MAX"]',
 'B', 'MySQL使用LIMIT限制结果数量，如LIMIT 10表示返回前10条记录。', NULL),

-- 多选题
('multiple_choice', '数据库理论', 'medium', '数据库事务的ACID特性包括：',
 '["A. 原子性", "B. 一致性", "C. 隔离性", "D. 持久性"]',
 'A,B,C,D', 'ACID是事务的四个基本特性：原子性、一致性、隔离性、持久性。', NULL),

-- 判断题
('true_false', '索引优化', 'medium', '索引越多，查询性能越好。',
 NULL, 'false', '索引可以提高查询性能，但会降低增删改性能，需要根据实际查询需求合理创建索引。', NULL),

-- 简答题
('short_answer', 'MySQL', 'hard', '请简述MySQL中InnoDB和MyISAM的区别。',
 NULL, '主要区别：1）事务支持：InnoDB支持事务，MyISAM不支持；2）锁机制：InnoDB支持行锁，MyISAM只支持表锁；3）外键：InnoDB支持外键，MyISAM不支持；4）崩溃恢复：InnoDB有崩溃恢复能力，MyISAM没有。',
 'InnoDB支持事务和行锁，更适合高并发；MyISAM表锁，适合读多写少', NULL);

-- 3.4 Spring框架题库
INSERT INTO `t_question_bank` (`question_type`, `knowledge_point`, `difficulty`, `question_text`, `options`, `correct_answer`, `analysis`, `company_id`) VALUES
-- 单选题
('single_choice', 'Spring核心', 'easy', 'Spring的核心功能是？',
 '["A. MVC框架", "B. IoC和AOP", "C. ORM框架", "D. 数据库连接池"]',
 'B', 'IoC（控制反转）和AOP（面向切面编程）是Spring的两大核心功能。', NULL),

('single_choice', 'Spring Boot', 'medium', 'Spring Boot用于简化Spring应用的？',
 '["A. 测试", "B. 开发和配置", "C. 部署", "D. 监控"]',
 'B', 'Spring Boot通过自动配置和约定优于配置，大大简化了Spring应用的开发和配置。', NULL),

-- 多选题
('multiple_choice', 'Spring MVC', 'medium', 'Spring MVC的主要组件包括：',
 '["A. DispatcherServlet", "B. Controller", "C. ViewResolver", "D. HandlerMapping"]',
 'A,B,C,D', 'Spring MVC的核心组件包括前端控制器、处理器、视图解析器、处理器映射器等。', NULL),

-- 判断题
('true_false', 'Spring核心', 'medium', '@Autowired默认按byType方式进行自动装配。',
 NULL, 'true', '@Autowired默认按类型装配，如果有多个类型匹配则按名称装配。', NULL),

-- 简答题
('short_answer', 'Spring核心', 'hard', '请简述IoC（控制反转）和DI（依赖注入）的概念和区别。',
 NULL, 'IoC（Inversion of Control）是一种设计思想，将对象的创建和管理交给Spring容器；DI（Dependency Injection）是IoC的实现方式，通过构造方法、setter方法等注入依赖。IoC是目标，DI是手段。',
 'IoC是设计思想，DI是IoC的具体实现方式', NULL);

-- ====================================================
-- 4. 创建测试数据
-- ====================================================

-- 4.1 为现有课程添加技术栈
UPDATE t_position SET
  `capability_radar` = '[{"name":"Java基础","value":8},{"name":"Spring框架","value":7},{"name":"MySQL数据库","value":6},{"name":"Redis缓存","value":5},{"name":"微服务架构","value":6}]'
WHERE id = 1;

UPDATE t_position SET
  `capability_radar` = '[{"name":"HTML/CSS","value":8},{"name":"JavaScript","value":8},{"name":"Vue框架","value":7},{"name":"工程化工具","value":6}]'
WHERE id = 2;

-- 4.2 创建示例试卷（课程考试）
INSERT INTO `t_exam` (`exam_name`, `exam_type`, `ref_id`, `duration`, `start_time`, `end_time`, `pass_score`, `total_score`, `status`) VALUES
('Java编程语言期末考试', 'course', 1, 90,
 DATE_ADD(NOW(), INTERVAL 1 DAY),
 DATE_ADD(NOW(), INTERVAL 7 DAY), 60.00, 100.00, 'published'),
('Web前端开发期中考试', 'course', 2, 60,
 DATE_ADD(NOW(), INTERVAL 1 DAY),
 DATE_ADD(NOW(), INTERVAL 5 DAY), 60.00, 100.00, 'published');

-- 4.3 为示例试卷添加题目
-- Java考试（使用前5道Java题）
SET @exam_id = LAST_INSERT_ID();
INSERT INTO `t_exam_question` (`exam_id`, `question_id`, `question_score`, `sort_order`)
SELECT
  (SELECT id FROM t_exam WHERE exam_name = 'Java编程语言期末考试'),
  id,
  CASE question_type
    WHEN 'single_choice' THEN 10
    WHEN 'multiple_choice' THEN 15
    WHEN 'true_false' THEN 5
    WHEN 'short_answer' THEN 20
  END,
  @row := @row + 1
FROM t_question_bank, (SELECT @row := 0) r
WHERE knowledge_point IN ('Java基础', '面向对象', '集合框架')
LIMIT 10;

-- Web前端考试（使用前5道前端题）
INSERT INTO `t_exam_question` (`exam_id`, `question_id`, `question_score`, `sort_order`)
SELECT
  (SELECT id FROM t_exam WHERE exam_name = 'Web前端开发期中考试'),
  id,
  CASE question_type
    WHEN 'single_choice' THEN 10
    WHEN 'multiple_choice' THEN 15
    WHEN 'true_false' THEN 5
    WHEN 'short_answer' THEN 20
  END,
  @row := @row + 1
FROM t_question_bank, (SELECT @row := 0) r
WHERE knowledge_point IN ('HTML/CSS', 'JavaScript', 'Vue基础')
LIMIT 8;

-- ====================================================
-- 5. 查看初始化结果
-- ====================================================

SELECT '题库统计' as '统计项';
SELECT
  question_type as '题型',
  knowledge_point as '知识点',
  COUNT(*) as '题目数量'
FROM t_question_bank
GROUP BY question_type, knowledge_point
ORDER BY knowledge_point, question_type;

SELECT '技术栈模板' as '统计项';
SELECT id, template_name as '模板名称', position_type as '适用岗位'
FROM t_tech_stack_template;

SELECT '试卷列表' as '统计项';
SELECT id, exam_name as '考试名称', exam_type as '类型', duration as '时长(分钟)', status as '状态'
FROM t_exam;
