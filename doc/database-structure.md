# 高校教学就业平台 - 数据库结构文档

## 数据库基本信息
- **数据库名称**: `teaching_employment_platform`
- **字符集**: `utf8mb4`
- **整理规则**: `utf8mb4_general_ci`

## 核心业务表

### 1. 用户表 (t_user)
用户基础信息表,存储所有类型的用户账号信息。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 用户ID |
| username | varchar(50) | NO | UNI | | 用户名(唯一) |
| password | varchar(255) | NO | | | 密码(加密) |
| real_name | varchar(50) | NO | | | 真实姓名 |
| phone | varchar(20) | YES | | | 手机号 |
| email | varchar(100) | YES | | | 邮箱 |
| avatar | varchar(255) | YES | | | 头像URL |
| role_id | bigint | NO | MUL | | 角色ID(关联t_role) |
| school_id | bigint | YES | MUL | | 所属学校ID |
| status | tinyint | YES | | 1 | 状态(1-启用 0-禁用) |
| is_deleted | tinyint | YES | | 0 | 是否删除(1-已删除 0-未删除) |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `role_id` → `t_role.id`
- `school_id` → `t_school.id`

---

### 2. 学校表 (t_school)
教育培训机构信息表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 学校ID |
| school_name | varchar(100) | NO | UNI | | 学校名称(唯一) |
| school_code | varchar(50) | NO | UNI | | 学校代码(唯一) |
| license_number | varchar(100) | NO | | | 办学许可证号 |
| license_issue_date | date | YES | | | 许可证颁发日期 |
| license_expiry_date | date | YES | | | 许可证过期日期 |
| license_image_url | varchar(255) | YES | | | 许可证图片URL |
| province | varchar(50) | YES | | | 省份 |
| city | varchar(50) | YES | | | 城市 |
| address | varchar(255) | YES | | | 详细地址 |
| website | varchar(255) | YES | | | 官网地址 |
| contact_person | varchar(50) | YES | | | 联系人 |
| contact_phone | varchar(20) | YES | | | 联系电话 |
| email | varchar(100) | YES | | | 邮箱 |
| description | text | YES | | | 学校描述 |
| status | tinyint | YES | MUL | 1 | 状态(1-正常 0-禁用) |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

---

### 3. 教师表 (t_teacher)
教师个人信息表,关联用户表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 教师ID |
| user_id | bigint | NO | UNI | | 用户ID(关联t_user) |
| teacher_no | varchar(50) | NO | UNI | | 教师工号(唯一) |
| school_id | bigint | NO | MUL | | 所属学校ID |
| department | varchar(100) | YES | | | 所属院系 |
| title | varchar(50) | YES | | | 职称 |
| education | varchar(50) | YES | | | 学历 |
| specialty | varchar(100) | YES | | | 专业领域 |
| entry_date | date | YES | | | 入职日期 |
| id_card | varchar(18) | YES | | | 身份证号 |
| gender | tinyint | YES | | | 性别(1-男 2-女) |
| birth_date | date | YES | | | 出生日期 |
| address | varchar(255) | YES | | | 家庭地址 |
| description | text | YES | | | 个人简介 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `user_id` → `t_user.id`
- `school_id` → `t_school.id`

**注意**: 教师的真实姓名存储在 `t_user.real_name` 中,需要通过 `user_id` 关联查询。

---

### 4. 学生表 (t_student)
学生个人信息表,关联用户表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 学生ID |
| user_id | bigint | NO | UNI | | 用户ID(关联t_user) |
| student_no | varchar(50) | NO | UNI | | 学号(唯一) |
| school_id | bigint | NO | MUL | | 所属学校ID |
| class_name | varchar(50) | YES | | | 班级名称 |
| major | varchar(100) | YES | | | 专业 |
| grade | varchar(20) | YES | | | 年级 |
| gender | tinyint | NO | | | 性别(1-男 2-女) |
| birth_date | date | YES | | | 出生日期 |
| id_card | varchar(18) | NO | MUL | | 身份证号 |
| phone | varchar(20) | NO | | | 手机号 |
| email | varchar(100) | YES | | | 邮箱 |
| address | varchar(255) | YES | | | 家庭地址 |
| enrollment_date | date | YES | | | 入学日期 |
| graduation_date | date | YES | | | 毕业日期 |
| political_status | varchar(50) | YES | | | 政治面貌 |
| nation | varchar(50) | YES | | | 民族 |
| guardian_name | varchar(50) | YES | | | 监护人姓名 |
| guardian_phone | varchar(20) | YES | | | 监护人电话 |
| description | text | YES | | | 个人描述 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `user_id` → `t_user.id`
- `school_id` → `t_school.id`

