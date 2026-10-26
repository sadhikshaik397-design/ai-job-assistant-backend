package com.jobassistant.job_assistant.config;

import com.jobassistant.job_assistant.service.AuthTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthTokenInterceptor implements HandlerInterceptor {
    public static final String USER_ID_ATTRIBUTE = "authenticatedUserId";
    private final AuthTokenService tokenService;

    public AuthTokenInterceptor(AuthTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in to access this resource.");
            return false;
        }
        Long userId = tokenService.userIdFor(header.substring(7).trim());
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Your login has expired. Please log in again.");
            return false;
        }
        request.setAttribute(USER_ID_ATTRIBUTE, userId);
        return true;
    }
}
