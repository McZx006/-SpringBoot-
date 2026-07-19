# 线上教学平台

本项目按《线上教学平台版本UI接口确定方案.md》和《线上教学平台后续实施与系统设计方案.md》创建工程骨架。

## 技术版本

- 后端：JDK 8、Spring Boot 2.7.18、MyBatis、Maven
- 数据库：MySQL 8.0，SQL 兼容 MySQL 5.7
- 学员端：Vue3、Vite、Element Plus、Pinia、Axios
- 管理端：Vue2.7、Vue CLI 5、Element UI、Vuex、Axios

## 目录结构

```text
online-teaching-platform/
  backend/       Spring Boot 后端
  student-web/   学员端 Vue3
  admin-web/     管理端 Vue2
  database/      数据库脚本
  docs/          接口与说明文档
```

## 推荐开发顺序

1. 导入 `database/init.sql`。
2. 启动 `backend` 后端服务。
3. 启动 `student-web` 学员端。
4. 启动 `admin-web` 管理端。
5. 先完成登录、资料、考试三个核心模块，再补论坛、留言、公告。

详细运行步骤见 [docs/run.md](docs/run.md)。
