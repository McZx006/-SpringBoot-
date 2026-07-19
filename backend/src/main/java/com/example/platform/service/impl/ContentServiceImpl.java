package com.example.platform.service.impl;

import com.example.platform.common.RoleConstants;
import com.example.platform.common.PageResult;
import com.example.platform.mapper.ContentMapper;
import com.example.platform.service.ContentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ContentServiceImpl implements ContentService {
    private final ContentMapper contentMapper;

    public ContentServiceImpl(ContentMapper contentMapper) {
        this.contentMapper = contentMapper;
    }

    @Override
    public PageResult<Map<String, Object>> news(int page, int limit, String keyword, boolean publicOnly) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                contentMapper.newsPage(offset, limit, keyword, publicOnly),
                contentMapper.newsCount(keyword, publicOnly),
                page,
                limit
        );
    }

    @Override
    public Map<String, Object> newsDetail(Long id) {
        Map<String, Object> news = contentMapper.newsDetail(id);
        if (news == null) {
            throw new IllegalArgumentException("公告不存在");
        }
        if (!Integer.valueOf(1).equals(news.get("status"))) {
            throw new IllegalArgumentException("公告暂未发布");
        }
        return news;
    }

    @Override
    public List<Map<String, Object>> banners() {
        return contentMapper.banners();
    }

    @Override
    public PageResult<Map<String, Object>> messages(int page, int limit, String keyword, Long userId) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                contentMapper.messagesPage(offset, limit, keyword, userId),
                contentMapper.messagesCount(keyword, userId),
                page,
                limit
        );
    }

    @Override
    public void saveMessage(Long userId, String content) {
        String text = content == null ? "" : content.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("留言内容不能为空");
        }
        if (text.length() > 500) {
            throw new IllegalArgumentException("留言内容不能超过500字");
        }
        contentMapper.saveMessage(userId, text);
    }

    @Override
    public void replyMessage(Long id, String reply) {
        String text = reply == null ? "" : reply.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("回复内容不能为空");
        }
        if (text.length() > 500) {
            throw new IllegalArgumentException("回复内容不能超过500字");
        }
        contentMapper.replyMessage(id, text);
    }

    @Override
    public void deleteMessages(java.util.List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            contentMapper.deleteMessages(ids);
        }
    }

    @Override
    public void saveNews(Map<String, Object> news) {
        contentMapper.saveNews(news);
    }

    @Override
    public void updateNews(Map<String, Object> news) {
        contentMapper.updateNews(news);
    }

    @Override
    public void deleteNews(java.util.List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            contentMapper.deleteNews(ids);
        }
    }

    @Override
    public PageResult<Map<String, Object>> bannerPage(int page, int limit, String keyword) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                contentMapper.bannerPage(offset, limit, keyword),
                contentMapper.bannerCount(keyword),
                page,
                limit
        );
    }

    @Override
    public void saveBanner(Map<String, Object> banner) {
        contentMapper.saveBanner(banner);
    }

    @Override
    public void updateBanner(Map<String, Object> banner) {
        contentMapper.updateBanner(banner);
    }

    @Override
    public void deleteBanners(java.util.List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            contentMapper.deleteBanners(ids);
        }
    }

    @Override
    public PageResult<Map<String, Object>> forum(int page, int limit, String keyword, boolean publicOnly) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                contentMapper.forumPage(offset, limit, keyword, publicOnly),
                contentMapper.forumCount(keyword, publicOnly),
                page,
                limit
        );
    }

    @Override
    public Map<String, Object> forumDetail(Long id, Long currentUserId, String role) {
        contentMapper.increaseForumViewCount(id);
        Map<String, Object> forum = contentMapper.forumDetail(id);
        if (forum == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        Integer status = Integer.valueOf(String.valueOf(forum.getOrDefault("status", 1)));
        if (status != 1 && !RoleConstants.ADMIN.equals(role)
                && !String.valueOf(currentUserId).equals(String.valueOf(forum.get("userId")))) {
            throw new IllegalArgumentException("该帖子已关闭或不可查看");
        }
        return forum;
    }

    @Override
    public void saveForum(Long userId, Map<String, Object> forum) {
        String title = String.valueOf(forum.getOrDefault("title", "")).trim();
        String content = String.valueOf(forum.getOrDefault("content", "")).trim();
        if (title.isEmpty()) {
            throw new IllegalArgumentException("帖子标题不能为空");
        }
        if (content.isEmpty()) {
            throw new IllegalArgumentException("帖子内容不能为空");
        }
        contentMapper.saveForum(userId, title, content);
    }

    @Override
    public void updateForum(Long currentUserId, String role, Map<String, Object> forum) {
        Long forumId = Long.valueOf(String.valueOf(forum.get("id")));
        Map<String, Object> current = contentMapper.forumDetail(forumId);
        if (current == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        if (!RoleConstants.ADMIN.equals(role) && !String.valueOf(currentUserId).equals(String.valueOf(current.get("userId")))) {
            throw new IllegalArgumentException("无权修改该帖子");
        }
        String title = String.valueOf(forum.getOrDefault("title", current.get("title"))).trim();
        String content = String.valueOf(forum.getOrDefault("content", current.get("content"))).trim();
        if (title.isEmpty()) {
            throw new IllegalArgumentException("帖子标题不能为空");
        }
        if (content.isEmpty()) {
            throw new IllegalArgumentException("帖子内容不能为空");
        }
        contentMapper.updateForum(forum);
    }

    @Override
    public void deleteForums(Long currentUserId, String role, java.util.List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            if (!RoleConstants.ADMIN.equals(role)) {
                for (Long id : ids) {
                    Map<String, Object> current = contentMapper.forumDetail(id);
                    if (current == null || !String.valueOf(currentUserId).equals(String.valueOf(current.get("userId")))) {
                        throw new IllegalArgumentException("无权删除该帖子");
                    }
                }
            }
            contentMapper.deleteForumCommentsByForumIds(ids);
            contentMapper.deleteForums(ids);
        }
    }

    @Override
    public java.util.List<Map<String, Object>> forumComments(Long forumId) {
        return contentMapper.forumComments(forumId);
    }

    @Override
    public void saveForumComment(Long forumId, Long userId, String content) {
        String text = content == null ? "" : content.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }
        if (text.length() > 500) {
            throw new IllegalArgumentException("评论内容不能超过500字");
        }
        contentMapper.saveForumComment(forumId, userId, text);
    }
}
