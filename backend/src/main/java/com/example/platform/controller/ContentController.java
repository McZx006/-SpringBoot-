package com.example.platform.controller;

import com.example.platform.common.PageResult;
import com.example.platform.common.Result;
import com.example.platform.common.RoleConstants;
import com.example.platform.common.SecurityConstants;
import com.example.platform.service.ContentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/news/page")
    public Result<PageResult<Map<String, Object>>> news(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit,
                                                        @RequestParam(required = false) String keyword,
                                                        @RequestParam(defaultValue = "true") boolean publicOnly,
                                                        @RequestAttribute(value = "role", required = false) Object roleAttr) {
        boolean effectivePublicOnly = publicOnly || !RoleConstants.ADMIN.equals(String.valueOf(roleAttr));
        return Result.success(contentService.news(page, limit, keyword, effectivePublicOnly));
    }

    @GetMapping("/news/{id}")
    public Result<Map<String, Object>> newsDetail(@PathVariable Long id) {
        return Result.success(contentService.newsDetail(id));
    }

    @GetMapping("/banners/list")
    public Result<List<Map<String, Object>>> banners() {
        return Result.success(contentService.banners());
    }

    @PostMapping("/news/save")
    public Result<String> saveNews(@RequestBody Map<String, Object> news) {
        contentService.saveNews(news);
        return Result.success("save success");
    }

    @PutMapping("/news/update")
    public Result<String> updateNews(@RequestBody Map<String, Object> news) {
        contentService.updateNews(news);
        return Result.success("update success");
    }

    @DeleteMapping("/news/delete")
    public Result<String> deleteNews(@RequestBody List<Long> ids) {
        contentService.deleteNews(ids);
        return Result.success("delete success");
    }

    @GetMapping("/banners/page")
    public Result<PageResult<Map<String, Object>>> bannerPage(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int limit,
                                                              @RequestParam(required = false) String keyword) {
        return Result.success(contentService.bannerPage(page, limit, keyword));
    }

    @PostMapping("/banners/save")
    public Result<String> saveBanner(@RequestBody Map<String, Object> banner) {
        contentService.saveBanner(banner);
        return Result.success("save success");
    }

    @PutMapping("/banners/update")
    public Result<String> updateBanner(@RequestBody Map<String, Object> banner) {
        contentService.updateBanner(banner);
        return Result.success("update success");
    }

    @DeleteMapping("/banners/delete")
    public Result<String> deleteBanners(@RequestBody List<Long> ids) {
        contentService.deleteBanners(ids);
        return Result.success("delete success");
    }

    @GetMapping("/forum/page")
    public Result<PageResult<Map<String, Object>>> forum(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "10") int limit,
                                                         @RequestParam(required = false) String keyword,
                                                         @RequestParam(defaultValue = "true") boolean publicOnly,
                                                         @RequestAttribute(value = "role", required = false) Object roleAttr) {
        boolean effectivePublicOnly = publicOnly || !RoleConstants.ADMIN.equals(String.valueOf(roleAttr));
        return Result.success(contentService.forum(page, limit, keyword, effectivePublicOnly));
    }

    @GetMapping("/forum/{id}")
    public Result<Map<String, Object>> forumDetail(@PathVariable Long id,
                                                   @RequestAttribute(value = "userId", required = false) Object userIdAttr,
                                                   @RequestAttribute(value = "role", required = false) Object roleAttr) {
        Long userId = userIdAttr == null ? null : Long.valueOf(String.valueOf(userIdAttr));
        String role = String.valueOf(roleAttr);
        return Result.success(contentService.forumDetail(id, userId, role));
    }

    @PostMapping("/forum/save")
    public Result<String> saveForum(@RequestBody Map<String, Object> request,
                                    @RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        contentService.saveForum(userId, request);
        return Result.success("save success");
    }

    @PutMapping("/forum/update")
    public Result<String> updateForum(@RequestBody Map<String, Object> request,
                                      @RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr,
                                      @RequestAttribute(SecurityConstants.REQUEST_ROLE) Object roleAttr) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        String role = String.valueOf(roleAttr);
        contentService.updateForum(userId, role, request);
        return Result.success("update success");
    }

    @DeleteMapping("/forum/delete")
    public Result<String> deleteForums(@RequestBody List<Long> ids,
                                       @RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr,
                                       @RequestAttribute(SecurityConstants.REQUEST_ROLE) Object roleAttr) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        String role = String.valueOf(roleAttr);
        contentService.deleteForums(userId, role, ids);
        return Result.success("delete success");
    }

    @GetMapping("/forum/comments/{id}")
    public Result<List<Map<String, Object>>> forumComments(@PathVariable Long id) {
        return Result.success(contentService.forumComments(id));
    }

    @PostMapping("/forum/comment")
    public Result<String> saveForumComment(@RequestBody Map<String, Object> request,
                                           @RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        contentService.saveForumComment(Long.valueOf(String.valueOf(request.get("forumId"))),
                userId,
                String.valueOf(request.get("content")));
        return Result.success("comment success");
    }

    @PostMapping("/messages/save")
    public Result<String> message(@RequestBody Map<String, Object> request,
                                  @RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        contentService.saveMessage(userId, String.valueOf(request.get("content")));
        return Result.success("message success");
    }

    @GetMapping("/messages/page")
    public Result<PageResult<Map<String, Object>>> messages(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int limit,
                                                            @RequestParam(required = false) String keyword,
                                                            @RequestAttribute(value = "role", required = false) Object roleAttr,
                                                            @RequestAttribute(value = "userId", required = false) Object userIdAttr) {
        Long userId = null;
        if (!"admin".equals(String.valueOf(roleAttr)) && userIdAttr != null) {
            userId = Long.valueOf(String.valueOf(userIdAttr));
        }
        return Result.success(contentService.messages(page, limit, keyword, userId));
    }

    @PutMapping("/messages/reply")
    public Result<String> replyMessage(@RequestBody Map<String, Object> request) {
        contentService.replyMessage(Long.valueOf(String.valueOf(request.get("id"))),
                String.valueOf(request.get("reply")));
        return Result.success("reply success");
    }

    @DeleteMapping("/messages/delete")
    public Result<String> deleteMessages(@RequestBody List<Long> ids) {
        contentService.deleteMessages(ids);
        return Result.success("delete success");
    }
}
