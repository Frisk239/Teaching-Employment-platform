# 企业负责人测试数据说明

## 概述
此测试数据用于企业负责人（企业对接人）功能的开发和测试。

## 数据文件位置
```
backend/src/main/resources/enterprise-test-data.sql
```

## 测试数据内容

### 1. 企业负责人用户（共15个账号）
每个企业配置1-2名企业负责人，用户名格式：`enterprise+公司ID+编号`

| 企业 | 账号 | 密码 | 姓名 | 说明 |
|------|------|------|------|------|
| 阿里巴巴 | enterprise11 | 123456 | 张伟 | 主要负责人 |
| 阿里巴巴 | enterprise12 | 123456 | 李娜 | 备用负责人 |
| 腾讯 | enterprise21 | 123456 | 王强 | 主要负责人 |
| 腾讯 | enterprise22 | 123456 | 刘洋 | 备用负责人 |
| 字节跳动 | enterprise31 | 123456 | 陈明 | 唯一负责人 |
| 华为 | enterprise41 | 123456 | 杨秀兰 | 主要负责人 |
| 华为 | enterprise42 | 123456 | 赵军 | 备用负责人 |
| 美团 | enterprise51 | 123456 | 孙丽 | 唯一负责人 |
| 京东 | enterprise61 | 123456 | 周涛 | 主要负责人 |
| 京东 | enterprise62 | 123456 | 吴杰 | 备用负责人 |
| 网易 | enterprise71 | 123456 | 郑敏 | 唯一负责人 |
| 百度 | enterprise81 | 123456 | 王磊 | 主要负责人 |
| 百度 | enterprise82 | 123456 | 冯静 | 备用负责人 |
| 小米 | enterprise91 | 123456 | 陈超 | 唯一负责人 |
| 滴滴 | enterprise101 | 123456 | 褚欣 | 主要负责人 |
| 滴滴 | enterprise102 | 123456 | 卫强 | 备用负责人 |

### 2. 更新的企业信息
- 所有10个企业的联系人信息已更新
- 联系人指向企业负责人账号
- 包含联系人姓名、职位、电话、邮箱

### 3. 职位数据更新
- 更新了所有10个职位的招聘人数
- 完善了职位描述信息

### 4. 求职申请数据（10条）
为每个学生创建了求职申请，包含不同状态：
- pending: 待处理
- written_test_scheduled: 笔试已安排
- written_test_passed: 笔试已通过
- written_test_failed: 笔试未通过
- interview_scheduled: 面试已安排
- interview_passed: 面试已通过
- interview_failed: 面试未通过
- rejected: 已拒绝

### 5. 笔试数据（5条）
包含不同状态的笔试记录：
- scheduled: 已安排
- passed: 已通过
- failed: 未通过

### 6. 面试数据（4条）
包含不同状态的面试记录：
- scheduled: 已安排
- passed: 已通过
- failed: 未通过

### 7. Offer数据（1条）
新增1个待确认的Offer记录

### 8. 通知数据（13条）
为企业负责人创建各种类型的通知：
- info: 信息通知
- success: 成功通知
- warning: 警告通知
- 涵盖简历投递、面试安排、笔试成绩等场景

## 如何使用

### 方式一：使用MySQL客户端执行
```bash
mysql -u root -p teaching_employment_platform < backend/src/main/resources/enterprise-test-data.sql
```

### 方式二：在数据库管理工具中执行
1. 打开Navicat、MySQL Workbench等工具
2. 连接到数据库
3. 打开enterprise-test-data.sql文件
4. 执行全部SQL语句

### 方式三：使用Maven命令（如果配置了SQL插件）
```bash
cd backend
mvn sql:execute
```

## 注意事项

1. **角色ID**: 企业负责人的role_id = 5 (enterprise_contact)
2. **密码加密**: 所有密码都是123456的BCrypt加密
3. **依赖数据**: 此脚本依赖已有的企业数据(t_company)和学生数据(t_student, t_user)
4. **执行顺序**: 建议先执行init.sql，再执行test-data.sql，最后执行enterprise-test-data.sql

## 测试场景覆盖

### 企业负责人登录
- 15个不同企业的负责人账号
- 可测试不同企业的权限隔离

### 简历管理
- 查看待处理的简历投递
- 查看已处理的简历列表
- 筛选不同状态的申请

### 笔试管理
- 安排笔试时间
- 录入笔试成绩
- 查看笔试统计

### 面试管理
- 安排面试时间
- 记录面试反馈
- 评估面试结果

### Offer管理
- 发送Offer
- 查看Offer状态
- 跟踪Offer确认情况

### 数据统计
- 职位申请统计
- 笔试通过率
- 面试通过率
- Offer接受率

## 相关表结构

### t_user (用户表)
- id: 用户ID
- username: 用户名
- password: 密码
- real_name: 真实姓名
- phone: 手机号
- email: 邮箱
- role_id: 角色ID（5=企业负责人）

### t_company (企业表)
- id: 企业ID
- company_name: 企业名称
- contact_name: 联系人姓名
- contact_position: 联系人职位
- contact_phone: 联系人电话
- contact_email: 联系人邮箱

### t_job_application (求职申请表)
- student_id: 学生ID
- position_id: 职位ID
- company_id: 企业ID
- status: 申请状态

### t_written_test (笔试表)
- application_id: 申请ID
- company_id: 企业ID
- position_id: 职位ID
- test_score: 笔试成绩
- test_status: 笔试状态

### t_interview (面试表)
- application_id: 申请ID
- company_id: 企业ID
- position_id: 职位ID
- interview_round: 面试轮次
- interview_status: 面试状态

### t_notification (通知表)
- user_id: 用户ID
- type: 通知类型
- title: 标题
- content: 内容

## 常见问题

### Q1: 如何查看企业负责人的user_id？
A: 用户ID从11开始递增，因为之前已有10个学生用户（ID 1-10）。

### Q2: 企业负责人如何关联到企业？
A: 目前通过用户名规则（enterprise+公司ID）和企业的联系人信息进行逻辑关联。

### Q3: 如何测试企业负责人Dashboard？
A: 使用enterprise11账号登录，应该能看到阿里巴巴相关的统计数据和操作。

### Q4: 数据重复怎么办？
A: 脚本已考虑幂等性，但建议先清空相关测试数据：
```sql
DELETE FROM t_notification WHERE user_id >= 11;
DELETE FROM t_offer WHERE student_id IN (1-10);
DELETE FROM t_interview;
DELETE FROM t_written_test;
DELETE FROM t_job_application WHERE id > 14;
DELETE FROM t_user WHERE role_id = 5;
```

## 后续扩展

如需添加更多测试数据，参考以下格式：

```sql
-- 添加企业负责人用户
INSERT INTO t_user (username, password, real_name, phone, email, role_id, status)
VALUES ('enterprise111', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '姓名', '手机号', '邮箱', 5, 1);

-- 添加求职申请
INSERT INTO t_job_application (student_id, position_id, company_id, status, apply_time)
VALUES (学生ID, 职位ID, 企业ID, '状态', NOW());

-- 添加笔试记录
INSERT INTO t_written_test (application_id, company_id, position_id, test_date, test_status)
VALUES (申请ID, 企业ID, 职位ID, DATE_ADD(NOW(), INTERVAL 2 DAY), 'scheduled');

-- 添加面试记录
INSERT INTO t_interview (application_id, company_id, position_id, interview_round, interview_date, interview_status)
VALUES (申请ID, 企业ID, 职位ID, 1, DATE_ADD(NOW(), INTERVAL 5 DAY), 'scheduled');
```

## 联系方式
如有问题，请联系开发团队。
