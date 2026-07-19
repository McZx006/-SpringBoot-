# 运行说明

## 1. 准备环境

| 工具 | 推荐版本 |
|---|---|
| JDK | 8 |
| Maven | 3.8.x |
| MySQL | 8.0，兼容 5.7 |
| Node.js | 学员端建议 18 LTS，管理端建议 16 LTS 或 18 LTS |

## 2. 初始化数据库

1. 打开 MySQL 客户端。
2. 执行 `database/init.sql`。
3. 默认账号：

| 角色 | 账号 | 密码 |
|---|---|---|
| 管理员 | admin | 123456 |
| 学员 | student | 123456 |

本机当前已验证可连接的 MySQL 账号示例：

```text
username: root
password: 123456
```

当前项目默认上传目录已调整到工作区内，便于本机直接运行：

```text
D:\桌面文件\实训\online-teaching-platform\runtime\upload
```

## 3. 启动后端

### IDEA 打开项目

直接用 IntelliJ IDEA 打开项目根目录：

```text
D:\桌面文件\实训\online-teaching-platform
```

IDEA 会通过根目录 `pom.xml` 识别 Maven 聚合项目，并导入 `backend` 后端模块。项目内已提供四个运行配置：

| 运行配置 | 用途 |
|---|---|
| Backend Spring Boot | 启动后端接口服务 |
| Backend Maven Spring Boot | 通过 Maven 执行 `spring-boot:run`，用于 IDEA 尚未正确生成模块时的后端备用启动 |
| Student Web Vite | 启动学员端 |
| Admin Web Vue CLI | 启动管理端 |

如果 IDEA 提示选择 JDK，建议优先选择 JDK 8；本机只有 JDK 17 时也可以先运行 Spring Boot 2.7 项目，但课程验收建议按 JDK 8 配置。

当前项目已在 `.idea` 中配置 MySQL 数据源 `online_teaching@localhost`，目标库为 `online_teaching`。如果 IDEA 的 SQL 文件提示“没有已配置的目标数据源”，原因通常是 SQL 脚本运行配置只识别到了文件，没有绑定数据源；当前已为 `database/init.sql` 绑定该数据源。若 IDEA 仍显示旧提示，关闭项目后重新打开本目录，并在 Database 面板确认账号为：

```text
username: root
password: 123456
database: online_teaching
```

当前 IDEA 项目配置已托管以下内容：

| 配置项 | 位置 | 说明 |
|---|---|---|
| MySQL 数据源 | `.idea/dataSources.xml` | 指向 `jdbc:mysql://localhost:3306/online_teaching` |
| SQL 目标数据源 | `.idea/workspace.xml` | `database/init.sql` 已绑定 `online_teaching@localhost` |
| Maven 本地仓库 | `.idea/maven.xml` | 指向 `D:\桌面文件\实训\.m2repo`，避免 IDEA 使用空仓库重新下载 |
| Maven Home | `.idea/maven.xml` | 指向 IDEA 自带 Maven：`D:\IDEA\IntelliJ IDEA 2024.3.5\plugins\maven\lib\maven3` |
| 项目编码 | `.idea/encodings.xml` | 项目、后端资源、SQL、前端源码均按 UTF-8 |
| 默认运行项 | `.idea/workspace.xml` | 默认选择 `Backend Spring Boot` |
| 后端运行 | `.idea/runConfigurations/Backend_Spring_Boot.xml` | 使用本机 JDK 17 路径运行 Spring Boot |
| 后端备用运行 | `.idea/runConfigurations/Backend_Maven_Spring_Boot.xml` | 使用 IDEA Maven 和 `.m2repo` 执行 `spring-boot:run` |
| 学员端运行 | `.idea/runConfigurations/Student_Web_Vite.xml` | 使用 `D:\node.js\node.exe` 执行 `npm run dev` |
| 管理端运行 | `.idea/runConfigurations/Admin_Web_Vue_CLI.xml` | 使用 `D:\node.js\node.exe` 执行 `npm run serve` |

### 命令行启动

进入后端目录：

```bash
cd backend
```

当前项目默认数据库连接为：

```yaml
spring:
  datasource:
    username: root
    password: 123456
```

启动：

```bash
mvn spring-boot:run
```

如果当前 Windows 环境里 `mvn` 或 `java` 路径不稳定，优先直接执行：

```text
online-teaching-platform\backend\run-local.cmd
```

PowerShell 环境下更推荐：

```powershell
powershell -ExecutionPolicy Bypass -File .\online-teaching-platform\backend\run-local.ps1
```

后端默认端口：

