-- =============================================
-- 高校教学就业平台 - 数据库初始化脚本
-- 数据库版本: MySQL 8.0
-- 字符集: utf8mb4
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS teaching_employment_platform
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE teaching_employment_platform;

-- =============================================
-- 1. 角色管理表
-- =============================================
DROP TABLE IF EXISTS t_role;
CREATE TABLE t_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(500) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-停用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- =============================================
-- 2. 学校管理表
-- =============================================
DROP TABLE IF EXISTS t_school;
CREATE TABLE t_school (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学校ID',
    school_name VARCHAR(100) NOT NULL UNIQUE COMMENT '学校名称',
    school_code VARCHAR(50) NOT NULL UNIQUE COMMENT '学校代码',
    license_number VARCHAR(100) NOT NULL COMMENT '办学许可证号',
    license_issue_date DATE COMMENT '许可证颁发日期',
    license_expiry_date DATE COMMENT '许可证有效期至',
    license_image_url VARCHAR(255) COMMENT '许可证图片URL',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    address VARCHAR(255) COMMENT '详细地址',
    website VARCHAR(255) COMMENT '学校官网',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    description TEXT COMMENT '学校简介',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-停用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_school_name (school_name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校信息表';

-- =============================================
-- 3. 用户管理表
-- =============================================
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    school_id BIGINT COMMENT '所属学校ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-停用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (role_id) REFERENCES t_role(id),
    FOREIGN KEY (school_id) REFERENCES t_school(id),
    INDEX idx_username (username),
    INDEX idx_role_id (role_id),
    INDEX idx_school_id (school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 4. 教师管理表
-- =============================================
DROP TABLE IF EXISTS t_teacher;
CREATE TABLE t_teacher (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    teacher_no VARCHAR(50) NOT NULL UNIQUE COMMENT '教师工号',
    school_id BIGINT NOT NULL COMMENT '所属学校ID',
    department VARCHAR(100) COMMENT '所属院系',
    title VARCHAR(50) COMMENT '职称（教授/副教授/讲师等）',
    education VARCHAR(50) COMMENT '学历（博士/硕士/本科）',
    specialty VARCHAR(100) COMMENT '专业领域',
    entry_date DATE COMMENT '入职日期',
    id_card VARCHAR(18) COMMENT '身份证号',
    gender TINYINT COMMENT '性别：1-男 2-女',
    birth_date DATE COMMENT '出生日期',
    address VARCHAR(255) COMMENT '家庭地址',
    description TEXT COMMENT '个人简介',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    FOREIGN KEY (school_id) REFERENCES t_school(id),
    INDEX idx_teacher_no (teacher_no),
    INDEX idx_school_id (school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- =============================================
-- 5. 教室管理表
-- =============================================
DROP TABLE IF EXISTS t_classroom;
CREATE TABLE t_classroom (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教室ID',
    classroom_no VARCHAR(50) NOT NULL COMMENT '教室编号',
    classroom_name VARCHAR(100) NOT NULL COMMENT '教室名称',
    school_id BIGINT NOT NULL COMMENT '所属学校ID',
    building VARCHAR(50) COMMENT '所在楼栋',
    floor VARCHAR(20) COMMENT '楼层',
    capacity INT COMMENT '容纳人数',
    classroom_type VARCHAR(50) COMMENT '教室类型（普通教室/机房/实验室/会议室）',
    has_projector TINYINT DEFAULT 0 COMMENT '是否有投影仪：1-是 0-否',
    has_computer TINYINT DEFAULT 0 COMMENT '是否有电脑：1-是 0-否',
    has_multimedia TINYINT DEFAULT 0 COMMENT '是否多媒体教室：1-是 0-否',
    equipment TEXT COMMENT '设备描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-可用 0-不可用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (school_id) REFERENCES t_school(id),
    INDEX idx_school_id (school_id),
    INDEX idx_classroom_no (classroom_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室信息表';

-- =============================================
-- 6. 课程管理表
-- =============================================
DROP TABLE IF EXISTS t_course;
CREATE TABLE t_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(50) COMMENT '课程代码',
    course_type VARCHAR(50) NOT NULL COMMENT '课程类型（普通课程/直播课程）',
    school_id BIGINT NOT NULL COMMENT '开课学校ID',
    teacher_id BIGINT NOT NULL COMMENT '授课教师ID',
    classroom_id BIGINT COMMENT '教室ID',
    description TEXT COMMENT '课程描述',
    credit DECIMAL(3,1) COMMENT '学分',
    total_hours INT COMMENT '总课时',
    start_date DATE COMMENT '开课日期',
    end_date DATE COMMENT '结课日期',
    max_students INT COMMENT '最大学员数',
    current_students INT DEFAULT 0 COMMENT '当前学员数',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-未开始 ongoing-进行中 completed-已完成 cancelled-已取消',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (school_id) REFERENCES t_school(id),
    FOREIGN KEY (teacher_id) REFERENCES t_teacher(id),
    FOREIGN KEY (classroom_id) REFERENCES t_classroom(id),
    INDEX idx_course_name (course_name),
    INDEX idx_school_id (school_id),
    INDEX idx_teacher_id (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

-- =============================================
-- 7. 学员管理表
-- =============================================
DROP TABLE IF EXISTS t_student;
CREATE TABLE t_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学员ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    school_id BIGINT NOT NULL COMMENT '所属学校ID',
    class_name VARCHAR(50) COMMENT '班级',
    major VARCHAR(100) COMMENT '专业',
    grade VARCHAR(20) COMMENT '年级',
    gender TINYINT NOT NULL COMMENT '性别：1-男 2-女',
    birth_date DATE COMMENT '出生日期',
    id_card VARCHAR(18) NOT NULL COMMENT '身份证号',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '家庭地址',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '预计毕业日期',
    political_status VARCHAR(50) COMMENT '政治面貌',
    nation VARCHAR(50) COMMENT '民族',
    guardian_name VARCHAR(50) COMMENT '监护人姓名',
    guardian_phone VARCHAR(20) COMMENT '监护人电话',
    description TEXT COMMENT '备注信息',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    FOREIGN KEY (school_id) REFERENCES t_school(id),
    INDEX idx_student_no (student_no),
    INDEX idx_school_id (school_id),
    INDEX idx_id_card (id_card)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员信息表';

-- =============================================
-- 8. 课程学员关联表
-- =============================================
DROP TABLE IF EXISTS t_course_student;
CREATE TABLE t_course_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    enrollment_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-在读 0-已退课',
    progress DECIMAL(5,2) DEFAULT 0.00 COMMENT '学习进度(%)',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES t_course(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    UNIQUE KEY uk_course_student (course_id, student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程学员关联表';

-- =============================================
-- 9. 企业管理表
-- =============================================
DROP TABLE IF EXISTS t_company;
CREATE TABLE t_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '企业ID',
    company_name VARCHAR(100) NOT NULL COMMENT '企业名称',
    company_code VARCHAR(50) UNIQUE COMMENT '企业代码',
    business_license VARCHAR(100) COMMENT '营业执照号',
    credit_code VARCHAR(50) COMMENT '企业统一社会信用代码',
    short_name VARCHAR(50) COMMENT '企业简称',
    logo VARCHAR(255) COMMENT '企业logo',
    industry VARCHAR(50) COMMENT '所属行业',
    company_size VARCHAR(50) COMMENT '企业规模',
    scale VARCHAR(50) COMMENT '规模（人）：1-10人 11-50人 51-200人 201-500人 501-1000人 1000人以上',
    stage VARCHAR(50) COMMENT '发展阶段：startup-初创期 growth-成长期 maturity-成熟期 listing-上市',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    address VARCHAR(255) COMMENT '详细地址',
    website VARCHAR(255) COMMENT '企业官网',
    description TEXT COMMENT '企业简介',
    benefits TEXT COMMENT '企业福利',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_position VARCHAR(50) COMMENT '联系人职位',
    contact_email VARCHAR(100) COMMENT '联系人邮箱',
    verify_status VARCHAR(20) DEFAULT 'pending' COMMENT '企业认证状态：pending-待认证 approved-已认证 rejected-已拒绝',
    verify_time DATETIME COMMENT '认证时间',
    reject_reason TEXT COMMENT '拒绝原因',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-停用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_company_name (company_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信息表';

-- =============================================
-- 10. 招聘岗位表
-- =============================================
DROP TABLE IF EXISTS t_recruitment_position;
CREATE TABLE t_recruitment_position (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    company_id BIGINT NOT NULL COMMENT '企业ID',
    position_name VARCHAR(100) NOT NULL COMMENT '岗位名称',
    position_type VARCHAR(50) COMMENT '岗位类型（全职/实习/兼职）',
    department VARCHAR(100) COMMENT '所属部门',
    salary_min DECIMAL(10,2) COMMENT '薪资范围-最小值',
    salary_max DECIMAL(10,2) COMMENT '薪资范围-最大值',
    salary_unit VARCHAR(20) COMMENT '薪资单位：month-按月 year-按年 day-按天 hour-按时',
    work_city VARCHAR(50) COMMENT '工作城市',
    work_address VARCHAR(255) COMMENT '工作地址',
    recruit_num INT COMMENT '招聘人数',
    hired_count INT DEFAULT 0 COMMENT '已招人数',
    application_count INT DEFAULT 0 COMMENT '简历投递数',
    education_require VARCHAR(50) COMMENT '学历要求：junior_college-大专 bachelor-本科 master-硕士 doctor-博士 unlimited-不限',
    experience_require VARCHAR(50) COMMENT '经验要求',
    description TEXT COMMENT '岗位描述',
    requirements TEXT COMMENT '岗位要求',
    benefits TEXT COMMENT '福利待遇',
    tech_stack JSON COMMENT '技术栈要求（JSON数组）',
    capability_radar JSON COMMENT '能力雷达图数据（JSON对象）',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：draft-草稿 active-招聘中 paused-暂停 closed-已关闭',
    publish_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布日期',
    publish_time DATETIME COMMENT '发布时间',
    deadline DATE COMMENT '截止日期',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (company_id) REFERENCES t_company(id),
    INDEX idx_company_id (company_id),
    INDEX idx_position_name (position_name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘岗位表';

-- =============================================
-- 11. 简历管理表
-- =============================================
DROP TABLE IF EXISTS t_resume;
CREATE TABLE t_resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '简历ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT COMMENT '性别：1-男 2-女',
    birth_date DATE COMMENT '出生日期',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    job_intention VARCHAR(100) COMMENT '求职意向',
    expect_city VARCHAR(50) COMMENT '期望城市',
    expect_salary_min INT COMMENT '期望薪资最低',
    expect_salary_max INT COMMENT '期望薪资最高',
    education VARCHAR(1000) COMMENT '教育经历（JSON数组）',
    school VARCHAR(100) COMMENT '毕业院校',
    major VARCHAR(100) COMMENT '专业',
    graduation_date DATE COMMENT '毕业时间',
    projects TEXT COMMENT '项目经历（JSON数组）',
    skills TEXT COMMENT '技能特长（JSON数组）',
    project_experience TEXT COMMENT '项目经验',
    internship_experience TEXT COMMENT '实习经验',
    awards TEXT COMMENT '获奖情况',
    self_evaluation TEXT COMMENT '自我评价',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    attachment_url VARCHAR(255) COMMENT '简历附件URL',
    completeness INT DEFAULT 0 COMMENT '简历完整度（百分比）',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿 active-公开 inactive-隐藏',
    last_update_time DATETIME COMMENT '最后更新时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- =============================================
-- 12. 求职申请表
-- =============================================
DROP TABLE IF EXISTS t_job_application;
CREATE TABLE t_job_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    position_id BIGINT NOT NULL COMMENT '岗位ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    resume_id BIGINT COMMENT '简历ID',
    status VARCHAR(50) DEFAULT 'pending' COMMENT '申请状态：pending-待处理 screened-已筛选 test_passed-笔试通过 interview_passed-面试通过 test_failed-笔试失败 interview_failed-面试失败 rejected-已拒绝 offered-已发offer hired-已入职 declined-已拒绝offer',
    current_stage VARCHAR(50) COMMENT '当前阶段：resume-简历筛选 test-笔试 interview-面试 offer-offer发放 hired-入职',
    application_time DATETIME COMMENT '申请时间',
    hr_remark VARCHAR(500) COMMENT 'HR备注',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (position_id) REFERENCES t_recruitment_position(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    FOREIGN KEY (resume_id) REFERENCES t_resume(id),
    INDEX idx_position_id (position_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='求职申请表';

-- =============================================
-- 13. 笔试管理表
-- =============================================
DROP TABLE IF EXISTS t_written_test;
CREATE TABLE t_written_test (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '笔试ID',
    application_id BIGINT NOT NULL COMMENT '申请ID',
    test_date DATETIME COMMENT '考试时间',
    duration INT COMMENT '考试时长（分钟）',
    total_score DECIMAL(5,2) COMMENT '总分',
    pass_score DECIMAL(5,2) COMMENT '及格分',
    student_score DECIMAL(5,2) COMMENT '学员得分',
    test_content TEXT COMMENT '考试内容（JSON格式）',
    test_result TEXT COMMENT '考试结果（JSON格式）',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待考试 ongoing-考试中 completed-已完成',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (application_id) REFERENCES t_job_application(id),
    INDEX idx_application_id (application_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔试表';

-- =============================================
-- 14. 面试管理表
-- =============================================
DROP TABLE IF EXISTS t_interview;
CREATE TABLE t_interview (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '面试ID',
    application_id BIGINT NOT NULL COMMENT '申请ID',
    position_id BIGINT NOT NULL COMMENT '职位ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    round INT COMMENT '面试轮次：1-初试 2-复试 3-终试',
    interview_type VARCHAR(50) COMMENT '面试类型：online-线上 offline-现场 phone-电话',
    interview_time DATETIME COMMENT '面试时间',
    location VARCHAR(255) COMMENT '面试地点（线下面试）',
    meeting_link VARCHAR(255) COMMENT '会议链接（线上面试）',
    interviewer VARCHAR(100) COMMENT '面试官',
    interviewer_contact VARCHAR(100) COMMENT '面试官联系方式',
    result VARCHAR(20) COMMENT '面试结果：pending-待面试 passed-通过 failed-未通过',
    score INT COMMENT '面试评分（1-100分）',
    comment TEXT COMMENT '面试评价',
    status VARCHAR(20) DEFAULT 'scheduled' COMMENT '面试状态：scheduled-已安排 completed-已完成 cancelled-已取消',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (application_id) REFERENCES t_job_application(id),
    FOREIGN KEY (position_id) REFERENCES t_recruitment_position(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    INDEX idx_application_id (application_id),
    INDEX idx_position_id (position_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试表';

-- =============================================
-- 15. Offer表
-- =============================================
DROP TABLE IF EXISTS t_offer;
CREATE TABLE t_offer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'OfferID',
    application_id BIGINT NOT NULL COMMENT '申请ID',
    offer_no VARCHAR(50) UNIQUE COMMENT 'Offer编号',
    position_id BIGINT NOT NULL COMMENT '岗位ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    company_id BIGINT NOT NULL COMMENT '企业ID',
    position_name VARCHAR(100) COMMENT '职位名称',
    city VARCHAR(50) COMMENT '入职城市',
    salary DECIMAL(10,2) COMMENT '薪资',
    salary_unit VARCHAR(20) COMMENT '薪资单位：month-按月 year-按年',
    onboard_date DATE COMMENT '入职日期',
    accept_deadline DATE COMMENT '接受截止日期',
    accept_time DATETIME COMMENT '接受时间',
    email_status VARCHAR(20) DEFAULT 'pending' COMMENT '邮件发送状态：pending-待发送 sent-已发送 failed-发送失败',
    email_send_time DATETIME COMMENT '邮件发送时间',
    attachment_url VARCHAR(255) COMMENT '附件URL（Offer PDF）',
    remark TEXT COMMENT '备注',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待接受 accepted-已接受 rejected-已拒绝 cancelled-已取消 expired-已过期',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (application_id) REFERENCES t_job_application(id),
    FOREIGN KEY (position_id) REFERENCES t_recruitment_position(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    FOREIGN KEY (company_id) REFERENCES t_company(id),
    INDEX idx_application_id (application_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Offer表';

-- =============================================
-- 16. 作业管理表
-- =============================================
DROP TABLE IF EXISTS t_homework;
CREATE TABLE t_homework (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '作业ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    title VARCHAR(200) NOT NULL COMMENT '作业标题',
    description TEXT COMMENT '作业描述',
    homework_type VARCHAR(50) COMMENT '作业类型：assignment-作业 project-项目 other-其他',
    attachment_url VARCHAR(255) COMMENT '附件URL（教师上传的作业文件）',
    deadline DATETIME COMMENT '截止时间',
    max_score INT COMMENT '满分',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿 published-已发布 closed-已截止',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES t_course(id),
    FOREIGN KEY (teacher_id) REFERENCES t_teacher(id),
    INDEX idx_course_id (course_id),
    INDEX idx_teacher_id (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- =============================================
-- 17. 作业提交表
-- =============================================
DROP TABLE IF EXISTS t_homework_submission;
CREATE TABLE t_homework_submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提交ID',
    homework_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    content TEXT COMMENT '作业内容',
    attachment_url VARCHAR(255) COMMENT '附件URL',
    submit_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    score DECIMAL(5,2) COMMENT '得分',
    comment TEXT COMMENT '批改意见',
    status VARCHAR(20) DEFAULT 'submitted' COMMENT '状态：draft-草稿 submitted-已提交 graded-已批改',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (homework_id) REFERENCES t_homework(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    UNIQUE KEY uk_homework_student (homework_id, student_id),
    INDEX idx_homework_id (homework_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- =============================================
-- 18. 日报管理表
-- =============================================
DROP TABLE IF EXISTS t_daily_report;
CREATE TABLE t_daily_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日报ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    report_date DATE NOT NULL COMMENT '日报日期',
    content TEXT NOT NULL COMMENT '日报内容',
    work_hours DECIMAL(4,1) COMMENT '工作时长',
    tomorrow_plan TEXT COMMENT '明日计划',
    issues TEXT COMMENT '遇到的问题',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    UNIQUE KEY uk_student_date (student_id, report_date),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日报表';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化角色数据
INSERT INTO t_role (role_code, role_name, description) VALUES
('admin', '管理员', '系统管理员，拥有所有权限'),
('college_head', '学院负责人', '学院级别的管理权限'),
('teacher', '教师', '授课教师权限'),
('user', '学生', '学员/学生权限'),
('enterprise_contact', '企业对接人', '企业招聘联系人权限');

-- 初始化学校数据
INSERT INTO t_school (school_name, school_code, license_number, license_issue_date, license_expiry_date, province, city, contact_person, contact_phone) VALUES
('福建师范大学', 'FJNU001', '教民135000012345678', '2000-01-01', '2025-12-31', '福建省', '福州市', '张老师', '0591-12345678'),
('福建农林大学', 'FJAU001', '教民135000023456789', '2000-01-01', '2025-12-31', '福建省', '福州市', '李老师', '0591-23456789'),
('福建理工大学', 'FJUT001', '教民135000034567890', '2000-01-01', '2025-12-31', '福建省', '福州市', '王老师', '0591-34567890'),
('福州大学', 'FZU001', '教民135000045678901', '2000-01-01', '2025-12-31', '福建省', '福州市', '陈老师', '0591-45678901'),
('厦门大学', 'XMU001', '教民135000056789012', '2000-01-01', '2025-12-31', '福建省', '厦门市', '刘老师', '0592-56789012'),
('集美大学', 'JMU001', '教民135000067890123', '2000-01-01', '2025-12-31', '福建省', '厦门市', '黄老师', '0592-67890123'),
('闽南师范大学', 'MNU001', '教民135000078901234', '2000-01-01', '2025-12-31', '福建省', '漳州市', '林老师', '0596-78901234');

-- 初始化管理员账号 (用户名: admin, 密码: admin123)
-- 密码使用 BCrypt 加密，实际值为 admin123
INSERT INTO t_user (username, password, real_name, role_id) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1);

COMMIT;

-- =============================================
-- 19. 消息通知表
-- =============================================
DROP TABLE IF EXISTS t_notification;
CREATE TABLE t_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type VARCHAR(50) COMMENT '通知类型（system-系统消息 interview-面试通知 offer-录用通知 homework-作业通知等）',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：1-是 0-否',
    related_id BIGINT COMMENT '关联ID（如面试ID、作业ID等）',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- =============================================
-- 20. 考试管理表
-- =============================================
DROP TABLE IF EXISTS t_exam;
CREATE TABLE t_exam (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试ID',
    exam_name VARCHAR(200) NOT NULL COMMENT '考试名称',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    exam_type VARCHAR(50) COMMENT '考试类型（final-期末考试 mid-期中考试 quiz-随堂测试）',
    duration INT COMMENT '考试时长(分钟)',
    total_score INT COMMENT '总分',
    passing_score INT COMMENT '及格分',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    description TEXT COMMENT '考试说明',
    is_random TINYINT DEFAULT 0 COMMENT '是否随机出题：1-是 0-否',
    question_count INT COMMENT '题目数量',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿 published-已发布 started-已进行中 ended-已结束',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES t_course(id),
    INDEX idx_course_id (course_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- =============================================
-- 21. 题目表
-- =============================================
DROP TABLE IF EXISTS t_question;
CREATE TABLE t_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    question_type VARCHAR(50) COMMENT '题目类型（single-单选题 multi-多选题 essay-简答题）',
    content TEXT NOT NULL COMMENT '题目内容',
    option_a TEXT COMMENT '选项A',
    option_b TEXT COMMENT '选项B',
    option_c TEXT COMMENT '选项C',
    option_d TEXT COMMENT '选项D',
    correct_answer VARCHAR(500) COMMENT '正确答案',
    score INT COMMENT '分值',
    sort_order INT DEFAULT 0 COMMENT '题目排序',
    analysis TEXT COMMENT '题目解析',
    difficulty VARCHAR(20) COMMENT '难度级别（easy-简单 medium-中等 hard-困难）',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (exam_id) REFERENCES t_exam(id),
    INDEX idx_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- =============================================
-- 22. 学员考试记录表
-- =============================================
DROP TABLE IF EXISTS t_student_exam_record;
CREATE TABLE t_student_exam_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    answers TEXT COMMENT '答题内容(JSON格式存储)',
    score DECIMAL(5,2) COMMENT '得分',
    is_passed TINYINT COMMENT '是否及格：1-是 0-否',
    start_time DATETIME COMMENT '考试开始时间',
    end_time DATETIME COMMENT '考试结束时间',
    duration INT COMMENT '用时(秒)',
    status VARCHAR(20) DEFAULT 'not_started' COMMENT '考试状态（not_started-未开始 in_progress-进行中 submitted-已提交 graded-已批改）',
    comment TEXT COMMENT '评语',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (exam_id) REFERENCES t_exam(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    INDEX idx_exam_id (exam_id),
    INDEX idx_student_id (student_id),
    UNIQUE KEY uk_exam_student (exam_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员考试记录表';

-- =============================================
-- 23. 菜单管理表
-- =============================================
DROP TABLE IF EXISTS t_menu;
CREATE TABLE t_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID，0为根菜单',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(200) COMMENT '菜单路径',
    menu_type VARCHAR(20) COMMENT '菜单类型（menu-菜单目录 button-按钮权限）',
    permission VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_visible TINYINT DEFAULT 1 COMMENT '是否可见：1-是 0-否',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-停用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- =============================================
-- 24. 权限管理表
-- =============================================
DROP TABLE IF EXISTS t_permission;
CREATE TABLE t_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID，0为根权限',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    permission_type VARCHAR(20) COMMENT '权限类型（menu-菜单权限 button-按钮权限 api-接口权限）',
    path VARCHAR(200) COMMENT '权限路径',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-停用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_permission_code (permission_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- =============================================
-- 25. 角色菜单关联表
-- =============================================
DROP TABLE IF EXISTS t_role_menu_relation;
CREATE TABLE t_role_menu_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (role_id) REFERENCES t_role(id),
    FOREIGN KEY (menu_id) REFERENCES t_menu(id),
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    INDEX idx_role_id (role_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- =============================================
-- 26. 角色权限关联表
-- =============================================
DROP TABLE IF EXISTS t_role_permission_relation;
CREATE TABLE t_role_permission_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (role_id) REFERENCES t_role(id),
    FOREIGN KEY (permission_id) REFERENCES t_permission(id),
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- =============================================
-- 27. 课程学习记录表
-- =============================================
DROP TABLE IF EXISTS t_course_record;
CREATE TABLE t_course_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    progress DECIMAL(5,2) DEFAULT 0.00 COMMENT '学习进度(%)',
    watched_duration BIGINT DEFAULT 0 COMMENT '已观看时长(秒)',
    avg_homework_score DECIMAL(5,2) COMMENT '平均作业分',
    exam_score DECIMAL(5,2) COMMENT '考试成绩',
    completed_chapters INT DEFAULT 0 COMMENT '已完成章节数',
    total_chapters INT DEFAULT 0 COMMENT '总章节数',
    status VARCHAR(20) DEFAULT 'not_started' COMMENT '学习状态（not_started-未开始 in_progress-学习中 completed-已完成）',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    FOREIGN KEY (course_id) REFERENCES t_course(id),
    UNIQUE KEY uk_student_course (student_id, course_id),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程学习记录表';

-- =============================================
-- 初始化菜单数据
-- =============================================
INSERT INTO t_menu (parent_id, menu_name, path, menu_type, permission, icon, sort_order, is_visible) VALUES
-- 一级菜单
(0, '系统管理', '/system', 'menu', 'system:view', 'setting', 1, 1),
(0, '教学管理', '/teaching', 'menu', 'teaching:view', 'book', 2, 1),
(0, '就业管理', '/employment', 'menu', 'employment:view', 'briefcase', 3, 1),
(0, '个人中心', '/profile', 'menu', 'profile:view', 'user', 4, 1);

-- =============================================
-- 初始化权限数据
-- =============================================
INSERT INTO t_permission (parent_id, permission_name, permission_code, permission_type, path, sort_order) VALUES
-- 系统管理权限
(0, '系统管理', 'system', 'menu', '/system', 1),
(1, '用户管理', 'system:user', 'api', '/user/**', 1),
(1, '角色管理', 'system:role', 'api', '/role/**', 2),
(1, '菜单管理', 'system:menu', 'api', '/menu/**', 3),
(1, '权限管理', 'system:permission', 'api', '/permission/**', 4),
-- 教学管理权限
(0, '教学管理', 'teaching', 'menu', '/teaching', 2),
(6, '学校管理', 'teaching:school', 'api', '/school/**', 1),
(6, '教师管理', 'teaching:teacher', 'api', '/teacher/**', 2),
(6, '课程管理', 'teaching:course', 'api', '/course/**', 3),
(6, '学员管理', 'teaching:student', 'api', '/student/**', 4),
(6, '作业管理', 'teaching:homework', 'api', '/homework/**', 5),
(6, '考试管理', 'teaching:exam', 'api', '/exam/**', 6),
-- 就业管理权限
(0, '就业管理', 'employment', 'menu', '/employment', 3),
(12, '企业管理', 'employment:company', 'api', '/company/**', 1),
(12, '职位管理', 'employment:position', 'api', '/position/**', 2),
(12, '简历管理', 'employment:resume', 'api', '/resume/**', 3),
(12, '申请管理', 'employment:application', 'api', '/application/**', 4),
(12, '面试管理', 'employment:interview', 'api', '/interview/**', 5);

-- =============================================
-- 初始化角色菜单关联（管理员拥有所有菜单权限）
-- =============================================
INSERT INTO t_role_menu_relation (role_id, menu_id)
SELECT 1, id FROM t_menu WHERE is_deleted = 0;

-- =============================================
-- 初始化角色权限关联（管理员拥有所有权限）
-- =============================================
INSERT INTO t_role_permission_relation (role_id, permission_id)
SELECT 1, id FROM t_permission WHERE is_deleted = 0;

COMMIT;

-- 显示初始化完成信息
SELECT '数据库初始化完成！' AS message;
SELECT COUNT(*) AS role_count FROM t_role;
SELECT COUNT(*) AS school_count FROM t_school;
SELECT COUNT(*) AS user_count FROM t_user;
SELECT COUNT(*) AS menu_count FROM t_menu;
SELECT COUNT(*) AS permission_count FROM t_permission;
