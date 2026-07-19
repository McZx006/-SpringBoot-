package com.example.platform.service;

import com.example.platform.common.PageResult;

import java.util.List;
import java.util.Map;

public interface StoreupService {
    PageResult<Map<String, Object>> page(int page, int limit, Long userId, String keyword);

    void save(Long userId, Map<String, Object> request);

    void cancel(Long userId, Map<String, Object> request);

    void delete(List<Long> ids);
}

