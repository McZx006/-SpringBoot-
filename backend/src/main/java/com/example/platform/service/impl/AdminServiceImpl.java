package com.example.platform.service.impl;

import com.example.platform.common.PageResult;
import com.example.platform.entity.Xueyuan;
import com.example.platform.mapper.ResourceTypeMapper;
import com.example.platform.mapper.XueyuanMapper;
import com.example.platform.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    private final XueyuanMapper xueyuanMapper;
    private final ResourceTypeMapper resourceTypeMapper;

    public AdminServiceImpl(XueyuanMapper xueyuanMapper, ResourceTypeMapper resourceTypeMapper) {
        this.xueyuanMapper = xueyuanMapper;
        this.resourceTypeMapper = resourceTypeMapper;
    }

    @Override
    public PageResult<Map<String, Object>> students(int page, int limit, String xuehao, String name) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                xueyuanMapper.page(offset, limit, xuehao, name),
                xueyuanMapper.count(xuehao, name),
                page,
                limit
        );
    }

    @Override
    public void saveStudent(Xueyuan xueyuan) {
        validateStudent(xueyuan, null);
        xueyuanMapper.insert(xueyuan);
    }

    @Override
    public void updateStudent(Xueyuan xueyuan) {
        validateStudent(xueyuan, xueyuan.getId());
        xueyuanMapper.update(xueyuan);
    }

    @Override
    public void deleteStudents(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            xueyuanMapper.deleteBatch(ids);
        }
    }

    @Override
    public List<Map<String, Object>> resourceTypes() {
        return resourceTypeMapper.list();
    }

    @Override
    public PageResult<Map<String, Object>> resourceTypePage(int page, int limit, String keyword) {
        int offset = (Math.max(page, 1) - 1) * limit;
        return new PageResult<>(
                resourceTypeMapper.page(offset, limit, keyword),
                resourceTypeMapper.count(keyword),
                page,
                limit
        );
    }

    @Override
    public void saveResourceType(Map<String, Object> request) {
        String name = String.valueOf(request.getOrDefault("name", "")).trim();
        Integer sort = Integer.valueOf(String.valueOf(request.getOrDefault("sort", 0)));
        if (name.isEmpty()) {
            throw new IllegalArgumentException("资料类型名称不能为空");
        }
        if (resourceTypeMapper.findByName(name) != null) {
            throw new IllegalArgumentException("资料类型名称已存在");
        }
        resourceTypeMapper.insert(name, sort);
    }

    @Override
    public void updateResourceType(Map<String, Object> request) {
        Long id = Long.valueOf(String.valueOf(request.get("id")));
        String name = String.valueOf(request.getOrDefault("name", "")).trim();
        Integer sort = Integer.valueOf(String.valueOf(request.getOrDefault("sort", 0)));
        if (name.isEmpty()) {
            throw new IllegalArgumentException("资料类型名称不能为空");
        }
        Map<String, Object> current = resourceTypeMapper.findByName(name);
        if (current != null && !String.valueOf(id).equals(String.valueOf(current.get("id")))) {
            throw new IllegalArgumentException("资料类型名称已存在");
        }
        resourceTypeMapper.update(id, name, sort);
    }

    @Override
    public void deleteResourceTypes(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            resourceTypeMapper.deleteBatch(ids);
        }
    }

    private void validateStudent(Xueyuan xueyuan, Long currentId) {
        String xuehao = xueyuan.getXuehao() == null ? "" : xueyuan.getXuehao().trim();
        String name = xueyuan.getName() == null ? "" : xueyuan.getName().trim();
        if (xuehao.isEmpty()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (xueyuan.getPhone() != null && !xueyuan.getPhone().trim().isEmpty()
                && !xueyuan.getPhone().trim().matches("^1\\d{10}$")) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
        if (xueyuan.getEmail() != null && !xueyuan.getEmail().trim().isEmpty()
                && !xueyuan.getEmail().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
        Map<String, Object> sameXuehao = xueyuanMapper.findByXuehao(xuehao);
        if (sameXuehao != null && (currentId == null || !String.valueOf(currentId).equals(String.valueOf(sameXuehao.get("id"))))) {
            throw new IllegalArgumentException("学号已存在");
        }
    }
}