```text
http://localhost:8080
```

建议先单独确认后端已经启动成功，再打开学员端和管理端。
如果前端控制台出现 `Could not proxy request ... to http://localhost:8080 (ECONNREFUSED)`，
通常表示后端尚未启动或已退出，不是前端代理配置本身异常。

## 4. 启动学员端

```bash
cd student-web
npm install
npm run dev
```

也可以直接执行：

```text
online-teaching-platform\student-web\run-local.cmd
```

PowerShell 环境下也可执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\online-teaching-platform\student-web\run-local.ps1
```

访问：

```text
http://localhost:5173
```

## 5. 启动管理端

```bash
cd admin-web
npm install
npm run serve
```

也可以直接执行：

```text
online-teaching-platform\admin-web\run-local.cmd
```

PowerShell 环境下也可执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\online-teaching-platform\admin-web\run-local.ps1
```

访问：

```text
http://localhost:5174/admin/login
```

## 6. Docker Compose 一键部署（优化预案）

本轮已补充容器化部署骨架，适合在安装 Docker Desktop 或 Linux Docker Engine 后验证 Windows/Linux 环境一致性。

```bash
docker compose up -d --build
```

默认访问地址：

```text
学员端：http://localhost/
管理端：http://localhost/admin/login
后端接口：http://localhost/api
```

编排内容：

| 服务 | 说明 |
|---|---|
| mysql | MySQL 8.0，初始化执行 `database/init.sql` |
| redis | Redis 7，预留首页缓存、Token、考试草稿 |
| backend | Spring Boot Jar，上传目录挂载到 Docker volume |
| nginx | 托管学员端和管理端静态资源，反向代理 `/api` |

如果本机未安装 Docker，仍按前文“启动后端、启动学员端、启动管理端”的本地方式运行。

## 7. 优化设计文档索引

| 文档 | 说明 |
|---|---|
| `docs/deep-optimization-review.md` | 覆盖统一接口、Token/RBAC、资料与考试状态机、异常交卷、Redis 缓存、Docker Compose、数据智能预研 |
| `docs/enterprise-architecture-optimization-report.md` | 覆盖 Vue2 管理端迁移 Vue3、28 模块 RESTful 契约、微服务演进、WebSocket、对象存储、支付状态机、MQ 异步判分、Strategy 判分引擎、Spring Security 方法级越权防御 |

## 8. 当前已接入数据库的接口

| 模块 | 接口 |
|---|---|
| 登录 | `/api/auth/login`、`/api/auth/register`、`/api/auth/info` |
| 学员 | `/api/xueyuan/page`、`/api/xueyuan/save`、`/api/xueyuan/update` |
| 资料类型 | `/api/resource-types/list`、`/api/resource-types/page`、`/api/resource-types/save`、`/api/resource-types/update` |
| 学习资料 | `/api/resources/page`、`/api/resources/{id}`、`/api/resources/download/{id}`、`/api/resources/comments/{id}`、`/api/resources/comment`、`/api/resources/save`、`/api/resources/update` |
| 考试 | `/api/exampapers/page`、`/api/exam/start/{paperId}`、`/api/exam/submit`、`/api/examrecords/{id}` |
| 公告 | `/api/news/page`、`/api/news/{id}`、`/api/news/save`、`/api/news/update` |
| 轮播 | `/api/banners/list`、`/api/banners/page`、`/api/banners/save`、`/api/banners/update` |
| 文件 | `/api/file/upload`、`/api/file/download` |
| 收藏 | `/api/storeup/page`、`/api/storeup/save`、`/api/storeup/cancel`、`/api/storeup/delete` |
| 留言 | `/api/messages/page`、`/api/messages/save`、`/api/messages/reply` |
| 论坛 | `/api/forum/page`、`/api/forum/{id}`、`/api/forum/comments/{id}`、`/api/forum/save`、`/api/forum/comment`、`/api/forum/update`、`/api/forum/delete` |
| 考试记录 | `/api/examrecords/page`、`/api/wrong-questions/page` |

## 9. 当前后台维护能力

后台管理端目前已具备以下基础维护闭环：

| 模块 | 能力 |
|---|---|
| 学员管理 | 查询、新增、编辑、删除 |
| 资料类型管理 | 查询、新增、编辑、删除 |
| 学习资料管理 | 查询、新增、编辑、删除 |
| 收藏管理 | 查询、删除 |
| 试卷管理 | 查询、新增、编辑、删除 |
| 试题管理 | 查询、新增、编辑、删除，支持单选、多选、判断、填空 |
| 文件上传 | 学习资料表单中可上传资料文件并自动写入文件地址 |
| 留言管理 | 查询、回复、删除 |
| 公告管理 | 查询、新增、编辑、删除 |
| 轮播图管理 | 查询、新增、编辑、删除 |
| 考试记录 | 查询考试记录和错题统计 |
| 论坛管理 | 查询、编辑状态/内容、删除帖子 |

