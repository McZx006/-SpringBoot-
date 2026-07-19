package com.example.platform.service;

import com.example.platform.dto.LoginRequest;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginRequest request);

    void register(Map<String, Object> request);

    Map<String, Object> info(String token);

    Map<String, Object> profile(Long userId, String role);

    void updateProfile(Long userId, String role, Map<String, Object> request);

    void logout(String token);
}
