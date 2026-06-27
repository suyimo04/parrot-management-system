# 鹦鹉管理系统

本项目是一个面向毕业设计演示的“鹦鹉管理系统”，采用前后端分离结构。系统包含后台管理端和前台内容端，后台主要给管理员、饲养员维护鹦鹉档案、品种、养护记录、预约、公告、用户和登录日志；前台主要给访客、客户浏览公开鹦鹉和公告知识，并完成预约咨询。

## 技术栈

- 后端：Spring Boot 3.2.5、Java 17、MyBatis-Plus、MySQL 8、JWT、BCrypt、Lombok
- 前端：Vue 3、Vite、Vue Router、Pinia、Axios、Element Plus、ECharts
- 数据库脚本：`parrot-management-system/server/src/main/resources/schema.sql`、`data.sql`

## 目录结构

```text
parrot-management-system
├─ server                 # Spring Boot 后端
│  ├─ src/main/java       # 后端业务代码
│  └─ src/main/resources  # 配置文件和 SQL 脚本
└─ web                    # Vue3 前端
   ├─ src/api             # 接口请求封装
   ├─ src/components      # 通用组件
   ├─ src/layout          # 前台、后台布局
   ├─ src/router          # 路由配置
   ├─ src/store           # Pinia 状态
   └─ src/views           # 页面
```

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+

## 数据库初始化

先创建并初始化数据库：

```bash
mysql -uroot -p < parrot-management-system/server/src/main/resources/schema.sql
mysql -uroot -p < parrot-management-system/server/src/main/resources/data.sql
```

`data.sql` 不写入用户账号。默认账号由后端 `DataInitializer` 在启动时自动创建。

## 后端启动

```bash
cd parrot-management-system/server
cp src/main/resources/application-dev.example.yml src/main/resources/application-dev.yml
```

修改 `application-dev.yml` 中的 MySQL 密码、JWT 密钥和上传目录后启动：

```bash
mvn spring-boot:run
```

默认后端地址：

```text
http://localhost:8080
```

健康检查：

```text
GET /api/public/health
```

## 前端启动

```bash
cd parrot-management-system/web
npm install
npm run dev
```

默认前端地址：

```text
http://localhost:5173
```

Vite 已配置 `/api` 和 `/upload` 代理到 `http://localhost:8080`。

## 默认演示账号

| 账号 | 密码 | 角色 | 说明 |
| --- | --- | --- | --- |
| admin | admin123 | 管理员 | 可访问后台全部功能 |
| keeper | admin123 | 饲养员 | 可访问后台养护、预约、内容等功能 |
| customer | admin123 | 客户 | 可访问前台个人中心和预约功能 |

## 接口分组简要说明

- 认证：`POST /api/auth/login`、`POST /api/auth/register`
- 用户与个人资料：`/api/admin/user/**`、`/api/portal/user/**`
- 鹦鹉与品种：`/api/admin/parrot/**`、`/api/admin/species/**`、`/api/public/parrot/**`
- 养护记录：`/api/admin/health-record/**`、`/api/admin/feeding-record/**`、`/api/admin/training-record/**`
- 预约咨询：`/api/portal/appointment/**`、`/api/admin/appointment/**`
- 公告知识：`/api/admin/notice/**`、`/api/public/notice/**`
- 上传：`POST /api/common/upload`
- 看板：`/api/admin/dashboard/stats`、`/api/admin/dashboard/charts`
- 登录日志：`GET /api/admin/login-log/page`

## 当前进度

- 后端已完成 B0-B12 主体内容：登录注册、权限、用户、品种、鹦鹉档案、养护记录、预约、公告、上传、看板、登录日志、演示数据和接口联调。
- 前端已完成 F0-F9：基础工程、请求封装、权限路由、后台布局、登录注册、首页看板、用户管理、登录日志、品种管理和鹦鹉档案管理。
- 本次已确认后端 `mvn -q -DskipTests compile` 通过，前端 `npm run build` 通过。
- 本次已用接口工具跑通登录、后台分页、预约列表、看板统计/图表、图片上传。测试后后端服务已停止，`8080` 未保留占用。
- 默认初始化器会补齐基础演示业务数据，旧数据库启动时也会自动补足 3 个品种、5 只鹦鹉、公告、养护记录和预约状态样例。
- 后续建议继续做 F10-F14：后台养护记录页面、预约咨询页面、公告知识页面、前台首页和个人预约闭环。