## 10. 当前学员端演示能力

| 模块 | 能力 |
|---|---|
| 学习资料 | 资料列表、资料详情、预览入口、下载统计、评论 |
| 在线考试 | 试卷列表、搜索试卷、进入答题、倒计时、自动交卷、结果页、答题详情 |
| 首页 | 轮播图、推荐资料、最新公告 |
| 公告资讯 | 公告列表、公告详情 |
| 个人中心 | 个人资料、我的收藏、考试记录、错题本 |
| 留言反馈 | 提交留言、查看回复状态 |
| 论坛交流 | 帖子列表、帖子详情、发帖、评论、删帖 |

## 11. 下一步开发建议

1. 启动 MySQL、后端、学员端和管理端，进入真实页面级联调。
2. 按 `docs/integration-checklist.md` 填写接口测试和联调记录。
3. 根据联调结果继续修复接口、页面和权限边界。
4. 补齐后台更多模块的专用表单校验和批量操作。
5. 根据 UI 原型继续补充页面细节，例如我的发布、后台统计图表和更完整的预览体验。
6. 收口部署文档、测试说明和最终验收材料。

## 12. 当前本机检测结果

| 检测项 | 结果 |
|---|---|
| 根目录 POM | 已创建，可供 IDEA 导入根项目 |
| IDEA 运行配置 | 已创建 Backend Spring Boot、Backend Maven Spring Boot、Student Web、Admin Web 四个运行项 |
| 后端 POM/XML | XML 解析通过 |
| MyBatis Mapper | XML 解析通过，接口方法和 XML id 已对齐 |
| 前端 package.json | 学员端、管理端均可解析 |
| 前端 JS 语法 | 路由和请求封装文件通过 `node --check` |
| 中文乱码扫描 | 未发现乱码命中 |
| Maven 编译 | 已使用 `mvn.cmd --% -Dmaven.repo.local=D:\桌面文件\实训\.m2repo -DskipTests compile` 在 `backend` 目录编译通过 |
| 学员端构建 | `student-web` 已执行 `npm run build` 通过，仅有 Vite/Rollup 体积提示 |
| 管理端构建 | `admin-web` 已执行 `npm run build` 通过，仅有 Webpack 体积提示 |
| 后端启动 | 已真实验证 `java -jar backend\\target\\online-teaching-backend-1.0.0.jar` 可启动到 `http://localhost:8080` |
| 基础接口探活 | 已真实验证 `/api/news/page`、`/api/resources/page`、`/api/auth/login`、`/api/xueyuan/page`、`/api/auth/profile` 可正常返回 |
| IDEA 数据源 | 已配置 `online_teaching@localhost`，并为 `database/init.sql` 绑定目标数据源 |
| 三端运行 | 已真实验证后端 `8080`、学员端 `5173`、管理端 `5174` 均可启动并返回 200 |
| 前端代理 | 已验证学员端通过 `5173/api/news/page` 可代理到后端；管理端未带 Token 访问受保护接口返回 401，说明请求已到达后端鉴权层 |
| 模块接口冒烟 | 已验证登录、资料、资料类型、试卷、论坛、留言、公告、轮播图、后台学员和后台试卷接口均返回 `code=0` |
| 2026-06-18 IDEA 运行核验 | 当前 `8080` 已有 Java 后端进程监听，`/api/news/page` 返回 `code=0`；学员端单独启动后 `/` 与 `/src/main.js` 均返回 200；管理端单独启动后 `/` 与 `/api/news/page` 均返回 200 |

如 IDEA 再次提示 `Port 8080 already in use`，表示已有后端实例正在运行，先停止旧的 `Backend Spring Boot` 运行项，或确认当前监听的 Java 进程就是本项目后端后再重新启动。若 IDEA SQL 文件仍提示“没有已配置的目标数据源”，关闭并重新打开 `D:\桌面文件\实训\online-teaching-platform` 项目，让 `.idea/dataSources.xml` 与 `.idea/workspace.xml` 重新加载。

## 13. 本机快捷启动

如果要在本机一次性拉起三端，可直接运行：

```text
online-teaching-platform\start-local.cmd
```

该脚本会分别打开后端、学员端、管理端三个命令行窗口。
