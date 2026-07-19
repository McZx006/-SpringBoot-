package com.example.platform.service.impl;

import com.example.platform.common.PageResult;
import com.example.platform.mapper.StoreupMapper;
import com.example.platform.service.StoreupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StoreupServiceImpl implements StoreupService {
    private final StoreupMapper storeupMapper;

    public StoreupServiceImpl(StoreupMapper storeupMapper) {
        this.storeupMapper = storeupMapper;
    }

    @Override
    public PageResult<Map<String, Object>> page(int page, int limit, Long userId, String keyword) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                storeupMapper.page(offset, limit, userId, keyword),
                storeupMapper.count(userId, keyword),
                page,
                limit
        );
    }

    @Override
    public void save(Long userId, Map<String, Object> request) {
        Long refId = Long.valueOf(String.valueOf(request.get("refId")));
        String type = String.valueOf(request.getOrDefault("type", "resource"));
        String title = String.valueOf(request.getOrDefault("title", ""));
        storeupMapper.insert(userId, refId, type, title);
    }

    @Override
    public void cancel(Long userId, Map<String, Object> request) {
        Long refId = Long.valueOf(String.valueOf(request.get("refId")));
        String type = String.valueOf(request.getOrDefault("type", "resource"));
        storeupMapper.delete(userId, refId, type);
    }

    @Override
    public void delete(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            storeupMapper.deleteBatch(ids);
        }
    }
}

