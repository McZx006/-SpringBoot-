CREATE DATABASE IF NOT EXISTS online_teaching
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE online_teaching;

DROP TABLE IF EXISTS forum_comment;
DROP TABLE IF EXISTS discussxuexiziliao;
DROP TABLE IF EXISTS examrecord;
DROP TABLE IF EXISTS examquestion;
DROP TABLE IF EXISTS exampaper;
DROP TABLE IF EXISTS storeup;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS forum;
DROP TABLE IF EXISTS t_xuexiziliao;
DROP TABLE IF EXISTS t_ziliaoleixing;
DROP TABLE IF EXISTS news;
DROP TABLE IF EXISTS config;
DROP TABLE IF EXISTS t_token;
DROP TABLE IF EXISTS t_xueyuan;
DROP TABLE IF EXISTS t_users;

CREATE TABLE t_users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(100) NOT NULL COMMENT '登录账号',
  password VARCHAR(255) NOT NULL COMMENT '登录密码',
  role VARCHAR(50) NOT NULL COMMENT '角色：admin/student',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0禁用',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_users_username (username),
  KEY idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE t_xueyuan (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学员ID',
  user_id BIGINT DEFAULT NULL COMMENT '关联用户ID',
  xuehao VARCHAR(100) NOT NULL COMMENT '学号',
  name VARCHAR(100) NOT NULL COMMENT '姓名',
  gender VARCHAR(20) DEFAULT NULL COMMENT '性别',
  phone VARCHAR(50) DEFAULT NULL COMMENT '手机号',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_xueyuan_xuehao (xuehao),
  KEY idx_xueyuan_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员表';

CREATE TABLE t_token (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Token ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  token VARCHAR(255) NOT NULL COMMENT '登录令牌',
  expire_time DATETIME NOT NULL COMMENT '过期时间',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_token_token (token),
  KEY idx_token_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Token表';

CREATE TABLE t_ziliaoleixing (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资料类型ID',
  name VARCHAR(100) NOT NULL COMMENT '类型名称',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_resource_type_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资料类型表';

CREATE TABLE t_xuexiziliao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学习资料ID',
  type_id BIGINT DEFAULT NULL COMMENT '资料类型ID',
  title VARCHAR(200) NOT NULL COMMENT '资料名称',
  summary VARCHAR(500) DEFAULT NULL COMMENT '资料简介',
  cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  file_url VARCHAR(255) DEFAULT NULL COMMENT '文件地址',
  video_url VARCHAR(255) DEFAULT NULL COMMENT '视频地址',
  author VARCHAR(100) DEFAULT NULL COMMENT '上传人',
  view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  download_count INT NOT NULL DEFAULT 0 COMMENT '下载次数',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1发布，0下架',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_resource_type (type_id),
  KEY idx_resource_title (title),
  KEY idx_resource_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资料表';

CREATE TABLE discussxuexiziliao (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资料评论ID',
  resource_id BIGINT NOT NULL COMMENT '学习资料ID',
  user_id BIGINT NOT NULL COMMENT '评论用户ID',
  content TEXT NOT NULL COMMENT '评论内容',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  KEY idx_discuss_resource_id (resource_id),
  KEY idx_discuss_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资料评论表';

CREATE TABLE storeup (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  ref_id BIGINT NOT NULL COMMENT '被收藏对象ID',
  type VARCHAR(50) NOT NULL COMMENT '收藏类型：resource/forum/exam',
  title VARCHAR(200) NOT NULL COMMENT '收藏名称',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  KEY idx_storeup_user (user_id),
  KEY idx_storeup_ref (ref_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

CREATE TABLE exampaper (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '试卷ID',
  name VARCHAR(200) NOT NULL COMMENT '试卷名称',
  description VARCHAR(500) DEFAULT NULL COMMENT '试卷说明',
  duration INT NOT NULL DEFAULT 60 COMMENT '考试时长，单位分钟',
  total_score INT NOT NULL DEFAULT 100 COMMENT '总分',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1发布，0草稿',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_exampaper_name (name),
  KEY idx_exampaper_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

CREATE TABLE examquestion (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '试题ID',
  paper_id BIGINT NOT NULL COMMENT '所属试卷ID',
  question_name VARCHAR(300) NOT NULL COMMENT '题目',
  question_type VARCHAR(50) NOT NULL COMMENT '题型：single/multiple/judge/fill',
  options_json TEXT DEFAULT NULL COMMENT '选项JSON',
  answer VARCHAR(500) NOT NULL COMMENT '正确答案',
  analysis TEXT DEFAULT NULL COMMENT '解析',
  score INT NOT NULL DEFAULT 5 COMMENT '分值',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_question_paper (paper_id),
  KEY idx_question_type (question_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试题表';

CREATE TABLE examrecord (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试记录ID',
  paper_id BIGINT NOT NULL COMMENT '试卷ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  score INT NOT NULL DEFAULT 0 COMMENT '得分',
  answer_json LONGTEXT DEFAULT NULL COMMENT '学员答案JSON',
  correct_count INT NOT NULL DEFAULT 0 COMMENT '正确题数',
  wrong_count INT NOT NULL DEFAULT 0 COMMENT '错题数',
  start_time DATETIME DEFAULT NULL COMMENT '开始时间',
  submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_examrecord_paper (paper_id),
  KEY idx_examrecord_user (user_id),
  KEY idx_examrecord_submit_time (submit_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

CREATE TABLE forum (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
  user_id BIGINT NOT NULL COMMENT '发布用户ID',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  content TEXT NOT NULL COMMENT '内容',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1开放，0关闭',
  view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  KEY idx_forum_user (user_id),
  KEY idx_forum_title (title),
  KEY idx_forum_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

CREATE TABLE forum_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '论坛评论ID',
  forum_id BIGINT NOT NULL COMMENT '帖子ID',
  user_id BIGINT NOT NULL COMMENT '评论用户ID',
  content TEXT NOT NULL COMMENT '评论内容',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  KEY idx_forum_comment_forum (forum_id),
  KEY idx_forum_comment_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛评论表';

CREATE TABLE messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '留言ID',
  user_id BIGINT NOT NULL COMMENT '留言用户ID',
  content TEXT NOT NULL COMMENT '留言内容',
  reply TEXT DEFAULT NULL COMMENT '管理员回复',
  reply_time DATETIME DEFAULT NULL COMMENT '回复时间',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待处理，1已回复',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
  KEY idx_messages_user (user_id),
  KEY idx_messages_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

CREATE TABLE news (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
  title VARCHAR(200) NOT NULL COMMENT '标题',
  picture VARCHAR(255) DEFAULT NULL COMMENT '图片',
  summary VARCHAR(500) DEFAULT NULL COMMENT '简介',
  content TEXT DEFAULT NULL COMMENT '内容',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1发布，0隐藏',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  KEY idx_news_title (title),
  KEY idx_news_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

CREATE TABLE config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
  name VARCHAR(100) NOT NULL COMMENT '配置名称',
  value VARCHAR(255) NOT NULL COMMENT '配置值',
  remark VARCHAR(255) DEFAULT NULL COMMENT '说明',
  addtime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_config_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置/轮播图表';

INSERT INTO t_users (username, password, role, status)
VALUES
('admin', '123456', 'admin', 1),
('student', '123456', 'student', 1);

INSERT INTO t_xueyuan (user_id, xuehao, name, gender, phone, email)
VALUES
(2, '2024001', '测试学员', '男', '13800000000', 'student@example.com');

INSERT INTO t_ziliaoleixing (name, sort)
VALUES
('Java 开发', 1),
('数据库', 2),
('前端技术', 3),
('软件工程', 4);

INSERT INTO t_xuexiziliao (type_id, title, summary, cover, file_url, video_url, author, status)
VALUES
(1, 'Java 基础课件', '适合初学者的 Java 入门学习资料，覆盖变量、流程控制、面向对象基础。', '/upload/cover/java.png', '/upload/file/java-basic.pdf', '', '管理员', 1),
(2, 'MySQL 数据库设计', '介绍数据库建模、表结构设计、索引设计和常用 SQL。', '/upload/cover/mysql.png', '/upload/file/mysql-design.pdf', '', '管理员', 1),
(3, 'Vue 前端开发入门', '介绍 Vue 基础语法、组件开发、路由和接口请求。', '/upload/cover/vue.png', '/upload/file/vue-basic.pdf', '', '管理员', 1);

INSERT INTO discussxuexiziliao (resource_id, user_id, content)
VALUES
(1, 2, '这份 Java 入门资料结构很清晰，适合先快速过一遍再配合代码练习。'),
(2, 2, '数据库设计部分对索引和建模的讲解比较实用。');

INSERT INTO exampaper (name, description, duration, total_score, status)
VALUES
('Java 基础测试', '用于检验 Java 基础语法和面向对象基础知识。', 60, 100, 1);

INSERT INTO examquestion (paper_id, question_name, question_type, options_json, answer, analysis, score, sort)
VALUES
(1, 'Java 中用于定义整数变量的关键字是？', 'single', '[{"key":"A","value":"int"},{"key":"B","value":"String"},{"key":"C","value":"class"},{"key":"D","value":"public"}]', 'A', 'int 是 Java 的基本整数类型。', 20, 1),
(1, '以下哪些属于 Java 基本数据类型？', 'multiple', '[{"key":"A","value":"int"},{"key":"B","value":"double"},{"key":"C","value":"String"},{"key":"D","value":"boolean"}]', 'A,B,D', 'String 是引用类型，int、double、boolean 是基本数据类型。', 30, 2),
(1, 'Java 是一种面向对象编程语言。', 'judge', '[{"key":"A","value":"对"},{"key":"B","value":"错"}]', 'A', 'Java 支持封装、继承、多态等面向对象特征。', 20, 3),
(1, 'Java 源文件的常见扩展名是____。', 'fill', '[]', '.java', 'Java 源代码文件通常使用 .java 扩展名。', 30, 4);

INSERT INTO news (title, summary, content, status)
VALUES
('线上教学平台上线通知', '平台已完成基础功能建设。', '欢迎使用线上教学平台，请及时完善个人信息并参与课程学习。', 1);

INSERT INTO config (name, value, remark)
VALUES
('banner1', '/upload/banner/banner1.jpg', '首页轮播图1'),
('banner2', '/upload/banner/banner2.jpg', '首页轮播图2');
