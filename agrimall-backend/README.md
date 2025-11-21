# 鲜采直送自营平台 - 后端项目

基于 Spring Boot 3 的 RESTful API 服务，提供完整的电商平台后端功能。

## 技术栈

- **Spring Boot 3.0.2** - 企业级 Java 应用框架
- **Java 17** - LTS 版本
- **MyBatis 3.0.1** - 持久层框架
- **MySQL 8.0** - 关系型数据库
- **Redis** - 缓存和分布式会话
- **JWT** - 无状态认证
- **Snowflake** - 分布式 ID 生成算法

## 项目结构

```
src/main/java/com/agrismart/agrimallbackend/
├── annotation/          # 自定义注解
│   └── ValidateEntity.java
├── common/             # 通用组件
│   ├── bean/           # 响应实体
│   │   ├── CodeMsg.java
│   │   └── ResponseVo.java
│   ├── constant/       # 常量定义
│   │   └── RuntimeConstant.java
│   ├── enums/          # 枚举类
│   │   ├── AdminStateEnum.java
│   │   ├── OrderStateEnum.java
│   │   └── ...
│   └── util/           # 工具类
│       ├── MailUtil.java
│       ├── SnowflakeIdGenerator.java
│       └── ...
├── config/             # 配置类
│   ├── WebMvcConfig.java
│   └── ...
├── controller/         # 控制器层
│   ├── admin/          # 管理员接口
│   ├── common/        # 通用接口
│   └── home/           # 前台用户接口
├── dto/                # 数据传输对象
│   ├── request/        # 请求 DTO
│   └── response/       # 响应 DTO
├── entity/             # 实体类
│   ├── admin/          # 管理员相关实体
│   ├── common/         # 通用实体
│   └── home/           # 前台相关实体
├── interceptor/        # 拦截器
│   ├── AdminInterceptor.java
│   └── JwtInterceptor.java
├── mapper/             # MyBatis Mapper 接口
├── service/            # 业务逻辑层
│   ├── admin/          # 管理员服务
│   ├── common/         # 通用服务
│   └── home/           # 前台服务
└── vo/                 # 视图对象
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+ (可选，用于 Session 存储)

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE agrimall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入 SQL 脚本：
```bash
mysql -u root -p agrimall < sql/fresh_pick_delivery.sql
```

### 配置文件

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/agrimall?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  
  # Redis 配置（可选）
  data:
    redis:
      host: localhost
      port: 6379
      password: # 如果有密码
  
  # 邮件配置（可选）
  mail:
    host: smtp.qq.com
    port: 465
    username: your_email@qq.com
    password: your_auth_code
```

### 运行项目

```bash
# 编译项目
mvn clean install

# 运行项目
mvn spring-boot:run
```

或者使用 IDE 直接运行 `AgrimallBackendApplication.java`。

服务将在 `http://localhost:8080` 启动。

## API 接口

### 前台用户接口

#### 用户相关
- `POST /api/home/user/register` - 用户注册
- `POST /api/home/user/login` - 用户登录
- `GET /api/home/user/info` - 获取用户信息
- `PUT /api/home/user/info` - 更新用户信息
- `PUT /api/home/user/password` - 修改密码

#### 商品相关
- `GET /api/home/product/list` - 获取商品列表
- `GET /api/home/product/detail/{id}` - 获取商品详情
- `GET /api/home/product/categories` - 获取商品分类
- `GET /api/home/product/recommend` - 获取推荐商品
- `GET /api/home/product/hot-rank` - 获取热门排行

#### 购物车相关
- `POST /api/home/cart/add` - 添加购物车
- `GET /api/home/cart/list` - 获取购物车列表
- `PUT /api/home/cart/update` - 更新购物车
- `DELETE /api/home/cart/delete/{id}` - 删除购物车项

#### 订单相关
- `POST /api/home/order/submit` - 提交订单
- `GET /api/home/order/list` - 获取订单列表
- `GET /api/home/order/detail/{id}` - 获取订单详情

#### 地址相关
- `GET /api/home/address/list` - 获取地址列表
- `POST /api/home/address/add` - 添加地址
- `PUT /api/home/address/update/{id}` - 更新地址
- `DELETE /api/home/address/delete/{id}` - 删除地址
- `PUT /api/home/address/set-first/{id}` - 设置默认地址

### 管理员接口

#### 认证相关
- `POST /api/admin/login` - 管理员登录
- `POST /api/admin/logout` - 管理员登出
- `GET /api/admin/info` - 获取管理员信息

