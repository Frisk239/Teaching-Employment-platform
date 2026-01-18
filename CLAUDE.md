# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个高校教学就业平台，采用前后端分离架构，支持管理员、学院负责人、教师、学员和企业对接人五种角色的权限管理。

- **前端**: `frontend/` - Vue 3 + TypeScript + Vite + Element Plus
- **后端**: `backend/` - Spring Boot 2.7.18 + MyBatis Plus + MySQL

## 开发命令

### 前端 (frontend/)
```bash
cd frontend
npm install          # 安装依赖
npm run dev          # 启动开发服务器 (http://localhost:8081)
npm run build        # 构建生产版本
npm run lint         # ESLint 代码检查
npm run format       # Prettier 代码格式化
```

### 后端 (backend/)
```bash
cd backend
mvn clean install    # 安装依赖
mvn spring-boot:run  # 启动后端服务器 (http://localhost:8080)
```

后端 API 基础路径: `/api`
Knife4j API 文档: http://localhost:8080/api/doc.html

## 架构设计

### 角色权限系统

平台支持 5 种角色（roleId 对应关系）:
- `admin` (1): 管理员 - 拥有所有权限
- `college_head` (2): 学院负责人 - 管理教学和就业
- `teacher` (3): 教师 - 管理课程和学员
- `user` (4): 学员 - 学员身份
- `enterprise_contact` (5): 企业对接人 - 管理企业招聘

**权限控制层级**:
1. **路由级**: `routes.ts` 中的 `meta.roles` 控制页面访问
2. **组件级**: 使用自定义指令 `v-permission` 控制元素显示
3. **数据级**: 后端根据用户角色过滤数据

### 前端架构

```
frontend/src/
├── api/              # API 模块
│   ├── *.ts          # 按业务模块划分的 API (auth, company, student 等)
│   └── types.ts      # TypeScript 类型定义
├── assets/           # 静态资源
├── components/       # 公共组件 (自动注册)
├── layouts/          # 布局组件
│   └── MainLayout.vue  # 主布局 (侧边栏、顶栏)
├── router/           # 路由配置
│   ├── index.ts      # 路由实例和守卫
│   └── routes.ts     # 路由定义
├── stores/           # Pinia 状态管理
│   └── auth.ts       # 认证状态
├── styles/           # 全局样式 (SCSS with OKLCH 颜色)
├── utils/            # 工具函数
│   └── request.ts    # Axios 封装 (拦截器、错误处理)
└── views/            # 页面组件
    ├── auth/         # 登录/注册
    ├── dashboard/    # 首页
    ├── system/       # 系统管理 (仅管理员)
    ├── teaching/     # 教学管理
    ├── employment/   # 就业管理
    └── profile/      # 个人中心
```

**关键配置**:
- **自动导入**: `unplugin-auto-import` - Vue/Pinia API 自动导入
- **组件自动注册**: `unplugin-vue-components` - Element Plus 组件自动注册
- **代理配置**: `vite.config.ts` - `/api` 和 `/system` 代理到后端 8080 端口
- **路径别名**: `@` 指向 `src/`

### 后端架构

```
backend/src/main/java/com/teaching/employment/
├── common/           # 公共类 (Result, 分页等)
├── config/           # 配置类
├── controller/       # REST API 控制器
├── entity/           # 实体类 (@TableName 对应数据库表)
├── dto/              # 数据传输对象
├── enums/            # 枚举类
├── exception/        # 异常处理
├── mapper/           # MyBatis Plus Mapper
├── security/         # JWT 认证
│   └── JwtUtil.java  # JWT 工具类
└── service/          # 业务逻辑层
```

**核心模块**:
- **认证**: `AuthController` - 登录/注册/获取当前用户
- **教学**: 学校、教室、课程、学员、教师、作业、日报、教学资料管理
- **就业**: 企业、职位、求职申请、笔试、面试、Offer、人才库管理
- **统计**: 就业数据统计和可视化

### 数据库设计

核心实体关系:
- `t_user` - 用户表 (包含 roleId, schoolId, companyId)
- `t_role` - 角色表
- `t_school` - 学校表
- `t_student` - 学员表 (关联 userId)
- `t_teacher` - 教师表 (关联 userId)
- `t_company` - 企业表
- `t_position` - 职位表
- `t_job_application` - 求职申请表
- `t_course` - 课程表
- `t_homework` - 作业表
- `t_daily_report` - 日报表
- `t_offer` - Offer 表

## 认证流程

1. **登录**: `POST /api/auth/login` → 返回 `{ token, user, roleCode, studentId?, teacherId?, companyId? }`
2. **Token 存储**: localStorage (记住我) 或 sessionStorage
3. **请求拦截**: 自动添加 `Authorization: Bearer {token}` 头
4. **状态恢复**: 应用启动时调用 `restoreAuth()` 恢复登录状态
5. **Token 验证**: 每次请求后端验证 JWT，过期返回 401 跳转登录页

## 开发注意事项

### 前端
- 使用 **Composition API** (`<script setup>`)
- 所有 Element Plus 组件无需手动导入，自动注册
- Vue/Pinia API 无需导入，自动可用
- 路由守卫在 `router/index.ts:26` 处理权限检查
- API 请求统一使用 `utils/request.ts` 中的 `http` 对象
- SCSS 变量定义在 `styles/variables.scss` (使用 OKLCH 颜色系统)

### 后端
- 统一返回格式: `Result<T>` 包含 `code`, `message`, `data`
- 使用 `@ApiModelProperty` 注解添加 Swagger 文档
- MyBatis Plus 的 `@TableField(exist = false)` 标记非数据库字段
- JWT 过期时间: 7 天 (配置在 `application.yml`)
- 逻辑删除已禁用，使用物理删除

### 角色特定 ID
登录响应中的角色特定 ID (用于关联查询):
- **学员**: `studentId` - `t_student.id`
- **教师**: `teacherId` - `t_teacher.id`
- **企业对接人**: `companyId` - `t_company.id` (直接来自 user.companyId)

这些 ID 存储在 authStore 中，在相关 API 调用时使用。

### 文件上传
- 前端: 使用 `http.upload(url, formData)`
- 后端: `FileUploadController` 处理文件上传
- 限制: 单文件最大 10MB，请求最大 20MB

### API 文档
开发时可访问 Knife4j 查看所有 API 接口和测试: http://localhost:8080/api/doc.html

## 测试数据

### 数据库初始化
测试数据 SQL 文件位于 `backend/src/main/resources/`:
- `init.sql` - 数据库表结构初始化
- `test-data.sql` - 基础测试数据（管理员、学员、教师等）
- `enterprise-test-data.sql` - 企业负责人测试数据

### 企业负责人测试账号
文档: `backend/ENTERPRISE_TEST_DATA_README.md`

**测试账号** (密码均为 `123456`):
- `enterprise11` - 阿里巴巴负责人（张伟）
- `enterprise21` - 腾讯负责人（王强）
- `enterprise31` - 字节跳动负责人（陈明）
- `enterprise41` - 华为负责人（杨秀兰）
- `enterprise51` - 美团负责人（孙丽）
- ... 共15个企业负责人账号

**导入测试数据**:
```bash
mysql -u root -p teaching_employment_platform < backend/src/main/resources/enterprise-test-data.sql
```

### 其他测试账号
- **管理员**: `admin` / `123456`
- **学院负责人**: 查看数据库 `t_user` 表中 role_id=2 的用户
- **教师**: 查看数据库 `t_user` 表中 role_id=3 的用户
- **学员**: 查看数据库 `t_user` 表中 role_id=4 的用户
