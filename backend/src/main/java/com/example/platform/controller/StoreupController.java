package com.example.platform.controller;

import com.example.platform.common.PageResult;
import com.example.platform.common.RoleConstants;
import com.example.platform.common.Result;
import com.example.platform.common.SecurityConstants;
import com.example.platform.service.StoreupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/storeup")
public class StoreupController {
    private final StoreupService storeupService;

    public StoreupController(StoreupService storeupService) {
        this.storeupService = storeupService;
    }

    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> page(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit,
                                                        @RequestParam(required = false) String keyword,
                                                        @RequestAttribute(value = "userId", required = false) Object userIdAttr,
                                                        @RequestAttribute(value = "role", required = false) Object roleAttr) {
        String role = roleAttr == null ? null : String.valueOf(roleAttr);
        Long userId = RoleConstants.ADMIN.equals(role) || userIdAttr == null
                ? null
                : Long.valueOf(String.valueOf(userIdAttr));
        return Result.success(storeupService.page(page, limit, userId, keyword));
    }

    @PostMapping("/save")
    public Result<String> save(@RequestBody Map<String, Object> request,
                               @RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        storeupService.save(userId, request);
        return Result.success("save success");
    }

    @DeleteMapping("/cancel")
    public Result<String> cancel(@RequestBody Map<String, Object> request,
                                 @RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        storeupService.cancel(userId, request);
        return Result.success("cancel success");
    }

    @DeleteMapping("/delete")
    public Result<String> delete(@RequestBody List<Long> ids) {
        storeupService.delete(ids);
        return Result.success("delete success");
    }
}
