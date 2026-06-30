# 鹦鹉管理系统

本项目是一个面向毕业设计演示的“鹦鹉管理系统”，采用前后端分离结构。系统包含后台管理端和前台内容端，后台主要给管理员、饲养员维护鹦鹉档案、品种、养护记录、预约、公告、用户和登录日志；前台主要给访客、客户浏览公开鹦鹉和公告知识，并完成预约咨询。

## 技术栈

- 后端：Spring Boot 3.2.5、Java 17、MyBatis-Plus、MySQL 8、JWT、BCrypt、Lombok
- 前端：Vue 3、Vite、Vue Router、Pinia、Axios、Element Plus、ECharts
- 数据库脚本：`parrot-management-system/server/src/main/resources/schema.sql`、`data.sql`

## 目录结构

```text
parrot-management-system
├─ scripts                # 演示数据采集与生成脚本
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

`data.sql` 不写入用户账号和密码，默认账号统一由后端 `DataInitializer` 在启动时初始化。脚本会写入菜单、角色菜单、品种、鹦鹉、养护记录和公告等演示数据，适合空库迁移部署或答辩演示前重置业务数据。

如需重新生成演示数据，可以运行：

```bash
python parrot-management-system/scripts/crawl_demo_data.py
```

脚本会从 GBIF Species API 校验鹦鹉物种学名，并从 Wikimedia Commons API 获取公开图片 URL。脚本只保存公开文字资料和图片地址，不下载图片，也不抓取需要登录的页面。

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

## 图片处理说明

后台品种、鹦鹉档案和公告封面都使用统一图片组件。图片既可以通过本地上传保存到 `/upload` 目录，也可以直接粘贴 `http(s)` 图片地址或 `/upload/...` 本地访问路径。业务表中统一保存可访问 URL，前台和后台都会按该地址直接显示图片。

## 当前进度

- 后端已完成 B0-B12 主体内容：登录注册、权限、用户、品种、鹦鹉档案、养护记录、预约、公告、上传、看板、登录日志、演示数据和接口联调。
- 前端已完成 F0-F14 主体内容：基础工程、请求封装、权限路由、前后台布局、登录注册、后台看板、用户/日志、品种/鹦鹉档案、养护记录、预约咨询、公告知识、前台公开内容、个人中心和我的预约。
- 本次已确认后端 `mvn -q -DskipTests compile` 通过，前端 `npm run build` 通过。
- 本次已启动后端并跑通关键接口闭环：管理员、饲养员和客户登录，饲养员/客户权限拦截，用户/鹦鹉/登录日志查询，公开鹦鹉/公告查询，客户提交预约、后台确认并完成预约、客户查看处理结果，客户取消预约后后台可见“已取消”，健康异常记录提交后鹦鹉状态同步为“观察中”。
- 本次已补充合法合规的演示数据生成脚本，`data.sql` 已包含约 200 条业务演示数据，但不包含用户账号和密码。
- 本次已验证图片上传、静态访问和 URL 图片填写：上传图片 URL 可访问，外部图片地址也可保存到品种、鹦鹉档案图片和公告封面字段中。
- 本次已启动前端开发服务并访问 `http://localhost:5173/`，入口页面返回 200。
- 测试后已停止 Spring Boot 和 Vite 服务，`8080`、`5173` 未保留 LISTEN 端口占用。
- 默认初始化器仍负责初始化默认账号，并会补齐少量基础业务数据；完整演示环境建议先执行 `data.sql`，再启动后端生成账号和预约样例。
- 后续建议如需继续精修，可重点做浏览器端逐页人工点验、清理联调产生的少量登录日志和取消预约记录，以及按答辩需要决定“公告知识管理”是否限制饲养员只能查看。
