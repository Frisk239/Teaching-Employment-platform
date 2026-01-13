# 高校教学就业平台 - 前端项目

基于 Vue 3 + TypeScript + Element Plus + Vite 构建的现代化就业管理平台前端项目。

## 技术栈

- **框架**: Vue 3.3.11 (Composition API + `<script setup>`)
- **构建工具**: Vite 5.0.8
- **语言**: TypeScript 5.3.3
- **UI 组件库**: Element Plus 2.4.4
- **路由**: Vue Router 4.2.5
- **状态管理**: Pinia 2.1.7
- **HTTP 客户端**: Axios 1.6.5
- **图表库**: ECharts 5.4.3
- **CSS 预处理**: Sass
- **代码规范**: ESLint + Prettier

## 项目结构

```
Teaching-Employment-platform/
├── frontend/              # 前端项目目录
│   ├── src/
│   ├── api/              # API 接口模块
│   │   ├── types.ts      # 类型定义
│   │   ├── auth.ts       # 认证接口
│   │   ├── company.ts    # 企业接口
│   │   ├── recruitment.ts # 职位接口
│   │   ├── application.ts # 申请接口
│   │   ├── student.ts    # 学生接口
│   │   ├── statistics.ts # 统计接口
│   │   ├── file.ts       # 文件接口
│   │   └── index.ts      # 统一导出
│   ├── assets/           # 静态资源
│   ├── components/       # 组件
│   │   └── layout/       # 布局组件
│   │       ├── MainLayout.vue
│   │       ├── Sidebar.vue
│   │       └── Header.vue
│   ├── composables/      # 组合式函数
│   ├── router/           # 路由
│   │   ├── index.ts
│   │   └── routes.ts
│   ├── stores/           # Pinia 状态管理
│   │   ├── auth.ts       # 认证状态
│   │   ├── app.ts        # 应用状态
│   │   └── index.ts
│   ├── styles/           # 样式
│   │   ├── variables.scss # 样式变量
│   │   └── global.scss   # 全局样式
│   ├── utils/            # 工具函数
│   │   ├── request.ts    # Axios 封装
│   │   └── index.ts      # 工具函数
│   ├── views/            # 页面
│   │   ├── auth/         # 认证页面
│   │   ├── dashboard/    # 仪表盘
│   │   ├── employment/   # 就业管理
│   │   ├── student/      # 学生管理
│   │   ├── course/       # 课程管理
│   │   ├── system/       # 系统设置
│   │   └── error/        # 错误页面
│   ├── App.vue           # 根组件
│   └── main.ts           # 入口文件
├── .env.development      # 开发环境变量
├── .env.production       # 生产环境变量
├── .gitignore
├── index.html
├── package.json
├── tsconfig.json
├── tsconfig.node.json
└── vite.config.ts
```

## 开始使用

### 安装依赖

```bash
cd frontend
npm install
```

### 开发模式

```bash
npm run dev
```

项目将在 `http://localhost:8081` 启动

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 环境配置

### 开发环境 (.env.development)

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_APP_TITLE=高校教学就业平台
```

### 生产环境 (.env.production)

```env
VITE_API_BASE_URL=https://api.yourdomain.com
VITE_APP_TITLE=高校教学就业平台
```

## 已实现功能

### ✅ 基础设施

- [x] 项目配置 (Vite, TypeScript, ESLint)
- [x] 路由系统 (Vue Router + 路由守卫)
- [x] 状态管理 (Pinia)
- [x] HTTP 请求封装 (Axios + 拦截器)
- [x] 全局样式系统 (OKLCH 颜色空间)
- [x] 工具函数库

### ✅ 布局组件

- [x] 主布局 (侧边栏 + 顶部栏 + 内容区)
- [x] 响应式侧边栏 (可折叠)
- [x] 顶部导航栏 (面包屑、用户菜单、通知)

### ✅ 认证模块

- [x] 登录页面
- [x] 注册页面
- [x] 登录状态管理
- [x] 路由守卫

### ✅ 仪表盘

- [x] 欢迎区域
- [x] 统计卡片 (学生、企业、职位、申请)
- [x] 就业趋势图表 (ECharts)
- [x] 就业状态饼图
- [x] 热门课程列表
- [x] 最新公告
- [x] 合作企业展示

### 🚧 待实现功能

#### 就业管理模块

- [ ] 企业管理 (CRUD)
- [ ] 职位发布
- [ ] 职位管理
- [ ] 申请管理

#### 学生管理模块

- [ ] 学生列表
- [ ] 学生导入/导出
- [ ] 学生详情

#### 课程管理模块

- [ ] 课程列表
- [ ] 热门课程
- [ ] 课程详情

#### 系统设置

- [ ] 个人资料
- [ ] 修改密码

## API 接口

后端服务运行在 `http://localhost:8080`

### 已对接接口

- `POST /system/auth/login` - 用户登录
- `POST /system/auth/register` - 用户注册
- `GET /system/auth/check-username` - 检查用户名
- `GET /api/employment/company/all` - 获取所有企业
- `POST /api/employment/company` - 创建企业
- `PUT /api/employment/company` - 更新企业
- `DELETE /api/employment/company/{id}` - 删除企业
- `GET /api/employment/recruitment/all` - 获取所有职位
- `POST /api/employment/recruitment` - 创建职位
- `PUT /api/employment/recruitment` - 更新职位
- `DELETE /api/employment/recruitment/{id}` - 删除职位
- `GET /api/employment/application/all` - 获取所有申请
- `POST /api/employment/application/submit` - 提交申请
- `POST /api/employment/application/update` - 更新申请

### 待补充接口

- `GET /api/employment/student/*` - 学生管理相关接口
- `GET /api/statistics/*` - 统计数据相关接口
- `POST /api/file/upload` - 文件上传接口
- `POST /system/auth/refresh` - Token 刷新接口

## 设计规范

### 颜色系统 (OKLCH)

- **主色**: oklch(0.65 0.18 45) - 温暖橙
- **成功色**: oklch(0.60 0.15 150) - 绿色
- **警告色**: oklch(0.70 0.15 85) - 黄色
- **危险色**: oklch(0.55 0.22 25) - 红色
- **信息色**: oklch(0.55 0.15 220) - 蓝色

### 间距系统

- 基础间距: 0.25rem
- 圆角: 0.5rem (可变)

### 阴影系统

- `--shadow-sm`: 轻微阴影
- `--shadow-md`: 中等阴影
- `--shadow-lg`: 大阴影
- `--shadow-xl`: 超大阴影

## 浏览器支持

- Chrome >= 90
- Firefox >= 88
- Safari >= 14
- Edge >= 90

## 开发建议

1. **代码风格**: 遵循 ESLint 和 Prettier 配置
2. **组件命名**: 使用 PascalCase (如 `UserList.vue`)
3. **文件命名**: 使用 kebab-case (如 `user-api.ts`)
4. **类型定义**: 优先使用 TypeScript 接口
5. **样式**: 使用 SCSS 和 CSS 变量

## 注意事项

1. 确保 TypeScript 版本 >= 5.3
2. Node.js 版本建议 >= 18.0
3. 使用 pnpm 或 npm 安装依赖
4. 开发时确保后端服务正常运行
5. 修改环境变量后需重启开发服务器

## 联系方式

如有问题，请联系开发团队。

---

**高校教学就业平台** © 2024