**注意**: 学生的真实姓名存储在 `t_user.real_name` 中,需要通过 `user_id` 关联查询。

---

### 5. 教室表 (t_classroom)
教室资源信息表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 教室ID |
| classroom_no | varchar(50) | NO | MUL | | 教室编号(如A101) |
| classroom_name | varchar(100) | NO | | | 教室名称 |
| school_id | bigint | NO | MUL | | 所属学校ID |
| building | varchar(50) | YES | | | 楼栋 |
| floor | varchar(20) | YES | | | 楼层 |
| capacity | int | YES | | | 容纳人数 |
| classroom_type | varchar(50) | YES | | | 教室类型 |
| has_projector | tinyint | YES | | 0 | 是否有投影仪(1-是 0-否) |
| has_computer | tinyint | YES | | 0 | 是否有电脑(1-是 0-否) |
| has_multimedia | tinyint | YES | | 0 | 是否多媒体教室(1-是 0-否) |
| equipment | text | YES | | | 设备描述 |
| status | tinyint | YES | | 1 | 状态(1-可用 0-不可用) |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `school_id` → `t_school.id`

---

### 6. 课程表 (t_course)
课程信息表,核心业务表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 课程ID |
| course_name | varchar(100) | NO | MUL | | 课程名称 |
| course_code | varchar(50) | YES | | | 课程代码(如CS101) |
| course_type | varchar(50) | NO | | | 课程类型(普通课程/直播课程) |
| school_id | bigint | NO | MUL | | 开课学校ID |
| teacher_id | bigint | NO | MUL | | 授课教师ID |
| classroom_id | bigint | YES | MUL | | 教室ID |
| description | text | YES | | | 课程描述 |
| credit | decimal(3,1) | YES | | | 学分 |
| total_hours | int | YES | | | 总课时 |
| start_date | date | YES | | | 开课日期 |
| end_date | date | YES | | | 结课日期 |
| max_students | int | YES | | | 最大学员数 |
| current_students | int | YES | | 0 | 当前学员数 |
| status | varchar(20) | YES | | pending | 状态(pending-未开始 ongoing-进行中 completed-已完成 cancelled-已取消) |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `school_id` → `t_school.id`
- `teacher_id` → `t_teacher.id`
- `classroom_id` → `t_classroom.id`

**关联查询字段** (不映射到数据库):
- `schoolName`: 学校名称(需要关联 t_school 查询)
- `teacherName`: 教师姓名(需要通过 t_teacher 关联 t_user 查询 real_name)
- `classroomName`: 教室名称(需要关联 t_classroom 查询)

**状态值说明**:
- `pending`: 未开始
- `ongoing`: 进行中
- `completed`: 已完成
- `cancelled`: 已取消

---

### 7. 课程学生关联表 (t_course_student)
学生选课记录表,多对多关系。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 记录ID |
| course_id | bigint | NO | MUL | | 课程ID |
| student_id | bigint | NO | MUL | | 学生ID |
| enrollment_date | date | YES | | | 选课日期 |
| status | varchar(20) | YES | | | 状态 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `course_id` → `t_course.id`
- `student_id` → `t_student.id`

---

