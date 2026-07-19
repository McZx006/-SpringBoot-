package com.example.platform.controller;

import com.example.platform.common.SecurityConstants;
import com.example.platform.common.Result;
import com.example.platform.dto.LoginRequest;
import com.example.platform.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody Map<String, Object> request) {
        authService.register(request);
        return Result.success("register success");
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> info(@RequestHeader(value = "Token", required = false) String token) {
        return Result.success(authService.info(token));
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile(@RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr,
                                               @RequestAttribute(SecurityConstants.REQUEST_ROLE) String role) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        return Result.success(authService.profile(userId, role));
    }

    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestAttribute(SecurityConstants.REQUEST_USER_ID) Object userIdAttr,
                                        @RequestAttribute(SecurityConstants.REQUEST_ROLE) String role,
                                        @RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(String.valueOf(userIdAttr));
        authService.updateProfile(userId, role, request);
        return Result.success("update success");
    }

    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Token", required = false) String token) {
        authService.logout(token);
        return Result.success("logout success");
    }
}
