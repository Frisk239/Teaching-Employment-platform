-- 学员个人中心完整扩展 - 数据库表结构
-- 创建日期: 2026-01-19
-- 说明: 完善学员个人中心所需的所有表结构

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 1. 扩展学员表 - 添加缺失字段
ALTER TABLE t_student
ADD COLUMN qq VARCHAR(20) COMMENT 'QQ号码',
ADD COLUMN wechat VARCHAR(50) COMMENT '微信号',
ADD COLUMN health_status VARCHAR(20) DEFAULT '健康' COMMENT '健康状况',
ADD COLUMN bio TEXT COMMENT '个人简介';

-- 2. 创建技能标签表
CREATE TABLE IF NOT EXISTS t_student_skill (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  skill_name VARCHAR(100) NOT NULL COMMENT '技能名称',
  skill_level VARCHAR(20) NOT NULL COMMENT '熟练程度(了解/入门/熟悉/掌握/精通)',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_student_id (student_id),
  FOREIGN KEY (student_id) REFERENCES t_student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员技能标签表';

-- 3. 创建项目经验表
CREATE TABLE IF NOT EXISTS t_student_project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  project_name VARCHAR(200) NOT NULL COMMENT '项目名称',
  role VARCHAR(100) COMMENT '担任角色',
  start_date DATE COMMENT '开始时间',
  end_date DATE COMMENT '结束时间',
  description TEXT COMMENT '项目描述',
  technologies TEXT COMMENT '使用技术(多个技术用逗号分隔)',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_student_id (student_id),
  FOREIGN KEY (student_id) REFERENCES t_student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员项目经验表';

-- 4. 创建求职偏好表
CREATE TABLE IF NOT EXISTS t_student_preference (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  expected_cities TEXT COMMENT '期望城市(JSON数组)',
  expected_positions TEXT COMMENT '期望职位(JSON数组)',
  salary_min DECIMAL(10,2) COMMENT '期望最低薪资',
  salary_max DECIMAL(10,2) COMMENT '期望最高薪资',
  salary_unit VARCHAR(10) DEFAULT 'month' COMMENT '薪资单位(month/year)',
  job_types VARCHAR(100) COMMENT '工作性质(多个用逗号分隔:全职,实习,兼职)',
  company_sizes VARCHAR(100) COMMENT '企业规模偏好(多个用逗号分隔)',
  seeking_status VARCHAR(20) DEFAULT 'actively' COMMENT '求职状态(actively/passively/not_seeking/employed)',
  self_evaluation TEXT COMMENT '自我评价',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_student_id (student_id),
  FOREIGN KEY (student_id) REFERENCES t_student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员求职偏好表';

-- 5. 创建简历表
CREATE TABLE IF NOT EXISTS t_student_resume (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  resume_name VARCHAR(200) NOT NULL COMMENT '简历名称',
  file_url VARCHAR(500) NOT NULL COMMENT '文件URL',
  file_size VARCHAR(20) COMMENT '文件大小',
  upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  INDEX idx_student_id (student_id),
  FOREIGN KEY (student_id) REFERENCES t_student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员简历表';

-- 6. 创建课程成绩表
CREATE TABLE IF NOT EXISTS t_student_course (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  course_name VARCHAR(200) NOT NULL COMMENT '课程名称',
  score DECIMAL(5,2) COMMENT '成绩',
  semester VARCHAR(20) COMMENT '学期',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_student_id (student_id),
  FOREIGN KEY (student_id) REFERENCES t_student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员课程成绩表';

-- 7. 删除学员表的is_deleted字段(改为物理删除)
ALTER TABLE t_student DROP COLUMN is_deleted;

-- 8. 插入测试数据(为张三添加，假设张三的student_id = 1)
INSERT INTO t_student_skill (student_id, skill_name, skill_level) VALUES
(1, 'Java', '掌握'),
(1, 'Vue.js', '熟悉'),
(1, 'MySQL', '掌握'),
(1, 'Python', '入门')
ON DUPLICATE KEY UPDATE skill_name=VALUES(skill_name);

INSERT INTO t_student_project (student_id, project_name, role, start_date, end_date, description, technologies) VALUES
(1, '在线图书商城系统', '后端开发', '2023-03-01', '2023-06-30', '基于Spring Boot + Vue的在线图书商城，实现了用户管理、商品管理、购物车、订单等功能', 'Java,Spring Boot,Vue,MySQL'),
(1, '学生信息管理系统', '全栈开发', '2023-09-01', '2023-12-31', '为学校开发的学生信息管理系统，支持学生信息的增删改查、成绩管理、课程管理等功能', 'Vue,Element Plus,Java,MyBatis-Plus')
ON DUPLICATE KEY UPDATE project_name=VALUES(project_name);

INSERT INTO t_student_preference (student_id, expected_cities, expected_positions, salary_min, salary_max, salary_unit, job_types, company_sizes, seeking_status, self_evaluation) VALUES
(1, '["北京","上海","深圳"]', '["Java开发工程师","后端开发工程师"]', 8000, 15000, 'month', '全职,实习', '中型企业,大型企业', 'actively', '本人热爱编程，具有良好的学习能力和团队协作精神，希望能在一个有挑战性的环境中不断提升自己')
ON DUPLICATE KEY UPDATE student_id=VALUES(student_id);

INSERT INTO t_student_resume (student_id, resume_name, file_url, file_size) VALUES
(1, '个人简历_2024.pdf', '/files/resume/sample.pdf', '1.2MB')
ON DUPLICATE KEY UPDATE resume_name=VALUES(resume_name);

INSERT INTO t_student_course (student_id, course_name, score, semester) VALUES
(1, '数据结构与算法', 90, '2021秋季'),
(1, '计算机网络', 85, '2022春季'),
(1, '操作系统', 88, '2022秋季'),
(1, '数据库系统', 92, '2023春季')
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name);
