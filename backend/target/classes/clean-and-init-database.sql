-- 清空并重新初始化数据库
-- 创建日期: 2025-01-18
-- 说明: 清空所有表，为后续完整测试数据做准备

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 删除所有表（按依赖关系顺序）
DROP TABLE IF EXISTS t_internship;
DROP TABLE IF EXISTS t_education;
DROP TABLE IF EXISTS t_talent_pool;
DROP TABLE IF EXISTS t_offer;
DROP TABLE IF EXISTS t_interview;
DROP TABLE IF EXISTS t_job_application;
DROP TABLE IF EXISTS t_written_test;
DROP TABLE IF EXISTS t_homework_submission;
DROP TABLE IF EXISTS t_homework;
DROP TABLE IF EXISTS t_daily_report;
DROP TABLE IF EXISTS t_course_student;
DROP TABLE IF EXISTS t_course_record;
DROP TABLE IF EXISTS t_timetable;
DROP TABLE IF EXISTS t_teaching_material;
DROP TABLE IF EXISTS t_exam;
DROP TABLE IF EXISTS t_student_exam_record;
DROP TABLE IF EXISTS t_course;
DROP TABLE IF EXISTS t_student;
DROP TABLE IF EXISTS t_teacher;
DROP TABLE IF EXISTS t_classroom;
DROP TABLE IF EXISTS t_school;
DROP TABLE IF EXISTS t_company;
DROP TABLE IF EXISTS t_position;
DROP TABLE IF EXISTS t_role_menu_relation;
DROP TABLE IF EXISTS t_role_permission_relation;
DROP TABLE IF EXISTS t_menu;
DROP TABLE IF EXISTS t_permission;
DROP TABLE IF EXISTS t_role;
DROP TABLE IF EXISTS t_user;

SET FOREIGN_KEY_CHECKS = 1;

-- ===================================
-- 核心表创建
-- ===================================

-- 1. 角色表
CREATE TABLE t_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
  role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
  role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
  description VARCHAR(200) COMMENT '角色描述',
  status INT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 2. 权限表
CREATE TABLE t_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
  permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
  permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
  resource_type VARCHAR(20) COMMENT '资源类型：menu-菜单 button-按钮 api-接口',
  description VARCHAR(200) COMMENT '权限描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 3. 菜单表
CREATE TABLE t_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
  parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID，0表示根菜单',
  menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
  menu_type VARCHAR(20) NOT NULL COMMENT '菜单类型：directory-目录 menu-菜单 button-按钮',
  path VARCHAR(200) COMMENT '路由路径',
  component VARCHAR(200) COMMENT '组件路径',
  icon VARCHAR(100) COMMENT '菜单图标',
  sort_order INT DEFAULT 0 COMMENT '排序',
  visible INT DEFAULT 1 COMMENT '是否可见：1-是 0-否',
  status INT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 4. 角色权限关联表
