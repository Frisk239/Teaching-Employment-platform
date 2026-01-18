# 高校教学就业平台 - 架构分析报告

## 项目概述

高校教学就业平台是一个基于 Vue 3 + Spring Boot 的全栈应用系统，用于管理高校的教学活动和学生的就业服务。该系统实现了教务管理、学生管理、企业招聘、就业跟踪等核心功能。

## 技术栈

### 前端
- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite 5.0
- **路由**: Vue Router 4
- **状态管理**: Pinia 2
- **UI框架**: Element Plus 2.4
- **HTTP客户端**: Axios
- **样式**: SCSS (OKLCH颜色系统)
- **类型支持**: TypeScript
- **图表**: ECharts
- **工具库**: Day.js, Lodash-es

### 后端
- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.5
- **安全**: JWT (Spring Security 仅用于密码加密)
- **文档**: Knife4j 3.0
- **工具**: Hutool, Apache POI, EasyExcel
- **连接池**: Druid 1.2

## 项目结构

```
Teaching-Employment-platform/
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── api/             # API 接口定义
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # 公共组件
│   │   ├── layouts/         # 布局组件
│   │   ├── router/          # 路由配置
│   │   ├── stores/          # Pinia 状态管理
│   │   ├── styles/          # 全局样式
│   │   ├── utils/           # 工具函数
│   │   ├── views/           # 页面组件
│   │   └── App.vue          # 根组件
│   ├── public/              # 公共资源
│   ├── package.json         # 依赖管理
│   ├── vite.config.ts       # Vite 配置
│   ├── tsconfig.json        # TypeScript 配置
│   ├── .env.development    # 开发环境变量
│   └── .env.production     # 生产环境变量
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/java/       # Java 源码
│   │   └── resources/       # 资源文件
│   ├── pom.xml             # Maven 配置
│   ├── sql/                # 数据库脚本
│   └── uploads/            # 文件上传目录
└── README.md
```

## 核心功能模块

### 1. 系统管理 (仅管理员)
- 用户管理
- 角色管理
- 权限管理
- 菜单管理

### 2. 教学管理 (多角色)
- 学校管理
- 教室管理
- 课程管理
- 学员管理
- 教师管理
- 作业管理
- 日报管理
- 教学资料

### 3. 就业管理 (多角色)
- 企业管理
- 职位管理
- 求职申请
- 笔试管理
- 面试管理
- Offer管理
- 人才库
- 就业统计

### 4. 个人中心
- 个人信息
- 修改密码
- 系统设置

## 角色权限系统

### 角色定义
- **admin**: 管理员
- **college_head**: 学院负责人
- **teacher**: 教师
- **user**: 学生/学员
- **enterprise_contact**: 企业对接人

### 权限控制
- 基于角色的访问控制 (RBAC)
- 路由级权限控制
- 按钮级权限控制
- 数据级权限控制

## 路由结构

### 路由配置特点
1. **模块化路由**: 按功能模块组织路由
2. **权限路由**: 每个路由可配置访问权限
3. **嵌套路由**: 支持多级菜单结构
4. **动态路由**: 根据用户角色动态生成菜单

### 路由守卫
- 身份验证检查
- 角色权限验证
- 页面标题设置
- 登录状态恢复

## 状态管理 (Pinia)

### 主要 Store
1. **authStore**: 用户认证状态
   - 登录/注册/登出
   - Token 管理
   - 用户信息存储
   - 权限验证

2. **appStore**: 应用全局状态
   - 全局配置
   - 主题设置
   - 通知消息

## API 架构

### HTTP 客户端配置
- **基础 URL**: 可通过环境变量配置
- **请求拦截**: 自动添加 Token
- **响应拦截**: 统一错误处理
- **进度条**: NProgress 显示加载状态

### API 设计原则
- RESTful 风格
- 统一的响应格式
- 分页查询支持
- 文件上传/下载支持

## 样式系统

### 设计系统
- **颜色系统**: 基于 OKLCH 颜色空间
- **字体系统**: Inter + 中文字体
- **阴影系统**: 多级阴影
- **圆角系统**: 统一边框圆角

### SCSS 架构
- CSS 变量定义
- 组件样式隔离
- 响应式设计支持
- 主题定制能力

## 开发命令

### 前端
```bash
# 开发环境
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview

# 代码检查
npm run lint

# 代码格式化
npm run format
```

### 后端
```bash
# 启动开发服务器
mvn spring-boot:run

# 构建
mvn clean package

# 运行测试
mvn test
```

## 环境配置

### 开发环境
- 前端端口: 8081
- 后端端口: 8080
- 数据库: MySQL
- API 前缀: /api

### 代理配置
开发环境下自动代理 API 请求到后端：
- `/api` -> `http://localhost:8080`
- `/system` -> `http://localhost:8080`

## 关键特性

### 1. 自动导入
- Vue 组件自动导入
- Vue API 自动导入
- Element Plus 组件自动导入
- Element Plus 图标自动导入

### 2. 类型安全
- 完整的 TypeScript 支持
- API 类型定义
- 组件 Props 类型检查

### 3. 性能优化
- Vite 快速构建
- 组件懒加载
- 代码分割
- 资源压缩

### 4. 开发体验
- ESLint + Prettier 代码规范
- 热模块替换 (HMR)
- 错误边界处理
- 开发环境友好提示

## 部署说明

### 前端部署
1. 构建生产版本
2. 部署到静态服务器 (Nginx)
3. 配置环境变量

### 后端部署
1. 构建 JAR 包
2. 配置数据库连接
3. 配置文件上传路径
4. 启动 Spring Boot 应用

## 安全注意事项

1. **Token 安全**
   - 使用 HTTPS 传输
   - Token 过期时间控制
   - 刷新 Token 机制

2. **权限控制**
   - 前端路由权限验证
   - 后端接口权限验证
   - 敏感数据加密

3. **数据安全**
   - 密码加密存储
   - SQL 注入防护
   - XSS 防护

## 后续优化方向

1. **性能优化**
   - 图片懒加载
   - 虚拟滚动
   - 缓存策略

2. **功能扩展**
   - 即时通讯
   - 文件预览
   - 数据导出

3. **用户体验**
   - 深色模式
   - 国际化
   - 无障碍支持

## 总结

这是一个结构清晰、技术栈现代化的高校教学就业平台项目。采用了当前主流的前后端分离架构，具有良好的可维护性和扩展性。项目实现了一个完整的权限控制系统，支持多角色协作，能够满足高校教学和就业管理的各项需求。

代码组织规范，使用了 TypeScript 提供类型安全，SCSS 提供了良好的样式管理，Vue 3 的 Composition API 使得组件逻辑更加清晰。整体架构设计合理，适合团队协作开发。

