package com.example.platform.service.impl;

import com.example.platform.common.RoleConstants;
import com.example.platform.dto.LoginRequest;
import com.example.platform.entity.User;
import com.example.platform.entity.Xueyuan;
import com.example.platform.mapper.TokenMapper;
import com.example.platform.mapper.UserMapper;
import com.example.platform.mapper.XueyuanMapper;
import com.example.platform.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final TokenMapper tokenMapper;
    private final XueyuanMapper xueyuanMapper;

    public AuthServiceImpl(UserMapper userMapper, TokenMapper tokenMapper, XueyuanMapper xueyuanMapper) {
        this.userMapper = userMapper;
        this.tokenMapper = tokenMapper;
        this.xueyuanMapper = xueyuanMapper;
    }

    @Override
    public Map<String, Object> login(LoginRequest request) {
        User user = userMapper.findByUsernameAndRole(request.getUsername(), request.getRole());
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 12);
        tokenMapper.insert(user.getId(), token, calendar.getTime());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        return data;
    }

    @Override
    @Transactional
    public void register(Map<String, Object> request) {
        String username = String.valueOf(request.get("username"));
        String password = String.valueOf(request.get("password"));
        if (userMapper.findByUsernameAndRole(username, "student") != null) {
            throw new IllegalArgumentException("账号已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("student");
        user.setStatus(1);
        userMapper.insert(user);

        Xueyuan xueyuan = new Xueyuan();
        xueyuan.setUserId(user.getId());
        xueyuan.setXuehao(String.valueOf(request.getOrDefault("xuehao", username)));
        xueyuan.setName(String.valueOf(request.getOrDefault("name", username)));
        xueyuan.setPhone(String.valueOf(request.getOrDefault("phone", "")));
        xueyuan.setEmail(String.valueOf(request.getOrDefault("email", "")));
        xueyuanMapper.insert(xueyuan);
    }

    @Override
    public Map<String, Object> info(String token) {
        Map<String, Object> tokenInfo = tokenMapper.findByToken(token);
        if (tokenInfo == null) {
            throw new IllegalArgumentException("登录已失效");
        }
        Object userId = tokenInfo.get("userId");
        User user = userMapper.findById(Long.valueOf(String.valueOf(userId)));
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        return data;
    }

    @Override
    public Map<String, Object> profile(Long userId, String role) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("status", user.getStatus());
        if (RoleConstants.STUDENT.equals(role)) {
            Map<String, Object> xueyuan = xueyuanMapper.findByUserId(userId);
            if (xueyuan != null) {
                data.putAll(xueyuan);
            }
        }
        return data;
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, String role, Map<String, Object> request) {
        if (!RoleConstants.STUDENT.equals(role)) {
            return;
        }
        Map<String, Object> current = xueyuanMapper.findByUserId(userId);
        if (current == null) {
            throw new IllegalArgumentException("学员资料不存在");
        }
        Xueyuan xueyuan = new Xueyuan();
        xueyuan.setId(Long.valueOf(String.valueOf(current.get("id"))));
        xueyuan.setUserId(userId);
        xueyuan.setXuehao(String.valueOf(request.getOrDefault("xuehao", current.get("xuehao"))));
        xueyuan.setName(String.valueOf(request.getOrDefault("name", current.get("name"))));
        xueyuan.setGender(String.valueOf(request.getOrDefault("gender", current.get("gender"))));
        xueyuan.setPhone(String.valueOf(request.getOrDefault("phone", current.get("phone"))));
        xueyuan.setEmail(String.valueOf(request.getOrDefault("email", current.get("email"))));
        xueyuan.setAvatar(String.valueOf(request.getOrDefault("avatar", current.get("avatar"))));
        xueyuanMapper.update(xueyuan);
        Object password = request.get("password");
        if (password != null) {
            String passwordText = String.valueOf(password).trim();
            if (!passwordText.isEmpty()) {
                userMapper.updatePassword(userId, passwordText);
            }
        }
    }

    @Override
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            tokenMapper.deleteByToken(token);
        }
    }
}