### 8. 企业表 (t_company)
企业招聘单位信息表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 企业ID |
| company_name | varchar(100) | NO | MUL | | 企业名称 |
| company_code | varchar(50) | YES | UNI | | 企业代码(唯一) |
| business_license | varchar(100) | YES | | | 营业执照号 |
| industry | varchar(50) | YES | | | 所属行业 |
| company_size | varchar(50) | YES | | | 企业规模 |
| province | varchar(50) | YES | | | 省份 |
| city | varchar(50) | YES | | | 城市 |
| address | varchar(255) | YES | | | 详细地址 |
| website | varchar(255) | YES | | | 官网地址 |
| contact_person | varchar(50) | YES | | | 联系人 |
| contact_phone | varchar(20) | YES | | | 联系电话 |
| email | varchar(100) | YES | | | 邮箱 |
| description | text | YES | | | 企业描述 |
| status | tinyint | YES | | 1 | 状态(1-正常 0-禁用) |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |
| credit_code | varchar(50) | YES | | | 统一社会信用代码 |
| short_name | varchar(50) | YES | | | 企业简称 |
| logo | varchar(255) | YES | | | 企业Logo |
| scale | varchar(50) | YES | | | 规模 |
| stage | varchar(50) | YES | | | 发展阶段 |
| benefits | text | YES | | | 福利待遇 |
| contact_name | varchar(50) | YES | | | 联系人姓名 |
| contact_position | varchar(50) | YES | | | 联系人职位 |
| contact_email | varchar(100) | YES | | | 联系邮箱 |
| verify_status | varchar(20) | YES | | pending | 认证状态(pending-待审核 approved-已认证 rejected-已拒绝) |
| verify_time | datetime | YES | | | 认证时间 |
| reject_reason | text | YES | | | 拒绝原因 |

---

### 9. 招聘岗位表 (t_recruitment_position)
企业招聘岗位信息表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 岗位ID |
| company_id | bigint | NO | MUL | | 企业ID |
| position_name | varchar(100) | NO | | | 岗位名称 |
| position_type | varchar(50) | YES | | | 岗位类型(fulltime-全职 parttime-兼职 internship-实习) |
| salary_min | decimal(10,2) | YES | | | 最低薪资 |
| salary_max | decimal(10,2) | YES | | | 最高薪资 |
| location | varchar(100) | YES | | | 工作地点 |
| requirements | text | YES | | | 岗位要求 |
| description | text | YES | | | 岗位描述 |
| recruitment_count | int | YES | | | 招聘人数 |
| status | varchar(20) | YES | | | 状态(active- active暂停) |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `company_id` → `t_company.id`

---

### 10. 作业表 (t_homework)
教师发布的作业信息表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 作业ID |
| course_id | bigint | NO | MUL | | 课程ID |
| teacher_id | bigint | NO | | | 教师ID |
| title | varchar(200) | NO | | | 作业标题 |
| description | text | YES | | | 作业描述 |
| homework_type | varchar(50) | YES | | | 作业类型 |
| attachment_url | varchar(255) | YES | | | 附件URL |
| deadline | datetime | YES | | | 截止时间 |
| max_score | int | YES | | | 满分分数 |
| status | varchar(20) | YES | | draft | 状态(draft-草稿 published-已发布) |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `course_id` → `t_course.id`
- `teacher_id` → `t_teacher.id`

---

### 11. 作业提交表 (t_homework_submission)
学生提交作业的记录表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 提交ID |
| homework_id | bigint | NO | | | 作业ID |
| student_id | bigint | NO | | | 学生ID |
| submission_url | varchar(255) | YES | | | 提交文件URL |
| score | int | YES | | | 得分 |
| comment | text | YES | | | 评语 |
| submit_time | datetime | YES | | | 提交时间 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `homework_id` → `t_homework.id`
- `student_id` → `t_student.id`

---

### 12. 日报表 (t_daily_report)
学生实习日报表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 日报ID |
| student_id | bigint | NO | MUL | | 学生ID |
| report_date | date | NO | | | 日报日期 |
| content | text | NO | | | 日报内容 |
| work_hours | decimal(4,1) | YES | | | 工作时长 |
| tomorrow_plan | text | YES | | | 明日计划 |
| issues | text | YES | | | 遇到问题 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `student_id` → `t_student.id`

---

## 系统管理表

