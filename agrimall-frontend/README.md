# 鲜采直送自营平台 - 前端项目

基于 Vue 3 + Vite 的现代化前端应用，提供用户购物和管理员后台管理功能。

## 技术栈

- **Vue 3.5.24** - 渐进式 JavaScript 框架，使用 Composition API
- **Vite 7.2.2** - 下一代前端构建工具
- **Vue Router 4.6.3** - 官方路由管理器
- **Axios 1.13.2** - HTTP 客户端
- **ECharts 6.0.0** - 数据可视化图表库

## 项目结构

```
src/
├── apis/              # API 接口定义
│   ├── admin.js      # 管理员相关接口
│   ├── home.js       # 前台用户相关接口
│   └── request.js    # Axios 请求封装
├── assets/           # 静态资源
│   └── home/         # 前台页面资源
│       ├── css/      # 样式文件
│       ├── fonts/    # 字体文件
│       └── images/   # 图片资源
├── components/       # 公共组件
│   └── home/         # 前台公共组件
│       ├── HomeHeader.vue
│       └── HomeFooter.vue
├── router/          # 路由配置
│   └── index.js     # 路由定义
├── utils/           # 工具函数
│   ├── cart.js      # 购物车工具
│   └── helpers.js   # 通用工具函数
├── views/           # 页面组件
│   ├── admin/       # 管理员页面
│   │   ├── AdminLogin.vue
│   │   ├── AdminLayout.vue
│   │   ├── AdminDashboard.vue
│   │   ├── AdminProducts.vue
│   │   ├── AdminOrders.vue
│   │   └── ...
│   └── home/        # 前台用户页面
│       ├── HomeIndex.vue
│       ├── HomeProductList.vue
│       ├── HomeProductDetail.vue
│       ├── HomeCart.vue
│       ├── HomeOrderConfirm.vue
│       └── ...
└── main.js          # 应用入口
```

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

应用将在 `http://localhost:5173` 启动（Vite 默认端口）。

### 配置后端 API 地址

在 `src/apis/request.js` 中修改 `baseURL`：

```javascript
const request = axios.create({
  baseURL: 'http://localhost:8080', // 修改为实际后端地址
  timeout: 10000,
});
```

## 主要功能

### 前台用户功能
- 用户注册/登录
- 商品浏览与搜索
- 购物车管理
- 订单管理
- 地址管理
- 个人中心
- 商品收藏
- 商品评论
- 热门排行
- 个性化推荐

### 管理员功能
- 管理员登录
- 数据仪表盘
- 商品管理
- 订单管理
- 用户管理
- 评论管理
- 管理员管理
- 角色权限管理
- 菜单管理
- 公告管理
- 邮件管理

## 路由说明

### 前台路由（/home）
- `/home` - 首页
- `/home/product/:category` - 商品分类列表
- `/home/product/detail/:id` - 商品详情
- `/home/cart` - 购物车（需登录）
- `/home/order/confirm` - 订单确认（需登录）
- `/home/user/order` - 订单列表（需登录）
- `/home/user/info` - 个人信息（需登录）
- `/home/user/comment` - 我的评论（需登录）
- `/home/collect` - 我的收藏（需登录）
- `/home/user/password` - 修改密码（需登录）
- `/home/address` - 地址管理（需登录）
- `/home/hot-rank` - 热门排行

### 管理员路由（/admin）
- `/admin/login` - 管理员登录
- `/admin/dashboard` - 仪表盘
- `/admin/products` - 商品管理
- `/admin/product/categories` - 商品分类
- `/admin/orders` - 订单管理
- `/admin/users` - 用户管理
- `/admin/comments` - 评论管理
- `/admin/admins` - 管理员管理
- `/admin/roles` - 角色管理
- `/admin/menus` - 菜单管理
- `/admin/announcements` - 公告管理
- `/admin/mails` - 邮件管理

## 开发规范

### 组件开发
- 使用 Vue 3 Composition API
- 使用 `<script setup>` 语法
- 组件命名采用 PascalCase
- 单文件组件结构：template -> script -> style

### API 调用
- 统一使用 `src/apis` 中定义的接口
- 使用封装的 `request` 进行 HTTP 请求
- 统一错误处理

### 状态管理
- 使用 localStorage 存储用户信息
- JWT Token 存储在 localStorage
- 购物车数据存储在 localStorage

### 样式规范
- 使用 scoped 样式避免样式污染
- 公共样式放在 `assets/home/css/` 目录

## 注意事项

1. **跨域问题**: 开发环境已配置代理，生产环境需要配置 CORS
2. **Token 管理**: JWT Token 存储在 localStorage，注意安全性
3. **路由守卫**: 需要登录的页面已配置路由守卫
4. **图片路径**: 图片资源使用相对路径或完整 URL
5. **API 地址**: 确保后端服务已启动并配置正确的 API 地址

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 依赖说明

### 生产依赖
- `vue` - Vue 框架
- `vue-router` - 路由管理
- `axios` - HTTP 客户端
- `echarts` - 图表库
- `china-area-data` - 中国地区数据

### 开发依赖
- `vite` - 构建工具
- `@vitejs/plugin-vue` - Vue 插件

## 常见问题

### 1. 端口被占用
修改 `vite.config.js` 或使用 `npm run dev -- --port 3000` 指定端口

### 2. API 请求失败
检查后端服务是否启动，以及 `request.js` 中的 `baseURL` 配置

### 3. 路由跳转问题
确保路由路径与后端接口路径一致

## 更新日志

- v0.0.0 - 初始版本
  - 实现基础的前台用户功能
  - 实现管理员后台功能
  - 集成 ECharts 数据可视化
