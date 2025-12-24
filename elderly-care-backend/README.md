# 养老院入住管理系统后端

基于 Spring Boot 3.1.5 + Vue 3 的养老院入住管理系统后端，实现老人档案管理、床位管理、护理计划、费用结算等功能。

## 技术栈

- **后端**: Spring Boot 3.1.5 + MySQL + JPA
- **认证**: JWT
- **构建工具**: Maven

## 项目结构

```
elderly-care-backend/
├── src/main/java/com/elderlycare/management/
│   ├── ElderlyCareManagementApplication.java  # 主应用类
│   ├── config/          # 配置类
│   │   ├── DataInitializer.java
│   │   └── SecurityConfig.java
│   ├── controller/      # 控制器
│   │   ├── AuthController.java
│   │   └── UserController.java
│   ├── dto/            # 数据传输对象
│   │   ├── ApiResponse.java
│   │   ├── PageResponse.java
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   └── User*.java
│   ├── entity/         # 实体类
│   │   ├── SysRole.java
│   │   ├── SysUser.java
│   │   ├── SysDict.java
│   │   ├── Room.java
│   │   ├── Bed.java
│   │   ├── CareLevel.java
│   │   ├── Elder.java
│   │   ├── CarePlan.java
│   │   ├── CareRecord.java
│   │   ├── FeeItem.java
│   │   ├── Bill.java
│   │   └── BillDetail.java
│   ├── exception/      # 异常处理
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ResourceNotFoundException.java
│   │   └── DuplicateEntryException.java
│   ├── repository/     # 数据访问层
│   │   └── [所有实体的Repository接口]
│   ├── security/       # 安全相关
│   │   ├── JwtUtils.java
│   │   ├── UserPrincipal.java
│   │   ├── UserDetailsServiceImpl.java
│   │   ├── AuthTokenFilter.java
│   │   └── AuthEntryPointJwt.java
│   └── service/        # 业务逻辑层
│       ├── AuthService.java
│       └── UserService.java
└── src/main/resources/
    └── application.yml  # 配置文件
```

## 数据库配置

1. 确保 MySQL 服务正在运行
2. 创建数据库：
```sql
CREATE DATABASE nms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

3. 运行数据库初始化脚本（位于项目根目录的 `system-init.sql`）

4. 修改 `src/main/resources/application.yml` 中的数据库连接信息

## 运行项目

```bash
cd elderly-care-backend
mvn spring-boot:run
```

后端服务将运行在 `http://localhost:8080`

## 默认账号

- 用户名: `admin`
- 密码: `123456`

## API 接口

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 用户管理
- `GET /api/users` - 获取用户列表（分页）
- `GET /api/users/{id}` - 根据ID获取用户信息
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `PUT /api/users/{id}/enable` - 启用用户
- `PUT /api/users/{id}/disable` - 禁用用户

## 开发状态

### 已完成
- ✅ 项目基础结构
- ✅ 实体类（Entity）
- ✅ Repository接口
- ✅ Security配置（JWT认证）
- ✅ 认证相关API（登录/注册）
- ✅ 用户管理API

### 待实现
- ⏳ 角色管理API
- ⏳ 房间管理API
- ⏳ 床位管理API
- ⏳ 护理等级管理API
- ⏳ 老人档案管理API
- ⏳ 护理计划管理API
- ⏳ 护理记录管理API
- ⏳ 费用项目管理API
- ⏳ 账单管理API
- ⏳ 字典管理API

## 注意事项

- 角色权限使用角色名称（如"系统管理员"），在Spring Security中使用 `ROLE_系统管理员` 格式
- 数据库表结构请参考 `system-init.sql`
- JWT token在请求头中使用 `Authorization: Bearer <token>` 格式
- 所有表都包含 `created_at` 和 `updated_at` 字段用于审计
- 用户表字段：`username`, `password`（BCrypt加密）, `real_name`, `email`, `phone`, `role_id`, `status`, `created_at`, `updated_at`