### 13. 角色表 (t_role)
系统角色表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 角色ID |
| role_name | varchar(50) | NO | | | 角色名称 |
| role_code | varchar(50) | YES | | | 角色代码 |
| description | varchar(200) | YES | | | 角色描述 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

---

### 14. 菜单表 (t_menu)
系统菜单表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 菜单ID |
| menu_name | varchar(50) | NO | | | 菜单名称 |
| menu_type | tinyint | NO | | | 菜单类型(1-目录 2-菜单 3-按钮) |
| parent_id | bigint | YES | | | 父菜单ID |
| path | varchar(200) | YES | | | 路由路径 |
| permission | varchar(100) | YES | | | 权限标识 |
| icon | varchar(100) | YES | | | 菜单图标 |
| sort_order | int | YES | | | 排序 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

---

### 15. 权限表 (t_permission)
系统权限表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 权限ID |
| permission_name | varchar(50) | NO | | | 权限名称 |
| permission_code | varchar(100) | NO | | | 权限代码 |
| resource_type | varchar(50) | YES | | | 资源类型 |
| resource_url | varchar(200) | YES | | | 资源URL |
| description | varchar(200) | YES | | | 权限描述 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

---

### 16. 角色菜单关联表 (t_role_menu_relation)
角色和菜单的多对多关联表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 关联ID |
| role_id | bigint | NO | | | 角色ID |
| menu_id | bigint | NO | | | 菜单ID |

**关联关系**:
- `role_id` → `t_role.id`
- `menu_id` → `t_menu.id`

---

### 17. 角色权限关联表 (t_role_permission_relation)
角色和权限的多对多关联表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 关联ID |
| role_id | bigint | NO | | | 角色ID |
| permission_id | bigint | NO | | | 权限ID |

**关联关系**:
- `role_id` → `t_role.id`
- `permission_id` → `t_permission.id`

---

## 其他业务表

### 18. 求职申请表 (t_job_application)
学生求职申请记录表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 申请ID |
| student_id | bigint | NO | | | 学生ID |
| position_id | bigint | NO | | | 岗位ID |
| resume_id | bigint | YES | | | 简历ID |
| status | varchar(20) | YES | | | 状态 |
| apply_time | datetime | YES | | | 申请时间 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `student_id` → `t_student.id`
- `position_id` → `t_recruitment_position.id`
- `resume_id` → `t_resume.id`

---

### 19. 简历表 (t_resume)
学生简历表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 简历ID |
| student_id | bigint | NO | | | 学生ID |
| title | varchar(100) | YES | | | 简历标题 |
| content | text | YES | | | 简历内容 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `student_id` → `t_student.id`

---

### 20. Offer表 (t_offer)
企业发放的Offer记录表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | OfferID |
| student_id | bigint | NO | | | 学生ID |
| company_id | bigint | NO | | | 企业ID |
| position_id | bigint | NO | | | 岗位ID |
| salary | decimal(10,2) | YES | | | 薪资 |
| status | varchar(20) | YES | | | 状态 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `student_id` → `t_student.id`
- `company_id` → `t_company.id`
- `position_id` → `t_recruitment_position.id`

---

### 21. 笔试表 (t_written_test)
笔试信息表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 笔试ID |
| company_id | bigint | NO | | | 企业ID |
| position_id | bigint | YES | | | 岗位ID |
| test_date | datetime | YES | | | 笔试时间 |
| location | varchar(200) | YES | | | 笔试地点 |
| duration | int | YES | | | 时长(分钟) |
| total_score | int | YES | | | 总分 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `company_id` → `t_company.id`
- `position_id` → `t_recruitment_position.id`

---

### 22. 学生笔试成绩表 (t_student_exam_record)
学生笔试成绩记录表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 记录ID |
| exam_id | bigint | NO | | | 笔试ID |
| student_id | bigint | NO | | | 学生ID |
| score | int | YES | | | 得分 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `exam_id` → `t_exam.id` (或 t_written_test.id)
- `student_id` → `t_student.id`

---