#### 商品管理
- `GET /api/admin/product/list` - 获取商品列表
- `POST /api/admin/product/add` - 添加商品
- `PUT /api/admin/product/update` - 更新商品
- `DELETE /api/admin/product/delete/{id}` - 删除商品

#### 订单管理
- `GET /api/admin/order/list` - 获取订单列表
- `GET /api/admin/order/detail/{id}` - 获取订单详情
- `PUT /api/admin/order/update` - 更新订单状态

#### 用户管理
- `GET /api/admin/user/list` - 获取用户列表

#### 评论管理
- `GET /api/admin/comment/list` - 获取评论列表
- `DELETE /api/admin/comment/delete/{id}` - 删除评论

### 通用接口

#### 文件上传
- `POST /api/common/upload/photo` - 上传图片
- `POST /api/common/upload/attachment` - 上传附件
- `GET /api/common/photo/view` - 查看图片
- `GET /api/common/attachment/download` - 下载附件

## 核心功能

### 1. 认证与授权

#### JWT 认证（前台用户）
- 使用 JWT Token 进行无状态认证
- Token 包含用户 ID、用户名、邮箱等信息
- 通过 `JwtInterceptor` 拦截器验证 Token

#### Session 认证（管理员）
- 使用 Spring Session + Redis 实现分布式会话
- Session 存储在 Redis 中
- 通过 `AdminInterceptor` 拦截器验证登录状态

### 2. 权限管理

- **RBAC 模型**: 基于角色的访问控制
- **角色管理**: 支持角色的创建、编辑、删除
- **菜单权限**: 根据角色动态显示菜单
- **接口权限**: 通过拦截器控制接口访问

### 3. 商品推荐算法

- **协同过滤**: 基于物品的协同过滤算法
- **个性化推荐**: 根据用户历史购买行为推荐商品
- **热门排行**: 基于商品销售数量进行排序

### 4. 分布式特性

- **Snowflake ID**: 使用 Snowflake 算法生成分布式唯一 ID
- **Redis 缓存**: 使用 Redis 缓存提升性能
- **分布式会话**: 使用 Redis 存储 Session

### 5. 文件上传

- **图片上传**: 支持用户头像、商品图片上传
- **附件上传**: 支持邮件附件上传
- **文件存储**: 文件按日期分类存储
- **文件访问**: 提供统一的文件访问接口

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

## 开发规范

### 代码结构
- Controller 层：处理 HTTP 请求，参数验证
- Service 层：业务逻辑处理
- Mapper 层：数据库操作
- Entity 层：实体类定义
- DTO 层：数据传输对象

### 异常处理
- 统一使用 `ResponseVo` 返回响应
- 使用 `CodeMsg` 定义错误码和错误信息
- 全局异常处理统一捕获异常

### 参数验证
- 使用 `@ValidateEntity` 自定义注解进行参数验证
- 使用 Spring Validation 进行数据校验

### 日志记录
- 使用 SLF4J + Logback 记录日志
- 关键操作记录日志

## 配置说明

### 文件上传配置
```yaml
xqx:
  upload:
    photo:
      path: upload/photo
    attachment:
      path: upload/attachment
```

### Session 配置
```yaml
server:
  servlet:
    session:
      timeout: 7200  # 2小时
```

### 文件大小限制
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 300MB
      max-request-size: 300MB
```

## 注意事项

1. **数据库连接**: 确保数据库服务已启动，连接信息正确
2. **Redis 配置**: 如果使用 Redis，确保 Redis 服务已启动
3. **文件上传**: 确保 `upload` 目录有写入权限
4. **跨域配置**: 开发环境已配置跨域，生产环境需要调整
5. **敏感信息**: 生产环境请修改配置文件中的敏感信息

## 依赖说明

主要依赖：
- `spring-boot-starter-web` - Web 开发
- `spring-boot-starter-data-redis` - Redis 支持
- `mybatis-spring-boot-starter` - MyBatis 集成
- `spring-boot-starter-mail` - 邮件服务
- `java-jwt` - JWT 支持
- `lombok` - 代码简化
- `fastjson` / `gson` - JSON 处理

## 常见问题

### 1. 数据库连接失败
检查数据库服务是否启动，连接信息是否正确

### 2. Redis 连接失败
如果未使用 Redis，可以注释相关配置

### 3. 文件上传失败
检查 `upload` 目录权限，确保有写入权限

### 4. 跨域问题
检查 `WebMvcConfig` 中的跨域配置

## 更新日志

- v0.0.1-SNAPSHOT
  - 初始版本
  - 实现基础的用户、商品、订单功能
  - 实现管理员后台功能
  - 集成 JWT 和 Session 认证
  - 实现商品推荐算法

