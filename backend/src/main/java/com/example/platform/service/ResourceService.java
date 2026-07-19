package com.example.platform.service;

import com.example.platform.common.PageResult;
import com.example.platform.entity.LearningResource;

import java.util.List;
import java.util.Map;

public interface ResourceService {
    PageResult<Map<String, Object>> page(int page, int limit, String keyword, Long typeId, boolean publicOnly);

    Map<String, Object> detail(Long id);

    Map<String, Object> download(Long id);

    List<Map<String, Object>> comments(Long resourceId);

    void saveComment(Long resourceId, Long userId, String content);

    void save(LearningResource resource);

    void update(LearningResource resource);

    void delete(java.util.List<Long> ids);
}