### 23. 面试表 (t_interview)
面试信息表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 面试ID |
| company_id | bigint | NO | | | 企业ID |
| position_id | bigint | YES | | | 岗位ID |
| interview_date | datetime | YES | | | 面试时间 |
| location | varchar(200) | YES | | | 面试地点 |
| interview_type | varchar(50) | YES | | | 面试类型 |
| status | varchar(20) | YES | | | 状态 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `company_id` → `t_company.id`
- `position_id` → `t_recruitment_position.id`

---

### 24. 通知表 (t_notification)
系统通知表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 通知ID |
| title | varchar(200) | NO | | | 通知标题 |
| content | text | YES | | | 通知内容 |
| receiver_id | bigint | YES | | | 接收者ID |
| type | varchar(50) | YES | | | 通知类型 |
| is_read | tinyint | YES | | 0 | 是否已读(1-已读 0-未读) |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

---

### 25. 课程记录表 (t_course_record)
课程教学记录表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 记录ID |
| course_id | bigint | NO | | | 课程ID |
| record_date | date | YES | | | 上课日期 |
| content | text | YES | | | 教学内容 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

**关联关系**:
- `course_id` → `t_course.id`

---

### 26. 题目表 (t_question)
考试题目表。

| 字段名 | 类型 | 是否为空 | 键 | 默认值 | 说明 |
|--------|------|---------|-----|--------|------|
| id | bigint | NO | PRI | auto_increment | 题目ID |
| question_type | varchar(50) | YES | | | 题目类型 |
| content | text | YES | | | 题目内容 |
| options | text | YES | | | 选项(JSON格式) |
| answer | text | YES | | | 答案 |
| score | int | YES | | | 分值 |
| is_deleted | tinyint | YES | | 0 | 是否删除 |
| create_time | datetime | YES | | CURRENT_TIMESTAMP | 创建时间 |
| update_time | datetime | YES | | CURRENT_TIMESTAMP | 更新时间 |

---

## 关联查询说明

### 课程表 (t_course) 关联查询

课程表需要关联多个表来获取完整的显示信息:

1. **学校名称 (schoolName)**
   ```sql
   SELECT s.school_name
   FROM t_school s
   WHERE s.id = t_course.school_id
   ```

2. **教师姓名 (teacherName)**
   ```sql
   SELECT u.real_name
   FROM t_teacher t
   JOIN t_user u ON t.user_id = u.id
   WHERE t.id = t_course.teacher_id
   ```

3. **教室名称 (classroomName)**
   ```sql
   SELECT c.classroom_name
   FROM t_classroom c
   WHERE c.id = t_course.classroom_id
   ```

### 完整的关联查询示例

```sql
SELECT
    c.*,
    s.school_name,
    u.real_name AS teacher_name,
    cl.classroom_name
FROM t_course c
LEFT JOIN t_school s ON c.school_id = s.id
LEFT JOIN t_teacher t ON c.teacher_id = t.id
LEFT JOIN t_user u ON t.user_id = u.id
LEFT JOIN t_classroom cl ON c.classroom_id = cl.id
WHERE c.is_deleted = 0
ORDER BY c.create_time DESC;
```

## 数据字典注意事项

1. **教师和学生的姓名**: 存储在 `t_user.real_name` 中,需要通过 `user_id` 关联查询
2. **课程状态**: 使用字符串类型,值为 `pending/ongoing/completed/cancelled`
3. **逻辑删除**: 大部分表使用 `is_deleted` 字段进行逻辑删除(1-已删除 0-未删除)
4. **时间戳**: 所有表都有 `create_time` 和 `update_time` 字段
5. **外键约束**: 数据库层面没有设置外键约束,通过应用层维护关联关系

## 索引说明

主要索引字段:
- **唯一索引**: username, school_code, license_number, teacher_no, student_no, classroom_no等
- **普通索引**: school_id, teacher_id, classroom_id, course_id, student_id, company_id, position_id等外键字段
- **联合索引**: 根据查询需求可能需要在多个表上建立联合索引

---

**文档版本**: 1.0
**最后更新**: 2026-01-14
**维护者**: Teaching Employment Platform Team
