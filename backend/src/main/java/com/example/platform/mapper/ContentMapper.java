package com.example.platform.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ContentMapper {
    List<Map<String, Object>> newsPage(@Param("offset") int offset, @Param("limit") int limit,
                                       @Param("keyword") String keyword, @Param("publicOnly") boolean publicOnly);

    long newsCount(@Param("keyword") String keyword, @Param("publicOnly") boolean publicOnly);

    Map<String, Object> newsDetail(@Param("id") Long id);

    List<Map<String, Object>> banners();

    List<Map<String, Object>> messagesPage(@Param("offset") int offset, @Param("limit") int limit,
                                           @Param("keyword") String keyword, @Param("userId") Long userId);

    long messagesCount(@Param("keyword") String keyword, @Param("userId") Long userId);

    int saveMessage(@Param("userId") Long userId, @Param("content") String content);

    int replyMessage(@Param("id") Long id, @Param("reply") String reply);

    int deleteMessages(@Param("ids") List<Long> ids);

    int saveNews(Map<String, Object> news);

    int updateNews(Map<String, Object> news);

    int deleteNews(@Param("ids") List<Long> ids);

    List<Map<String, Object>> bannerPage(@Param("offset") int offset, @Param("limit") int limit,
                                         @Param("keyword") String keyword);

    long bannerCount(@Param("keyword") String keyword);

    int saveBanner(Map<String, Object> banner);

    int updateBanner(Map<String, Object> banner);

    int deleteBanners(@Param("ids") List<Long> ids);

    List<Map<String, Object>> forumPage(@Param("offset") int offset, @Param("limit") int limit,
                                        @Param("keyword") String keyword, @Param("publicOnly") boolean publicOnly);

    long forumCount(@Param("keyword") String keyword, @Param("publicOnly") boolean publicOnly);

    Map<String, Object> forumDetail(@Param("id") Long id);

    int increaseForumViewCount(@Param("id") Long id);

    int saveForum(@Param("userId") Long userId, @Param("title") String title, @Param("content") String content);

    int updateForum(Map<String, Object> forum);

    int deleteForums(@Param("ids") List<Long> ids);

    List<Map<String, Object>> forumComments(@Param("forumId") Long forumId);

    int saveForumComment(@Param("forumId") Long forumId, @Param("userId") Long userId, @Param("content") String content);

    int deleteForumCommentsByForumIds(@Param("ids") List<Long> ids);

    int deleteForumCommentsByForumId(@Param("forumId") Long forumId);
}
