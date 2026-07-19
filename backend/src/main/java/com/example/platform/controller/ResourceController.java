package com.example.platform.controller;

import com.example.platform.common.PageResult;
import com.example.platform.common.Result;
import com.example.platform.common.RoleConstants;
import com.example.platform.entity.LearningResource;
import com.example.platform.service.ResourceService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long typeId,
            @RequestParam(defaultValue = "true") boolean publicOnly,
            @RequestAttribute(value = "role", required = false) Object roleAttr) {
        boolean effectivePublicOnly = publicOnly || !RoleConstants.ADMIN.equals(String.valueOf(roleAttr));
        return Result.success(resourceService.page(page, limit, keyword, typeId, effectivePublicOnly));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.success(resourceService.detail(id));
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> publicDetail(@PathVariable Long id) {
        return Result.success(resourceService.detail(id));
    }

    @GetMapping("/download/{id}")
    public Result<Map<String, Object>> download(@PathVariable Long id) {
        return Result.success(resourceService.download(id));
    }

    @GetMapping("/comments/{id}")
    public Result<List<Map<String, Object>>> comments(@PathVariable Long id) {
        return Result.success(resourceService.comments(id));
    }

    @PostMapping("/comment")
    public Result<String> saveComment(@RequestBody Map<String, Object> request,
                                      @RequestAttribute(value = "userId", required = false) Object userIdAttr) {
        if (userIdAttr == null) {
            throw new IllegalArgumentException("请先登录后再评论");
        }
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        Long resourceId = Long.valueOf(String.valueOf(request.get("resourceId")));
        String content = String.valueOf(request.getOrDefault("content", ""));
        resourceService.saveComment(resourceId, userId, content);
        return Result.success("save success");
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody LearningResource resource) {
        resourceService.save(resource);
        return Result.success("save success");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody LearningResource resource) {
        resourceService.update(resource);
        return Result.success("update success");
    }

    @DeleteMapping("/delete")
    public Result<String> delete(@RequestBody List<Long> ids) {
        resourceService.delete(ids);
        return Result.success("delete success");
    }
}