CREATE TABLE t_role_permission_relation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_role_permission (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES t_role(id) ON DELETE CASCADE,
  FOREIGN KEY (permission_id) REFERENCES t_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 5. 角色菜单关联表
CREATE TABLE t_role_menu_relation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_role_menu (role_id, menu_id),
  FOREIGN KEY (role_id) REFERENCES t_role(id) ON DELETE CASCADE,
  FOREIGN KEY (menu_id) REFERENCES t_menu(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 6. 用户表（扩展版，包含学员专用字段）
CREATE TABLE t_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
  real_name VARCHAR(50) COMMENT '真实姓名',
  phone VARCHAR(20) COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  avatar VARCHAR(255) COMMENT '头像URL',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  school_id BIGINT COMMENT '所属学校ID',
  company_id BIGINT COMMENT '所属企业ID（企业对接人专用）',
  student_number VARCHAR(50) COMMENT '学号（学员专用）',
  resume_url VARCHAR(255) COMMENT '简历文件URL（学员专用）',
  status INT DEFAULT 1 COMMENT '状态：1-正常 0-停用',
  is_deleted INT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (role_id) REFERENCES t_role(id),
  INDEX idx_username (username),
  INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 7. 学校表
CREATE TABLE t_school (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学校ID',
  school_name VARCHAR(100) NOT NULL COMMENT '学校名称',
  school_code VARCHAR(50) UNIQUE COMMENT '学校编码',
  address VARCHAR(200) COMMENT '地址',
  contact_person VARCHAR(50) COMMENT '联系人',
  contact_phone VARCHAR(20) COMMENT '联系电话',
  description TEXT COMMENT '学校简介',
  status INT DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校表';

-- 8. 企业表
CREATE TABLE t_company (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '企业ID',
  company_name VARCHAR(100) NOT NULL COMMENT '企业名称',
  company_code VARCHAR(50) UNIQUE COMMENT '企业编码',
  industry VARCHAR(50) COMMENT '行业',
  scale VARCHAR(50) COMMENT '规模',
  location VARCHAR(200) COMMENT '地址',
  website VARCHAR(255) COMMENT '网站',
  description TEXT COMMENT '企业简介',
  contact_person VARCHAR(50) COMMENT '联系人',
  contact_phone VARCHAR(20) COMMENT '联系电话',
  contact_email VARCHAR(100) COMMENT '联系邮箱',
  status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-激活 inactive-停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业表';

-- 9. 职位表
CREATE TABLE t_position (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '职位ID',
  company_id BIGINT NOT NULL COMMENT '企业ID',
  position_name VARCHAR(100) NOT NULL COMMENT '职位名称',
  position_type VARCHAR(20) DEFAULT 'fulltime' COMMENT '职位类型：fulltime-全职 parttime-兼职 internship-实习',
  salary_min DECIMAL(10,2) COMMENT '最低薪资',
  salary_max DECIMAL(10,2) COMMENT '最高薪资',
  location VARCHAR(200) COMMENT '工作地点',
  requirements TEXT COMMENT '职位要求',
  description TEXT COMMENT '职位描述',
  recruitment_count INT DEFAULT 1 COMMENT '招聘人数',
  status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-激活 inactive-停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (company_id) REFERENCES t_company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';

-- 10. 学员表
CREATE TABLE t_student (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学员ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
  school_id BIGINT COMMENT '所属学校ID',
  gender INT COMMENT '性别：1-男 2-女',
  phone VARCHAR(20) COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  grade VARCHAR(20) COMMENT '年级',
  major VARCHAR(100) COMMENT '专业',
  class_name VARCHAR(50) COMMENT '班级',
  enrollment_date DATE COMMENT '入学日期',
  graduation_date DATE COMMENT '预计毕业日期',
  political_status VARCHAR(50) COMMENT '政治面貌',
  nation VARCHAR(50) COMMENT '民族',
  birth_date DATE COMMENT '出生日期',
  id_card VARCHAR(18) COMMENT '身份证号',
  address VARCHAR(200) COMMENT '家庭地址',
  guardian_name VARCHAR(50) COMMENT '监护人姓名',
  guardian_phone VARCHAR(20) COMMENT '监护人电话',
  description TEXT COMMENT '备注信息',
  is_deleted INT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (user_id) REFERENCES t_user(id),
  FOREIGN KEY (school_id) REFERENCES t_school(id),
  INDEX idx_student_no (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员表';

-- 11. 教师表
CREATE TABLE t_teacher (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  school_id BIGINT COMMENT '所属学校ID',
  teacher_no VARCHAR(50) NOT NULL UNIQUE COMMENT '教师工号',
  name VARCHAR(50) COMMENT '姓名',
  gender INT COMMENT '性别：1-男 2-女',
  phone VARCHAR(20) COMMENT '手机号',
  email VARCHAR(100) COMMENT '邮箱',
  department VARCHAR(100) COMMENT '部门',
  title VARCHAR(50) COMMENT '职称',
  education VARCHAR(50) COMMENT '学历',
  specialization VARCHAR(100) COMMENT '专业方向',
  description TEXT COMMENT '简介',
  is_deleted INT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (user_id) REFERENCES t_user(id),
  FOREIGN KEY (school_id) REFERENCES t_school(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 12. 教室表
CREATE TABLE t_classroom (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教室ID',
  school_id BIGINT COMMENT '所属学校ID',
  classroom_no VARCHAR(50) COMMENT '教室编号',
  classroom_name VARCHAR(100) COMMENT '教室名称',
  building VARCHAR(50) COMMENT '所在楼栋',
  floor VARCHAR(20) COMMENT '楼层',
  capacity INT COMMENT '容纳人数',
  classroom_type VARCHAR(50) COMMENT '教室类型',
  has_projector INT DEFAULT 0 COMMENT '是否有投影仪：1-是 0-否',
  has_computer INT DEFAULT 0 COMMENT '是否有电脑：1-是 0-否',
  has_multimedia INT DEFAULT 0 COMMENT '是否多媒体教室：1-是 0-否',
  equipment TEXT COMMENT '设备描述',
  status INT DEFAULT 1 COMMENT '状态：1-可用 0-不可用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (school_id) REFERENCES t_school(id),
  INDEX idx_school_id (school_id),
  INDEX idx_classroom_no (classroom_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室表';

-- 13. 课程表
CREATE TABLE t_course (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
  course_code VARCHAR(50) NOT NULL UNIQUE COMMENT '课程编码',
  course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
  course_type VARCHAR(50) COMMENT '课程类型',
  credits DECIMAL(3,1) COMMENT '学分',
  hours INT COMMENT '学时',
  description TEXT COMMENT '课程描述',
  teacher_id BIGINT COMMENT '授课教师ID',
  status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-激活 inactive-停用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (teacher_id) REFERENCES t_teacher(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 14. 课程学生关联表
CREATE TABLE t_course_student (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  enrollment_date DATE COMMENT '选课日期',
  status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-在读 inactive-退课',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_course_student (course_id, student_id),
  FOREIGN KEY (course_id) REFERENCES t_course(id),
  FOREIGN KEY (student_id) REFERENCES t_student(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程学生关联表';

-- 15. 课程表（时间表）
CREATE TABLE t_timetable (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程表ID',
  course_id BIGINT COMMENT '课程ID',
  student_id BIGINT COMMENT '学员ID',
  teacher_id BIGINT COMMENT '教师ID',
  classroom_id BIGINT COMMENT '教室ID',
  week_number INT DEFAULT 1 COMMENT '周次',
  day_of_week INT COMMENT '星期几：1-7（周一到周日）',
  start_period INT COMMENT '开始节次',
  end_period INT COMMENT '结束节次',
  semester VARCHAR(20) COMMENT '学期',
  school_year VARCHAR(20) COMMENT '学年',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (course_id) REFERENCES t_course(id),
  FOREIGN KEY (student_id) REFERENCES t_student(id),
  FOREIGN KEY (teacher_id) REFERENCES t_teacher(id),
  FOREIGN KEY (classroom_id) REFERENCES t_classroom(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表（时间表）';

-- 16. 作业表
CREATE TABLE t_homework (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '作业ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  title VARCHAR(200) NOT NULL COMMENT '作业标题',
  content TEXT COMMENT '作业内容',
  attachment_url VARCHAR(255) COMMENT '附件URL',
  deadline DATETIME COMMENT '截止时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (course_id) REFERENCES t_course(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- 17. 作业提交表
CREATE TABLE t_homework_submission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提交ID',
  homework_id BIGINT NOT NULL COMMENT '作业ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  content TEXT COMMENT '提交内容',
  attachment_url VARCHAR(255) COMMENT '附件URL',
  submit_time DATETIME COMMENT '提交时间',
  score DECIMAL(5,2) COMMENT '分数',
  comment TEXT COMMENT '批改意见',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (homework_id) REFERENCES t_homework(id),
  FOREIGN KEY (student_id) REFERENCES t_student(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- 18. 日报表
CREATE TABLE t_daily_report (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日报ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  report_date DATE NOT NULL COMMENT '日报日期',
  today_content TEXT COMMENT '今日学习内容',
  today_progress TEXT COMMENT '今日进度',
  problems TEXT COMMENT '遇到的问题',
  tomorrow_plan TEXT COMMENT '明日计划',
  study_hours DECIMAL(4,1) COMMENT '学习时长（小时）',
  attachment_url VARCHAR(255) COMMENT '附件URL',
  status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿 submitted-已提交 reviewed-已批阅',
  teacher_comment TEXT COMMENT '教师批阅',
  rating INT COMMENT '评分（1-5）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (student_id) REFERENCES t_student(id),
  INDEX idx_student_date (student_id, report_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日报表';

-- 19. 教学资料表
CREATE TABLE t_teaching_material (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资料ID',
  course_id BIGINT COMMENT '课程ID',
  title VARCHAR(200) NOT NULL COMMENT '资料标题',
  material_type VARCHAR(20) COMMENT '资料类型：document-文档 video-视频 other-其他',
  file_url VARCHAR(255) COMMENT '文件URL',
  description TEXT COMMENT '资料描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (course_id) REFERENCES t_course(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学资料表';

-- 20. 考试表
CREATE TABLE t_exam (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试ID',
  course_id BIGINT COMMENT '课程ID（可选，为空表示独立考试）',
  title VARCHAR(200) NOT NULL COMMENT '考试标题',
  description TEXT COMMENT '考试说明',
  duration INT COMMENT '考试时长（分钟）',
  start_time DATETIME COMMENT '开始时间',
  end_time DATETIME COMMENT '结束时间',
  full_score DECIMAL(5,2) DEFAULT 100 COMMENT '满分',
  passing_score DECIMAL(5,2) DEFAULT 60 COMMENT '及格分',
  status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿 published-已发布 started-进行中 ended-已结束',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (course_id) REFERENCES t_course(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- 21. 学员考试记录表
CREATE TABLE t_student_exam_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  exam_id BIGINT NOT NULL COMMENT '考试ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  start_time DATETIME COMMENT '开始时间',
  end_time DATETIME COMMENT '结束时间',
  score DECIMAL(5,2) COMMENT '得分',
  status VARCHAR(20) DEFAULT 'not_started' COMMENT '状态：not_started-未开始 in_progress-进行中 completed-已完成',
  answers TEXT COMMENT '答案（JSON格式）',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (exam_id) REFERENCES t_exam(id),
  FOREIGN KEY (student_id) REFERENCES t_student(id),
  UNIQUE KEY uk_exam_student (exam_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员考试记录表';

-- 22. 课程记录表（课程进度）
CREATE TABLE t_course_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  week_number INT COMMENT '周次',
  content TEXT COMMENT '课程内容',
  homework TEXT COMMENT '作业',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (course_id) REFERENCES t_course(id),
  FOREIGN KEY (student_id) REFERENCES t_student(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程记录表';

-- 23. 笔试表
CREATE TABLE t_written_test (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '笔试ID',
  course_id BIGINT COMMENT '关联课程ID',
  position_id BIGINT COMMENT '关联职位ID',
  title VARCHAR(200) NOT NULL COMMENT '笔试标题',
  description TEXT COMMENT '笔试说明',
  test_url VARCHAR(255) COMMENT '笔试链接',
  duration INT COMMENT '笔试时长（分钟）',
  start_time DATETIME COMMENT '开始时间',
  end_time DATETIME COMMENT '结束时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (course_id) REFERENCES t_course(id),
  FOREIGN KEY (position_id) REFERENCES t_position(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔试表';

-- 24. 求职申请表
CREATE TABLE t_job_application (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
  position_id BIGINT NOT NULL COMMENT '职位ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  company_id BIGINT NOT NULL COMMENT '企业ID',
  status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待处理 screened-已筛选 test_passed-笔试通过 interview_passed-面试通过 hired-已录用 rejected-已拒绝',
  apply_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (position_id) REFERENCES t_position(id),
  FOREIGN KEY (student_id) REFERENCES t_student(id),
  FOREIGN KEY (company_id) REFERENCES t_company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='求职申请表';

-- 25. 面试表
CREATE TABLE t_interview (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '面试ID',
  application_id BIGINT COMMENT '申请ID',
  position_id BIGINT COMMENT '职位ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  company_id BIGINT NOT NULL COMMENT '企业ID',
  interview_type VARCHAR(20) DEFAULT 'online' COMMENT '面试类型：online-线上 offline-线下',
  interview_time DATETIME COMMENT '面试时间',
  duration INT COMMENT '面试时长（分钟）',
  interview_url VARCHAR(255) COMMENT '面试链接',
  location VARCHAR(200) COMMENT '面试地点',
  status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待面试 completed-已完成 accepted-已接受 rejected-已拒绝',
  score DECIMAL(5,2) COMMENT '面试评分',
  feedback TEXT COMMENT '面试反馈',
  result VARCHAR(20) COMMENT '面试结果：pass-通过 fail-未通过 pending-待定',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (student_id) REFERENCES t_student(id),
  FOREIGN KEY (company_id) REFERENCES t_company(id),
  FOREIGN KEY (position_id) REFERENCES t_position(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试表';

-- 26. Offer表
CREATE TABLE t_offer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'OfferID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  company_id BIGINT NOT NULL COMMENT '企业ID',
  position_id BIGINT COMMENT '职位ID',
  position_name VARCHAR(100) COMMENT '职位名称',
  salary DECIMAL(10,2) COMMENT '薪资',
  start_date DATE COMMENT '入职日期',
  status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待接受 accepted-已接受 declined-已拒绝',
  offer_file_url VARCHAR(255) COMMENT 'Offer文件URL',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (student_id) REFERENCES t_student(id),
  FOREIGN KEY (company_id) REFERENCES t_company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Offer表';

-- 27. 人才库表
CREATE TABLE t_talent_pool (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '人才ID',
  student_id BIGINT NOT NULL COMMENT '学员ID',
  company_id BIGINT NOT NULL COMMENT '企业ID',
  source VARCHAR(50) COMMENT '来源：application-求职推荐 interview-面试推荐',
  status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-活跃 inactive-不活跃',
  notes TEXT COMMENT '备注',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (student_id) REFERENCES t_student(id),
  FOREIGN KEY (company_id) REFERENCES t_company(id),
  UNIQUE KEY uk_student_company (student_id, company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人才库表';

-- ===================================
-- 学员端扩展表
-- ===================================

-- 28. 教育经历表
CREATE TABLE t_education (
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
  INDEX idx_user_dates (user_id, start_date DESC),
  FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教育经历表';

-- 29. 实习经历表
CREATE TABLE t_internship (
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
  INDEX idx_user_dates (user_id, start_date DESC),
  FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实习经历表';

-- 完成
SELECT '数据库初始化完成！' AS message;
