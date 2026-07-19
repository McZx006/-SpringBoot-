# 线上教学平台接口说明

## 通用规范

- 接口前缀：`/api`
- 请求格式：JSON
- 响应格式：JSON
- 登录认证：请求头携带 `Token`

## 统一响应

```json
{
  "code": 0,
  "msg": "success",
  "data": {}
}
```

## 核心接口

| 模块 | 方法 | 路径 | 说明 |
|---|---|---|---|
| 认证 | POST | `/api/auth/login` | 登录 |
| 认证 | POST | `/api/auth/register` | 学员注册 |
| 认证 | GET | `/api/auth/info` | 当前用户信息 |
| 认证 | GET | `/api/auth/profile` | 当前用户资料 |
| 认证 | PUT | `/api/auth/profile` | 更新当前用户资料 |
| 学习资料 | GET | `/api/resources/page` | 资料分页 |
| 学习资料 | GET | `/api/resources/{id}` | 资料详情 |
| 学习资料 | GET | `/api/resources/download/{id}` | 资料下载地址与统计 |
| 学习资料 | GET | `/api/resources/comments/{id}` | 资料评论列表 |
| 学习资料 | POST | `/api/resources/comment` | 发表评论 |
| 学习资料 | POST | `/api/resources/save` | 新增资料 |
| 资料类型 | GET | `/api/resource-types/page` | 资料类型分页 |
| 资料类型 | POST | `/api/resource-types/save` | 新增资料类型 |
| 资料类型 | PUT | `/api/resource-types/update` | 修改资料类型 |
| 资料类型 | DELETE | `/api/resource-types/delete` | 删除资料类型 |
| 试卷 | GET | `/api/exampapers/page` | 试卷分页 |
| 考试 | GET | `/api/exam/start/{paperId}` | 开始考试 |
| 考试 | POST | `/api/exam/submit` | 提交考试 |
| 考试 | GET | `/api/examrecords/page` | 考试记录分页 |
| 考试 | GET | `/api/examrecords/{id}` | 考试记录详情 |
| 考试 | GET | `/api/wrong-questions/page` | 错题本分页 |
| 论坛 | GET | `/api/forum/page` | 帖子分页 |
| 论坛 | GET | `/api/forum/{id}` | 帖子详情 |
| 论坛 | GET | `/api/forum/comments/{id}` | 帖子评论列表 |
| 论坛 | POST | `/api/forum/save` | 发布帖子 |
| 论坛 | POST | `/api/forum/comment` | 发布帖子评论 |
| 论坛 | PUT | `/api/forum/update` | 修改帖子 |
| 论坛 | DELETE | `/api/forum/delete` | 删除帖子 |
| 留言 | POST | `/api/messages/save` | 提交留言 |
| 留言 | GET | `/api/messages/page` | 留言分页 |
| 留言 | PUT | `/api/messages/reply` | 回复留言 |
| 公告 | GET | `/api/news/page` | 公告分页 |
| 公告 | GET | `/api/news/{id}` | 公告详情 |
| 公告 | POST | `/api/news/save` | 新增公告 |
| 公告 | PUT | `/api/news/update` | 修改公告 |
| 公告 | DELETE | `/api/news/delete` | 删除公告 |
| 轮播 | GET | `/api/banners/list` | 获取轮播图 |
| 轮播 | GET | `/api/banners/page` | 轮播图分页 |
| 轮播 | POST | `/api/banners/save` | 新增轮播图 |
| 轮播 | PUT | `/api/banners/update` | 修改轮播图 |
| 轮播 | DELETE | `/api/banners/delete` | 删除轮播图 |
| 收藏 | GET | `/api/storeup/page` | 收藏分页 |
| 收藏 | POST | `/api/storeup/save` | 新增收藏 |
| 收藏 | DELETE | `/api/storeup/cancel` | 取消收藏 |
| 收藏 | DELETE | `/api/storeup/delete` | 后台删除收藏 |

## 当前联调重点

当前最值得联调验证的主流程：

1. 学员登录 -> 资料浏览 -> 评论 -> 收藏 -> 下载。
2. 学员登录 -> 进入考试 -> 提交 -> 查看结果 -> 查看考试记录详情。
3. 学员发帖 -> 评论 -> 查看帖子 -> 删除自己的帖子。
4. 学员留言 -> 后台回复 -> 学员查看回复。
5. 后台维护公告/轮播图/资料类型/学习资料并在学员端确认展示。
