package com.example.platform.service.impl;

import com.example.platform.common.PageResult;
import com.example.platform.entity.LearningResource;
import com.example.platform.mapper.ResourceMapper;
import com.example.platform.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceMapper resourceMapper;

    public ResourceServiceImpl(ResourceMapper resourceMapper) {
        this.resourceMapper = resourceMapper;
    }

    @Override
    public PageResult<Map<String, Object>> page(int page, int limit, String keyword, Long typeId, boolean publicOnly) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                resourceMapper.page(offset, limit, keyword, typeId, publicOnly),
                resourceMapper.count(keyword, typeId, publicOnly),
                page,
                limit
        );
    }

    @Override
    public Map<String, Object> detail(Long id) {
        ensureResourceExists(id);
        resourceMapper.increaseViewCount(id);
        return resourceMapper.findById(id);
    }

    @Override
    public Map<String, Object> download(Long id) {
        Map<String, Object> resource = resourceMapper.findById(id);
        if (resource == null) {
            throw new IllegalArgumentException("资料不存在");
        }
        String fileUrl = String.valueOf(resource.getOrDefault("fileUrl", ""));
        if (fileUrl.isEmpty()) {
            throw new IllegalArgumentException("当前资料没有可下载文件");
        }
        resourceMapper.increaseDownloadCount(id);
        Map<String, Object> result = new HashMap<>();
        result.put("fileUrl", fileUrl);
        result.put("downloadUrl", toDownloadUrl(fileUrl));
        return result;
    }

    @Override
    public List<Map<String, Object>> comments(Long resourceId) {
        ensureResourceExists(resourceId);
        return resourceMapper.commentList(resourceId);
    }

    @Override
    public void saveComment(Long resourceId, Long userId, String content) {
        ensureResourceExists(resourceId);
        String text = content == null ? "" : content.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }
        if (text.length() > 500) {
            throw new IllegalArgumentException("评论内容不能超过500字");
        }
        resourceMapper.insertComment(resourceId, userId, text);
    }

    @Override
    public void save(LearningResource resource) {
        validateResource(resource, false);
        resourceMapper.insert(resource);
    }

    @Override
    public void update(LearningResource resource) {
        validateResource(resource, true);
        resourceMapper.update(resource);
    }

    @Override
    public void delete(java.util.List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            resourceMapper.deleteBatch(ids);
        }
    }

    private String toDownloadUrl(String fileUrl) {
        if (!fileUrl.startsWith("/upload/")) {
            return fileUrl;
        }
        String relative = fileUrl.substring("/upload/".length());
        int slashIndex = relative.indexOf('/');
        if (slashIndex < 0 || slashIndex == relative.length() - 1) {
            return fileUrl;
        }
        String type = relative.substring(0, slashIndex);
        String name = relative.substring(slashIndex + 1);
        return "/api/file/download?type=" + type + "&name=" + name;
    }

    private void validateResource(LearningResource resource, boolean requireId) {
        if (requireId && resource.getId() == null) {
            throw new IllegalArgumentException("资料ID不能为空");
        }
        if (resource.getTypeId() == null) {
            throw new IllegalArgumentException("资料类型不能为空");
        }
        if (resource.getTitle() == null || resource.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("资料名称不能为空");
        }
        if (resource.getSummary() == null || resource.getSummary().trim().isEmpty()) {
            throw new IllegalArgumentException("资料简介不能为空");
        }
        if ((resource.getFileUrl() == null || resource.getFileUrl().trim().isEmpty())
                && (resource.getVideoUrl() == null || resource.getVideoUrl().trim().isEmpty())) {
            throw new IllegalArgumentException("资料文件地址和视频地址至少填写一个");
        }
        if (resource.getStatus() == null) {
            resource.setStatus(1);
        }
    }

    private void ensureResourceExists(Long id) {
        if (resourceMapper.findById(id) == null) {
            throw new IllegalArgumentException("资料不存在");
        }
    }
}
