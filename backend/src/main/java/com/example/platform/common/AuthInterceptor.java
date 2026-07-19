package com.example.platform.common;

import com.example.platform.mapper.TokenMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenMapper tokenMapper;

    public AuthInterceptor(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        String method = request.getMethod();
        boolean publicApi = isPublicApi(uri, method);
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (token == null || token.trim().isEmpty()) {
            if (publicApi) {
                return true;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        Map<String, Object> tokenInfo = tokenMapper.findByToken(token);
        if (tokenInfo == null) {
            if (publicApi) {
                return true;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String role = String.valueOf(tokenInfo.get("role"));
        request.setAttribute(SecurityConstants.REQUEST_USER_ID, tokenInfo.get("userId"));
        request.setAttribute(SecurityConstants.REQUEST_ROLE, role);
        if (publicApi) {
            return true;
        }
        if (isAdminApi(uri, method) && !RoleConstants.ADMIN.equals(role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    private boolean isPublicApi(String uri, String method) {
        if (!"GET".equalsIgnoreCase(method)) {
            return false;
        }
        return uri.equals("/api/news/page")
                || uri.matches("/api/news/\\d+")
                || uri.equals("/api/banners/list")
                || uri.equals("/api/resources/page")
                || uri.matches("/api/resources/\\d+")
                || uri.startsWith("/api/resources/detail/")
                || uri.startsWith("/api/resources/comments/")
                || uri.equals("/api/resource-types/list")
                || uri.equals("/api/exampapers/page")
                || uri.equals("/api/forum/page")
                || uri.matches("/api/forum/\\d+")
                || uri.startsWith("/api/forum/comments/")
                || uri.equals("/api/file/download")
                || uri.startsWith("/api/file/preview/");
    }

    private boolean isAdminApi(String uri, String method) {
        if (uri.contains("/xueyuan")) {
            return true;
        }
        if (uri.contains("/resource-types") && !"GET".equalsIgnoreCase(method)) {
            return true;
        }
        if (uri.contains("/resources") && !"GET".equalsIgnoreCase(method) && !uri.equals("/api/resources/comment")) {
            return true;
        }
        if (uri.contains("/exampapers") && !"GET".equalsIgnoreCase(method)) {
            return true;
        }
        return uri.contains("/examquestions")
                || uri.contains("/storeup/delete")
                || uri.contains("/messages/reply")
                || uri.contains("/messages/delete")
                || uri.contains("/news/save")
                || uri.contains("/news/update")
                || uri.contains("/news/delete")
                || uri.contains("/banners");
    }
}
