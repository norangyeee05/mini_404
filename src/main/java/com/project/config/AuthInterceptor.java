package com.project.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.project.common.BusinessException;
import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.domain.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 세션 기반 접근 제어 (API 명세서 권한 표기 기준)
 * - /api/admin/**               : 관리자만 (미로그인 401, 일반 회원 403)
 * - /api/members/**             : 로그인 필요 (401)
 * - POST /api/auth/logout       : 로그인 필요 (401)
 * - POST /api/resources/requests: 로그인 필요 (401) - 일반 회원 여부는 컨트롤러에서 확인(403)
 * 그 외(자료실 조회/다운로드, 회원가입/로그인 등)는 전체 공개
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // CORS preflight
        }

        String path = request.getRequestURI();
        String method = request.getMethod();

        HttpSession session = request.getSession(false);
        LoginUser user = session == null ? null : (LoginUser) session.getAttribute(SessionKeys.LOGIN_USER);

        if (path.equals("/api/admin") || path.startsWith("/api/admin/")) {
            if (user == null) {
                throw BusinessException.unauthorized("로그인이 필요합니다.");
            }
            if (user.role() != Role.ADMIN) {
                throw BusinessException.forbidden("접근 권한이 없습니다.");
            }
            return true;
        }

        boolean needLogin = path.equals("/api/members") || path.startsWith("/api/members/")
                || path.equals("/api/auth/logout")
                || ("POST".equalsIgnoreCase(method) && path.equals("/api/resources/requests"));

        if (needLogin && user == null) {
            throw BusinessException.unauthorized("로그인이 필요합니다.");
        }
        return true;
    }
}
