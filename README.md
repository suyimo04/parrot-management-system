# 基于 Spring Boot 的鹦鹉管理系统

本项目是一个面向毕业设计、课程设计和答辩演示的鹦鹉管理系统，主要围绕鹦鹉档案、品种资料、健康记录、喂养记录、训练记录、预约咨询、公告知识和用户权限进行管理。项目采用前后端分离结构，前端负责后台管理端和门户端页面展示，后端负责登录注册、权限校验、数据接口和文件访问等服务。

## 项目结构

```text
chenenqi
├── parrot-management-system
│   ├── server                 # Spring Boot 后端工程
│   │   ├── src/main/java       # 后端业务代码
│   │   └── src/main/resources  # 配置文件和数据库脚本
│   └── web                    # Vue 前端工程
│       ├── src/api             # 接口请求封装
│       ├── src/components      # 通用组件
│       ├── src/layout          # 前台和后台布局
│       ├── src/router          # 页面路由
│       ├── src/store           # Pinia 状态管理
│       └── src/views           # 页面视图
├── wd                         # 论文、开题和设计说明资料
├── xqwd                       # 需求分析文档
└── 开发任务拆分清单.md
```

## 技术栈

后端：Spring Boot 3.2.5、Java 17、MyBatis-Plus、MySQL、JWT、Lombok。

前端：Vue 3、Vite、Vue Router、Pinia、Axios、Element Plus、ECharts。

数据库：MySQL，初始化脚本位于 `parrot-management-system/server/src/main/resources/schema.sql` 和 `data.sql`。

## 当前进度

- 前端已搭建门户端和后台管理端页面，包含登录注册、首页看板、鹦鹉展示、鹦鹉档案、品种、健康、喂养、训练、预约、公告、用户和登录日志等页面入口。
- 后端已搭建 Spring Boot 基础工程、统一返回结构、登录注册、JWT 拦截、角色判断、健康检查和默认演示账号初始化。
- 数据库脚本已准备主要业务表，方便后续接口开发和答辩演示时快速建库。
- 目前仍需要继续补充部分业务模块的后端 Controller、Service 和 Mapper，实现前端页面对应的数据增删改查接口。

## 环境要求

- JDK 17 或以上
- Maven 3.8 或以上
- Node.js 18 或以上
- MySQL 8.0 或兼容版本

## 数据库初始化

先在本机确认 MySQL 服务已启动，然后依次执行数据库结构和演示数据脚本：

```bash
mysql -uroot -p < parrot-management-system/server/src/main/resources/schema.sql
mysql -uroot -p < parrot-management-system/server/src/main/resources/data.sql
```

如果使用图形化工具，也可以直接打开这两个 SQL 文件按顺序执行。

## 后端运行

真实开发配置文件不提交到 GitHub，请先复制示例配置：

```bash
cd parrot-management-system/server
cp src/main/resources/application-dev.example.yml src/main/resources/application-dev.yml
```

然后根据自己的 MySQL 账号、密码和上传目录修改 `application-dev.yml`。配置完成后启动后端：

```bash
mvn spring-boot:run
```

默认后端地址为：

```text
http://localhost:8080
```

健康检查接口：

```text
http://localhost:8080/api/public/health
```

## 前端运行

```bash
cd parrot-management-system/web
npm install
npm run dev
```

默认前端访问地址为：

```text
http://localhost:5173
```

前端开发服务器已在 `vite.config.js` 中配置 `/api` 和 `/upload` 代理，请先启动后端服务再访问需要接口数据的页面。

## 演示账号

后端启动时，如果 `sys_user` 表为空，会自动创建三类演示账号，密码均为 `admin123`：

| 账号 | 角色 | 说明 |
| --- | --- | --- |
| admin | 管理员 | 可访问后台管理功能 |
| keeper | 饲养员 | 可访问饲养、健康、训练等管理功能 |
| customer | 客户 | 可访问前台个人中心和预约功能 |

## 上传文件说明

上传文件保存路径由后端配置项 `parrot.upload.save-path` 控制，建议设置为项目外或 `parrot-management-system/upload` 目录。上传文件属于本机运行数据，已在 `.gitignore` 中忽略，不建议提交到 GitHub。

## Git 提交说明

本仓库只建议提交源码、配置模板、数据库脚本和项目文档。以下内容已经通过 `.gitignore` 忽略：

- 前端依赖和构建结果：`node_modules`、`dist`
- 后端编译结果：`target`、`*.class`、`*.jar`
- 本机敏感配置：`application-dev.yml`、`.env.local`
- 运行时文件：`upload`、`logs`
- IDE 和系统临时文件：`.idea`、`.vscode`、`Thumbs.db` 等

## 后续开发建议

下一步可以优先补齐后端业务接口，让前端已有页面都能完成真实数据联调。推荐顺序为：品种管理、鹦鹉档案、健康记录、喂养记录、训练记录、预约咨询、公告知识、用户管理、登录日志和首页统计。
