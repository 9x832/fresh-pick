# 鲜采直送自营平台

一个基于 Spring Boot 3 和 Vue 3 的现代化农业电商平台，提供完整的商品管理、订单处理、用户管理等功能。

## 项目简介

鲜采直送自营平台是一个前后端分离的农业电商系统，支持用户在线购买农产品、管理员后台管理商品和订单。系统采用现代化的技术栈，提供良好的用户体验和管理效率。

## 技术栈

### 后端技术
- **框架**: Spring Boot 3.0.2
- **语言**: Java 17 (LTS)
- **数据库**: MySQL 8.0
- **ORM**: MyBatis 3.0.1
- **缓存**: Redis (Spring Session Data Redis)
- **认证**: JWT (java-jwt 4.4.0)
- **ID生成**: Snowflake 算法
- **工具库**: Lombok, Fastjson, Gson, Apache Commons
- **邮件服务**: Spring Mail

### 前端技术
- **框架**: Vue 3.5.24 (Composition API)
- **构建工具**: Vite 7.2.2
- **路由**: Vue Router 4.6.3
- **图表**: ECharts 6.0.0
- **HTTP客户端**: Axios 1.13.2

## 项目结构

```
fresh-pick/
├── agrimall-backend/          # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/agrismart/agrimallbackend/
│   │   │   │       ├── annotation/      # 自定义注解
│   │   │   │       ├── common/          # 通用组件
│   │   │   │       │   ├── bean/        # 响应实体
│   │   │   │       │   ├── constant/    # 常量定义
│   │   │   │       │   ├── enums/       # 枚举类
│   │   │   │       │   └── util/        # 工具类
│   │   │   │       ├── config/          # 配置类
│   │   │   │       ├── controller/      # 控制器
│   │   │   │       │   ├── admin/       # 管理员接口
│   │   │   │       │   ├── common/      # 通用接口
│   │   │   │       │   └── home/        # 前台用户接口
│   │   │   │       ├── dto/             # 数据传输对象
│   │   │   │       ├── entity/          # 实体类
│   │   │   │       ├── interceptor/     # 拦截器
│   │   │   │       ├── mapper/          # MyBatis Mapper接口
│   │   │   │       ├── service/         # 业务逻辑层
│   │   │   │       └── vo/              # 视图对象
│   │   │   └── resources/
│   │   │       ├── application.yml      # 配置文件
│   │   │       └── mappers/             # MyBatis XML映射文件
│   │   └── sql/                         # SQL脚本
│   └── pom.xml                          # Maven依赖配置
│
└── agrimall-frontend/        # 前端项目
    ├── src/
    │   ├── apis/             # API接口定义
    │   ├── assets/           # 静态资源
    │   ├── components/       # 公共组件
    │   ├── router/           # 路由配置
    │   ├── utils/            # 工具函数
    │   ├── views/            # 页面组件
    │   │   ├── admin/        # 管理员页面
    │   │   └── home/         # 前台用户页面
    │   └── main.js           # 入口文件
    ├── package.json          # 依赖配置
    └── vite.config.js        # Vite配置
```

## 环境要求

### 后端环境
- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 前端环境
- Node.js 16+ 
- npm 或 yarn

## 安装与运行

### 1. 数据库配置

1. 创建 MySQL 数据库：
```sql
CREATE DATABASE agrimall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入数据库脚本：
```bash
# 执行 agrimall-backend/sql/fresh_pick_delivery.sql
mysql -u root -p agrimall < fresh-pick/agrimall-backend/sql/fresh_pick_delivery.sql
```

### 2. 后端配置

1. 修改数据库配置（`fresh-pick/agrimall-backend/src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/agrimall?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

2. 配置 Redis（如需要）：
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: # 如果有密码
```

3. 配置邮件服务（可选）：
```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 465
    username: your_email@qq.com
    password: your_auth_code
```

4. 启动后端：
```bash
cd fresh-pick/agrimall-backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 3. 前端配置

