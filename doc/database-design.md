# 高校教学就业平台 - 数据库设计方案

## 数据库基本信息
- 数据库名: `teaching_employment_platform`
- 字符集: `utf8mb4`
- 排序规则: `utf8mb4_general_ci`
- MySQL版本: 8.0

## 数据表设计

### 1. 学校管理表 (t_school)
```sql
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
) COMMENT='学校信息表';
```

### 2. 角色管理表 (t_role)
```sql
CREATE TABLE t_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-停用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_role_code (role_code)
) COMMENT='角色表';
```

### 3. 用户管理表 (t_user)
```sql
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
) COMMENT='用户表';
```

### 4. 教师管理表 (t_teacher)
```sql
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
) COMMENT='教师信息表';
```

### 5. 教室管理表 (t_classroom)
```sql
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
) COMMENT='教室信息表';
```

### 6. 课程管理表 (t_course)
```sql
CREATE TABLE t_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(50) COMMENT '课程代码',
    course_type VARCHAR(50) NOT NULL COMMENT '课程类型（普通课程/直播课程）',
    school_id BIGINT NOT NULL COMMENT '开课学校ID',
    teacher_id BIGINT NOT NULL COMMENT '授课教师ID',
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
    INDEX idx_course_name (course_name),
    INDEX idx_school_id (school_id),
    INDEX idx_teacher_id (teacher_id)
) COMMENT='课程信息表';
```

### 7. 学员管理表 (t_student)
```sql
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
) COMMENT='学员信息表';
```

### 8. 课程学员关联表 (t_course_student)
```sql
CREATE TABLE t_course_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    enroll_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-在读 completed-已完成 dropped-退课',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (course_id) REFERENCES t_course(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    UNIQUE KEY uk_course_student (course_id, student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_student_id (student_id)
) COMMENT='课程学员关联表';
```

### 9. 企业管理表 (t_company)
```sql
CREATE TABLE t_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '企业ID',
    company_name VARCHAR(100) NOT NULL COMMENT '企业名称',
    company_code VARCHAR(50) UNIQUE COMMENT '企业代码',
    business_license VARCHAR(100) COMMENT '营业执照号',
    industry VARCHAR(50) COMMENT '所属行业',
    company_size VARCHAR(50) COMMENT '企业规模',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    address VARCHAR(255) COMMENT '详细地址',
    website VARCHAR(255) COMMENT '企业官网',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    description TEXT COMMENT '企业简介',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常 0-停用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_company_name (company_name)
) COMMENT='企业信息表';
```

### 10. 招聘岗位表 (t_recruitment_position)
```sql
CREATE TABLE t_recruitment_position (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    company_id BIGINT NOT NULL COMMENT '企业ID',
    position_name VARCHAR(100) NOT NULL COMMENT '岗位名称',
    position_type VARCHAR(50) COMMENT '岗位类型（全职/实习/兼职）',
    salary_min DECIMAL(10,2) COMMENT '薪资范围-最小值',
    salary_max DECIMAL(10,2) COMMENT '薪资范围-最大值',
    work_city VARCHAR(50) COMMENT '工作城市',
    work_address VARCHAR(255) COMMENT '工作地址',
    recruit_num INT COMMENT '招聘人数',
    education_require VARCHAR(50) COMMENT '学历要求',
    experience_require VARCHAR(50) COMMENT '经验要求',
    description TEXT COMMENT '岗位描述',
    requirements TEXT COMMENT '岗位要求',
    benefits TEXT COMMENT '福利待遇',
    tech_stack JSON COMMENT '技术栈要求（JSON数组）',
    capability_radar JSON COMMENT '能力雷达图数据（JSON对象）',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-招聘中 closed-已关闭',
    publish_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布日期',
    deadline DATE COMMENT '截止日期',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (company_id) REFERENCES t_company(id),
    INDEX idx_company_id (company_id),
    INDEX idx_position_name (position_name),
    INDEX idx_status (status)
) COMMENT='招聘岗位表';
```

### 11. 简历管理表 (t_resume)
```sql
CREATE TABLE t_resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '简历ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender TINYINT COMMENT '性别：1-男 2-女',
    birth_date DATE COMMENT '出生日期',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    education VARCHAR(50) COMMENT '学历',
    school VARCHAR(100) COMMENT '毕业院校',
    major VARCHAR(100) COMMENT '专业',
    graduation_date DATE COMMENT '毕业时间',
    skills TEXT COMMENT '专业技能',
    project_experience TEXT COMMENT '项目经验',
    internship_experience TEXT COMMENT '实习经验',
    awards TEXT COMMENT '获奖情况',
    self_evaluation TEXT COMMENT '自我评价',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    attachment_url VARCHAR(255) COMMENT '简历附件URL',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿 published-已发布',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    INDEX idx_student_id (student_id)
) COMMENT='简历表';
```

### 12. 求职申请表 (t_job_application)
```sql
CREATE TABLE t_job_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    position_id BIGINT NOT NULL COMMENT '岗位ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    resume_id BIGINT COMMENT '简历ID',
    apply_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待审核 resume_review-简历筛选 written_test-笔试 interview-面试 offer-录用 rejected-拒绝',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (position_id) REFERENCES t_recruitment_position(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    FOREIGN KEY (resume_id) REFERENCES t_resume(id),
    INDEX idx_position_id (position_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) COMMENT='求职申请表';
```

### 13. 笔试管理表 (t_written_test)
```sql
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
) COMMENT='笔试表';
```

