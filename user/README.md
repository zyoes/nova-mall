# User 模块 — 用户服务

用户服务是 Nova Mall 的核心模块之一，负责用户认证、个人信息管理及收货地址管理等功能。

---

## 技术栈

| 技术 | 说明 |
|------|------|
| Java 17 | 开发语言 |
| Spring Boot 3.2.12 | 核心框架 |
| MyBatis-Plus 3.5.15 | ORM 框架（含逻辑删除、自动填充） |
| Spring Security | 密码加密（BCrypt） |
| JWT | 用户认证令牌 |
| Redis | 验证码缓存（有效期 5 分钟） |
| Flyway | 数据库版本迁移 |
| MySQL | 关系型数据库 |
| Spring Mail | 邮箱验证码发送 |

---

## 模块结构

```
user/
├── src/main/java/com/example/user/
│   ├── config/                     # 配置类
│   │   ├── MyBatisPlusHandler.java     # 自动填充（createdAt/updatedAt/createdBy/updatedBy）
│   │   ├── MybatisPlusConfig.java      # MyBatis-Plus 分页插件配置
│   │   ├── OpenAPIConfig.java          # Swagger/OpenAPI 文档配置
│   │   ├── SecurityConfig.java         # Spring Security 配置（BCrypt + 放行所有请求）
│   │   └── WebMvcConfig.java           # 拦截器 + CORS 配置
│   ├── controller/                 # 控制器层
│   │   ├── AuthController.java         # 认证（注册、登录、发送验证码）
│   │   ├── UserProfileController.java  # 个人信息（查看、编辑、修改密码）
│   │   └── UserAddressController.java  # 收货地址（增删改查、设置默认）
│   ├── dto/                        # 数据传输对象
│   │   ├── request/                    # 请求 DTO
│   │   └── response/                   # 响应 DTO
│   ├── entity/                     # 数据库实体
│   │   ├── SysUser.java                # 用户表
│   │   └── UserAddress.java            # 用户地址表
│   ├── mapper/                     # MyBatis-Plus Mapper
│   │   ├── SysUserMapper.java
│   │   └── UserAddressMapper.java
│   ├── service/                    # 服务层
│   │   ├── AuthService.java            # 认证服务接口
│   │   ├── UserProfileService.java     # 个人信息服务接口
│   │   ├── UserAddressService.java     # 地址服务接口
│   │   ├── EmailVerificationService.java # 邮箱验证码服务接口
│   │   └── Impl/                       # 服务实现类
│   └── UserApplication.java        # 启动类
├── src/main/resources/
│   ├── db/migration/               # Flyway 迁移脚本
│   │   ├── V1__create_sys_user.sql
│   │   └── V2__create_user_address.sql
│   ├── application.yml             # 主配置文件
│   └── application-dev.yml         # 开发环境配置（覆盖敏感信息）
└── flyway.conf                     # Flyway CLI 配置
```

---

## 数据库表结构

### sys_user（用户表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键（雪花算法） |
| email | varchar(255) | 邮箱（唯一索引） |
| password | varchar(255) | 密码（BCrypt 加密） |
| name | varchar(255) | 用户名（唯一索引） |
| mobile | varchar(20) | 手机号 |
| avatar | varchar(512) | 头像 URL |
| enabled | tinyint | 是否启用（默认 1） |

### user_address（用户地址表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键（雪花算法） |
| user_id | bigint | 所属用户 ID |
| receiver_name | varchar(50) | 收货人姓名 |
| receiver_phone | varchar(20) | 收货人手机号（唯一索引） |
| province | varchar(255) | 省份 |
| city | varchar(255) | 城市 |
| district | varchar(255) | 区县 |
| address | varchar(255) | 详细地址 |
| is_default | tinyint | 是否默认（0-否，1-是） |

> 两张表均包含通用字段：`created_at`、`created_by`、`updated_at`、`updated_by`、`deleted`（逻辑删除）、`deleted_at`、`deleted_by`。

---

## API 接口

### 认证模块 `/auth`（无需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/register` | 用户注册（需邮箱验证码） |
| POST | `/auth/send-code` | 发送邮箱验证码 |
| POST | `/auth/login` | 用户登录（返回 JWT Token） |

### 个人信息 `/user`（需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/profile` | 查看个人信息 |
| PUT | `/user/updateProfile` | 更新个人信息 |
| PUT | `/user/updatePassword` | 修改密码 |

### 收货地址 `/user-address`（需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/user-address/list` | 获取地址列表（分页 + 关键词搜索） |
| POST | `/user-address/save-or-update` | 新增或更新地址 |
| POST | `/user-address/delete?id=` | 删除地址 |
| POST | `/user-address/set-default?id=` | 设置默认地址 |

---

## 核心设计

### 认证流程

```
发送验证码 → Redis 缓存（5分钟有效）→ 注册时校验验证码 → 创建用户（BCrypt 加密密码）→ 返回 JWT Token
```

### 安全机制

- **JWT 认证**：通过 `LoginInterceptor` 拦截器解析 `Authorization: Bearer <token>` 请求头
- **用户上下文**：`UserContext`（ThreadLocal）存储当前登录用户 ID，请求结束自动清理
- **密码加密**：使用 Spring Security 的 `BCryptPasswordEncoder`
- **归属校验**：地址的增删改均校验当前用户是否为地址所有者
- **唯一性校验**：邮箱、手机号、收货地址均有唯一性约束
- **逻辑删除**：通过 `@TableLogic` 注解实现，数据不物理删除

### 默认地址策略

每个用户只能有一个默认地址。设置新默认地址时，会自动取消该用户其他地址的默认状态（单条 SQL 批量更新）。

---

## 环境配置

### 必需服务

- **MySQL**：数据库
- **Redis**：验证码缓存

### 配置文件

敏感信息（数据库密码、Redis 密码、邮箱授权码、JWT 密钥）应在 `application-dev.yml` 中覆盖，不要提交到代码仓库。

```yaml
# application-dev.yml 示例
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/nova_user?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      password: your_redis_password
  mail:
    password: your_email_auth_code

auth:
  jwt:
    secret-key: your_long_secret_key_at_least_64_characters
```

---

## 快速启动

```bash
# 1. 确保 MySQL 和 Redis 已启动

# 2. 配置 application-dev.yml（参考上方示例）

# 3. 启动服务
cd user
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 4. 访问 Swagger 文档
# http://localhost:8080/swagger-ui.html
```

---

## 数据库迁移

使用 Flyway 管理数据库版本，启动应用时自动执行迁移脚本。

```bash
# 手动执行迁移（可选）
mvn flyway:migrate -f user/pom.xml
```
