package com.example.platform.service;

import com.example.platform.common.PageResult;

import java.util.List;
import java.util.Map;

public interface ContentService {
    PageResult<Map<String, Object>> news(int page, int limit, String keyword, boolean publicOnly);

    Map<String, Object> newsDetail(Long id);

    List<Map<String, Object>> banners();

    PageResult<Map<String, Object>> messages(int page, int limit, String keyword, Long userId);

    void saveMessage(Long userId, String content);

    void replyMessage(Long id, String reply);

    void deleteMessages(java.util.List<Long> ids);

    void saveNews(Map<String, Object> news);

    void updateNews(Map<String, Object> news);

    void deleteNews(java.util.List<Long> ids);

    PageResult<Map<String, Object>> bannerPage(int page, int limit, String keyword);

    void saveBanner(Map<String, Object> banner);

    void updateBanner(Map<String, Object> banner);

    void deleteBanners(java.util.List<Long> ids);

    PageResult<Map<String, Object>> forum(int page, int limit, String keyword, boolean publicOnly);

    Map<String, Object> forumDetail(Long id, Long currentUserId, String role);

    void saveForum(Long userId, Map<String, Object> forum);

    void updateForum(Long currentUserId, String role, Map<String, Object> forum);

    void deleteForums(Long currentUserId, String role, java.util.List<Long> ids);

    java.util.List<Map<String, Object>> forumComments(Long forumId);

    void saveForumComment(Long forumId, Long userId, String content);
}
