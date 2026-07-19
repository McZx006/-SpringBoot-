package com.example.platform.controller;

import com.example.platform.common.PageResult;
import com.example.platform.common.Result;
import com.example.platform.entity.Xueyuan;
import com.example.platform.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/xueyuan/page")
    public Result<PageResult<Map<String, Object>>> students(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int limit,
                                                            @RequestParam(required = false) String xuehao,
                                                            @RequestParam(required = false) String name) {
        return Result.success(adminService.students(page, limit, xuehao, name));
    }

    @PostMapping("/xueyuan/save")
    public Result<String> saveStudent(@RequestBody Xueyuan xueyuan) {
        adminService.saveStudent(xueyuan);
        return Result.success("save success");
    }

    @PutMapping("/xueyuan/update")
    public Result<String> updateStudent(@RequestBody Xueyuan xueyuan) {
        adminService.updateStudent(xueyuan);
        return Result.success("update success");
    }

    @DeleteMapping("/xueyuan/delete")
    public Result<String> deleteStudents(@RequestBody List<Long> ids) {
        adminService.deleteStudents(ids);
        return Result.success("delete success");
    }

    @GetMapping("/resource-types/list")
    public Result<List<Map<String, Object>>> resourceTypes() {
        return Result.success(adminService.resourceTypes());
    }

    @GetMapping("/resource-types/page")
    public Result<PageResult<Map<String, Object>>> resourceTypePage(@RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "10") int limit,
                                                                    @RequestParam(required = false) String keyword) {
        return Result.success(adminService.resourceTypePage(page, limit, keyword));
    }

    @PostMapping("/resource-types/save")
    public Result<String> saveResourceType(@RequestBody Map<String, Object> request) {
        adminService.saveResourceType(request);
        return Result.success("save success");
    }

    @PutMapping("/resource-types/update")
    public Result<String> updateResourceType(@RequestBody Map<String, Object> request) {
        adminService.updateResourceType(request);
        return Result.success("update success");
    }

    @DeleteMapping("/resource-types/delete")
    public Result<String> deleteResourceTypes(@RequestBody List<Long> ids) {
        adminService.deleteResourceTypes(ids);
        return Result.success("delete success");
    }
}
