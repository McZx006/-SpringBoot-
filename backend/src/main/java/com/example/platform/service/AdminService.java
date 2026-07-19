package com.example.platform.service;

import com.example.platform.common.PageResult;
import com.example.platform.entity.Xueyuan;

import java.util.List;
import java.util.Map;

public interface AdminService {
    PageResult<Map<String, Object>> students(int page, int limit, String xuehao, String name);

    void saveStudent(Xueyuan xueyuan);

    void updateStudent(Xueyuan xueyuan);

    void deleteStudents(List<Long> ids);

    List<Map<String, Object>> resourceTypes();

    PageResult<Map<String, Object>> resourceTypePage(int page, int limit, String keyword);

    void saveResourceType(Map<String, Object> request);

    void updateResourceType(Map<String, Object> request);

    void deleteResourceTypes(List<Long> ids);
}
