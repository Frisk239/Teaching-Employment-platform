-- 学员教育经历扩展SQL
-- 创建日期: 2026-01-19

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 1. 创建教育经历表
CREATE TABLE IF NOT EXISTS t_student_education (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  school_name VARCHAR(200) NOT NULL COMMENT '学校名称',
  college VARCHAR(200) COMMENT '学院',
  major VARCHAR(200) NOT NULL COMMENT '专业',
  grade VARCHAR(50) COMMENT '年级',
  class_name VARCHAR(100) COMMENT '班级',
  education VARCHAR(50) COMMENT '学历(本科/硕士/博士/专科)',
  enrollment_date DATE COMMENT '入学时间',
  graduation_date DATE COMMENT '预计毕业时间',
  duration VARCHAR(20) COMMENT '学制',
  status VARCHAR(20) DEFAULT '在校' COMMENT '在校状态',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_student_id (student_id),
  FOREIGN KEY (student_id) REFERENCES t_student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员教育经历表';

-- 2. 插入测试数据(为张三添加，假设student_id = 1)
INSERT INTO t_student_education (student_id, school_name, college, major, grade, class_name, education, enrollment_date, graduation_date, duration, status) VALUES
(1, '某某大学', '计算机科学与技术学院', '计算机科学与技术', '2021级', '计科2101班', '本科', '2021-09-01', '2025-06-30', '4年', '在校')
ON DUPLICATE KEY UPDATE school_name=VALUES(school_name);