1. 安装依赖：
```bash
cd fresh-pick/agrimall-frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动（Vite 默认端口）。

3. 配置 API 地址（如需要）：
在 `fresh-pick/agrimall-frontend/src/apis/request.js` 中修改 `baseURL`。

## 功能模块

### 前台用户功能
- **用户注册/登录**: JWT 认证，支持用户注册和登录
- **商品浏览**: 分类浏览、商品搜索、商品详情查看
- **购物车**: 添加商品、修改数量、删除商品
- **订单管理**: 下单、订单确认、订单列表、订单详情
- **地址管理**: 添加、编辑、删除收货地址，设置默认地址
- **个人中心**: 个人信息管理、密码修改、头像上传
- **商品收藏**: 收藏/取消收藏商品，查看收藏列表
- **商品评论**: 对已购买商品进行评论
- **热门排行**: 查看热销商品排行榜
- **个性化推荐**: 基于协同过滤算法的商品推荐

### 管理员功能
- **仪表盘**: 数据统计、图表展示、核心指标监控
- **商品管理**: 商品 CRUD、商品分类管理、商品图片上传
- **订单管理**: 订单列表、订单详情、订单状态管理
- **用户管理**: 用户列表、用户信息查看、用户搜索
- **评论管理**: 评论列表、评论审核、评论删除
- **管理员管理**: 管理员账号管理、角色分配
- **角色管理**: 角色 CRUD、权限配置、菜单关联
- **菜单管理**: 菜单树结构管理、路由配置
- **公告管理**: 公告发布、编辑、删除
- **邮件管理**: 发送邮件、邮件列表、附件上传

## 核心特性

### 1. 权限管理
- **RBAC 模型**: 基于角色的访问控制
- **灵活配置**: 支持角色、菜单、权限的动态配置
- **菜单权限**: 根据角色动态显示菜单
- **接口权限**: 通过拦截器控制接口访问

### 2. 认证与授权
- **前台用户**: JWT Token 认证
- **管理员**: Session + Redis 分布式会话
- **拦截器**: 自动验证登录状态和权限

### 3. 商品推荐算法
- **协同过滤**: 基于物品的协同过滤算法
- **个性化推荐**: 根据用户历史行为推荐商品
- **热门排行**: 基于销售数据的统计排序

### 4. 分布式特性
- **分布式ID**: Snowflake 算法生成唯一ID
- **分布式会话**: Redis 存储 Session
- **缓存机制**: Redis 缓存提升性能

### 5. 文件上传
- **图片上传**: 支持用户头像、商品图片上传
- **附件上传**: 支持邮件附件上传
- **文件管理**: 统一的文件存储和访问接口

## API 接口说明

### 前台用户接口（/api/home）
- `POST /api/home/user/register` - 用户注册
- `POST /api/home/user/login` - 用户登录
- `GET /api/home/user/info` - 获取用户信息
- `PUT /api/home/user/info` - 更新用户信息
- `GET /api/home/product/list` - 获取商品列表
- `GET /api/home/product/detail/{id}` - 获取商品详情
- `GET /api/home/product/recommend` - 获取推荐商品
- `GET /api/home/product/hot-rank` - 获取热门排行
- `POST /api/home/cart/add` - 添加购物车
- `GET /api/home/cart/list` - 获取购物车列表
- `POST /api/home/order/submit` - 提交订单
- `GET /api/home/order/list` - 获取订单列表

### 管理员接口（/api/admin）
- `POST /api/admin/login` - 管理员登录
- `GET /api/admin/dashboard` - 获取仪表盘数据
- `GET /api/admin/product/list` - 获取商品列表
- `POST /api/admin/product/add` - 添加商品
- `PUT /api/admin/product/update` - 更新商品
- `DELETE /api/admin/product/delete/{id}` - 删除商品
- `GET /api/admin/order/list` - 获取订单列表
- `PUT /api/admin/order/update` - 更新订单状态

### 通用接口（/api/common）
- `POST /api/common/upload/photo` - 上传图片
- `POST /api/common/upload/attachment` - 上传附件
- `GET /api/common/photo/view` - 查看图片
- `GET /api/common/attachment/download` - 下载附件

## 数据库设计

主要数据表：
- `mall_user` - 用户表
- `mall_admin` - 管理员表
- `mall_product` - 商品表
- `mall_product_category` - 商品分类表
- `mall_order` - 订单表
- `mall_order_item` - 订单项表
- `mall_address` - 收货地址表
- `mall_cart` - 购物车表
- `mall_collect` - 收藏表
- `mall_comment` - 评论表
- `mall_role` - 角色表
- `mall_menu` - 菜单表
- `mall_authority` - 权限表
- `mall_announcement` - 公告表
- `mall_mail` - 邮件表
- `mall_attachment` - 附件表

## 开发说明

### 后端开发
- 使用 MyBatis 进行数据库操作
- 使用 Lombok 简化代码
- 统一异常处理和响应格式
- 使用自定义注解进行参数验证

### 前端开发
- 使用 Vue 3 Composition API
- 组件化开发，提高代码复用性
- 使用 Axios 进行 HTTP 请求
- 路由守卫控制页面访问权限

## 注意事项

1. **配置文件**: 生产环境请修改 `fresh-pick/agrimall-backend/src/main/resources/application.yml` 中的敏感信息（数据库密码、Redis 密码、邮件配置等）
2. **文件上传**: 确保 `upload` 目录有写入权限
3. **跨域配置**: 开发环境已配置跨域，生产环境请根据实际情况调整
4. **Session 超时**: 默认 Session 超时时间为 7200 秒（2小时）
5. **文件大小限制**: 默认最大文件上传大小为 300MB

