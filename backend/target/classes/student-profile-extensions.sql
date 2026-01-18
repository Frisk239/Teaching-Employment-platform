-- 学员端功能扩展 - 数据库表结构
-- 创建日期: 2025-01-18
-- 说明: 添加学员个人中心所需的表结构

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 1. 创建教育经历表
CREATE TABLE IF NOT EXISTS t_education (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  school_name VARCHAR(100) NOT NULL COMMENT '学校名称',
  major VARCHAR(100) NOT NULL COMMENT '专业',
  degree VARCHAR(20) NOT NULL COMMENT '学历(本科/硕士/博士/专科)',
  start_date DATE NOT NULL COMMENT '开始时间',
  end_date DATE COMMENT '结束时间(空表示至今)',
  description TEXT COMMENT '描述/备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教育经历表';

-- 2. 创建实习经历表
CREATE TABLE IF NOT EXISTS t_internship (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  company_name VARCHAR(100) NOT NULL COMMENT '公司名称',
  position VARCHAR(100) NOT NULL COMMENT '职位',
  department VARCHAR(100) COMMENT '部门',
  start_date DATE NOT NULL COMMENT '开始时间',
  end_date DATE COMMENT '结束时间(空表示至今)',
  description TEXT COMMENT '工作描述/职责',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_user_id (user_id),
  FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实习经历表';

-- 3. 扩展用户表 - 添加学号和简历URL字段
ALTER TABLE t_user
ADD COLUMN student_number VARCHAR(50) COMMENT '学号',
ADD COLUMN resume_url VARCHAR(255) COMMENT '简历文件URL';

-- 4. 扩展课程表 - 添加周次字段(支持每周不同的课程表)
ALTER TABLE t_timetable
ADD COLUMN week_number INT DEFAULT 1 COMMENT '周次';

-- 5. 插入示例教育经历数据
INSERT INTO t_education (user_id, school_name, major, degree, start_date, end_date, description) VALUES
-- 为student1(张三, user_id=1)添加教育经历
(1, '清华大学', '计算机科学与技术', '本科', '2021-09-01', '2025-06-30', 'GPA: 3.8/4.0，主修计算机基础课程'),
(1, '北京四中', '理科', '高中', '2018-09-01', '2021-06-30', '理科班，多次获得校级奖学金'),

-- 为student2(李四, user_id=2)添加教育经历
(2, '北京大学', '软件工程', '本科', '2021-09-01', '2025-06-30', 'GPA: 3.6/4.0，擅长前端开发'),

-- 为student3(王五, user_id=3)添加教育经历
(3, '浙江大学', '计算机科学与技术', '本科', '2021-09-01', '2025-06-30', '主修人工智能方向，参与过多个项目'),

-- 为student5(钱七, user_id=5)添加教育经历
(5, '上海交通大学', '人工智能', '硕士', '2021-09-01', '2024-06-30', '研究方向:机器学习，发表SCI论文1篇'),
(5, '复旦大学', '数学与应用数学', '本科', '2017-09-01', '2021-06-30', '数学基础扎实'),

-- 为student9(郑一, user_id=9)添加教育经历
(9, '南京大学', '数据科学', '本科', '2021-09-01', '2025-06-30', '专注于大数据分析和可视化');

-- 6. 插入示例实习经历数据
INSERT INTO t_internship (user_id, company_name, position, department, start_date, end_date, description) VALUES
-- 为student1(张三)添加实习经历
(1, '阿里巴巴', '前端开发实习生', '淘宝事业部', '2024-07-01', '2024-09-30', '负责Vue组件开发，参与重构项目'),
(1, '字节跳动', '后端开发实习生', '今日头条', '2023-07-01', '2023-09-30', '使用Go开发后端API接口'),

-- 为student2(李四)添加实习经历
(2, '腾讯', '前端开发实习生', '微信事业群', '2024-07-01', '2024-10-31', '负责小程序开发，优化用户体验'),

-- 为student3(王五)添加实习经历
(3, '华为', '算法工程师实习生', '2012实验室', '2024-06-01', '2024-09-30', '参与推荐算法优化项目'),

-- 为student5(钱七)添加实习经历
(5, '美团', '数据分析师实习生', '商业分析部', '2023-07-01', '2023-12-31', '负责用户行为数据分析，产出报告'),

-- 为student9(郑一)添加实习经历
(9, '京东', '数据开发工程师实习生', '京东科技', '2024-07-01', '2024-09-30', '参与数据仓库建设，使用Spark和Hive');

-- 7. 为用户表更新学号(从t_student表同步)
UPDATE t_user u
INNER JOIN t_student s ON u.id = s.user_id
SET u.student_number = s.student_no
WHERE s.user_id IS NOT NULL;

-- 8. 添加索引以优化查询性能
CREATE INDEX idx_education_user_dates ON t_education(user_id, start_date DESC);
CREATE INDEX idx_internship_user_dates ON t_internship(user_id, start_date DESC);

-- 9. 为不同周次创建课程表数据(示例)
-- 这里只是示例，实际数据应根据具体需求生成
-- INSERT INTO t_timetable (week_number, ...) VALUES ...