### 14. 面试管理表 (t_interview)
```sql
CREATE TABLE t_interview (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '面试ID',
    application_id BIGINT NOT NULL COMMENT '申请ID',
    interview_date DATETIME COMMENT '面试时间',
    interview_type VARCHAR(50) COMMENT '面试类型（初试/复试/终面）',
    interview_method VARCHAR(50) COMMENT '面试方式（现场/视频/电话）',
    interviewer VARCHAR(100) COMMENT '面试官',
    interview_location VARCHAR(255) COMMENT '面试地点',
    interview_notes TEXT COMMENT '面试记录',
    result VARCHAR(20) COMMENT '结果：pass-通过 fail-未通过 pending-待定',
    reason TEXT COMMENT '录用/不录用原因',
    status VARCHAR(20) DEFAULT 'scheduled' COMMENT '状态：scheduled-已安排 completed-已完成 cancelled-已取消',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (application_id) REFERENCES t_job_application(id),
    INDEX idx_application_id (application_id)
) COMMENT='面试表';
```

### 15. Offer表 (t_offer)
```sql
CREATE TABLE t_offer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'OfferID',
    application_id BIGINT NOT NULL COMMENT '申请ID',
    offer_no VARCHAR(50) UNIQUE COMMENT 'Offer编号',
    position_id BIGINT NOT NULL COMMENT '岗位ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    company_id BIGINT NOT NULL COMMENT '企业ID',
    salary DECIMAL(10,2) COMMENT '薪资',
    start_date DATE COMMENT '入职日期',
    offer_content TEXT COMMENT 'Offer内容',
    send_date DATETIME COMMENT '发送时间',
    status VARCHAR(20) DEFAULT 'sent' COMMENT '状态：sent-已发送 accepted-已接受 rejected-已拒绝 expired-已过期',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (application_id) REFERENCES t_job_application(id),
    FOREIGN KEY (position_id) REFERENCES t_recruitment_position(id),
    FOREIGN KEY (student_id) REFERENCES t_student(id),
    FOREIGN KEY (company_id) REFERENCES t_company(id),
    INDEX idx_application_id (application_id),
    INDEX idx_student_id (student_id)
) COMMENT='Offer表';
```

### 16. 作业管理表 (t_homework)
```sql
CREATE TABLE t_homework (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '作业ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '作业标题',
    description TEXT COMMENT '作业描述',
    attachment_url VARCHAR(255) COMMENT '附件URL',
    publish_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    deadline DATETIME COMMENT '截止时间',
    total_score DECIMAL(5,2) COMMENT '总分',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-进行中 closed-已截止',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-已删除 0-未删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES t_course(id),
    INDEX idx_course_id (course_id)
) COMMENT='作业表';
```

### 17. 作业提交表 (t_homework_submission)
```sql
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
) COMMENT='作业提交表';
```

### 18. 日报管理表 (t_daily_report)
```sql
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
) COMMENT='日报表';
```

## 初始化数据

### 角色初始化数据
```sql
INSERT INTO t_role (role_code, role_name, description) VALUES
('admin', '管理员', '系统管理员，拥有所有权限'),
('college_head', '学院负责人', '学院级别的管理权限'),
('teacher', '教师', '授课教师权限'),
('student', '学员', '学员/学生权限'),
('enterprise_contact', '企业对接人', '企业招聘联系人权限');
```

### 学校初始化数据
```sql
INSERT INTO t_school (school_name, school_code, license_number, license_issue_date, license_expiry_date, province, city, contact_person, contact_phone) VALUES
('福建师范大学', 'FJNU001', '教民135000012345678', '2000-01-01', '2025-12-31', '福建省', '福州市', '张老师', '0591-12345678'),
('福建农林大学', 'FJA U001', '教民135000023456789', '2000-01-01', '2025-12-31', '福建省', '福州市', '李老师', '0591-23456789'),
('福建理工大学', 'FJUT001', '教民135000034567890', '2000-01-01', '2025-12-31', '福建省', '福州市', '王老师', '0591-34567890'),
('福州大学', 'FZU001', '教民135000045678901', '2000-01-01', '2025-12-31', '福建省', '福州市', '陈老师', '0591-45678901'),
('厦门大学', 'XMU001', '教民135000056789012', '2000-01-01', '2025-12-31', '福建省', '厦门市', '刘老师', '0592-56789012'),
('集美大学', 'JMU001', '教民135000067890123', '2000-01-01', '2025-12-31', '福建省', '厦门市', '黄老师', '0592-67890123'),
('闽南师范大学', 'MNU001', '教民135000078901234', '2000-01-01', '2025-12-31', '福建省', '漳州市', '林老师', '0596-78901234');
```

### 默认管理员账号
```sql
-- 用户名: admin，密码: admin123 (实际使用时需要加密)
INSERT INTO t_user (username, password, real_name, role_id) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1);
```

## 索引设计原则

1. 所有表都有主键 `id` (自增)
2. 所有表都有 `is_deleted` 字段用于逻辑删除
3. 所有表都有 `create_time` 和 `update_time` 字段
4. 外键字段建立索引
5. 常用查询字段建立索引
6. 唯一性约束字段建立唯一索引

## 数据库设计特点

1. **规范化设计**: 符合第三范式，减少数据冗余
2. **逻辑删除**: 所有表支持逻辑删除，保留数据历史
3. **时间戳**: 统一的创建和更新时间记录
4. **关联设计**: 合理的外键关联，确保数据一致性
5. **扩展性**: 预留扩展字段，便于后续功能增强
6. **JSON字段**: 技术栈、能力雷达图等复杂数据使用JSON存储
7. **状态管理**: 关键业务流程都有状态字段跟踪
