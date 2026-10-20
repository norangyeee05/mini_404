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

/** Session based API authorization. */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);
        LoginUser user = session == null ? null : (LoginUser) session.getAttribute(SessionKeys.LOGIN_USER);

        if (path.equals("/api/admin") || path.startsWith("/api/admin/")) {
            if (user == null) throw BusinessException.unauthorized("로그인이 필요합니다.");
            if (user.role() != Role.ADMIN && user.role() != Role.WEB_ADMIN) throw BusinessException.forbidden("접근 권한이 없습니다.");
            if (user.role() == Role.WEB_ADMIN) {
                String method = request.getMethod();
                boolean dashboard = path.equals("/api/admin/dashboard");
                boolean roleChange = path.matches("/api/admin/members/\\d+/role");
                boolean railMutation = path.startsWith("/api/admin/line7/stations") || path.startsWith("/api/admin/line7/trains");
                boolean mutation = !"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method);
                if (dashboard || roleChange || (railMutation && mutation)) throw BusinessException.forbidden("접근 권한이 없습니다.");
            }
            return true;
        }

        boolean qnaPath = path.equals("/api/qna") || path.startsWith("/api/qna/");
        if (qnaPath) {
            if (user == null) throw BusinessException.unauthorized("Q&A는 로그인 후 이용할 수 있습니다.");
            if (user.role() != Role.USER) throw BusinessException.forbidden("회원만 Q&A를 이용할 수 있습니다.");
        }

        boolean resourcePath = path.equals("/api/resources") || path.startsWith("/api/resources/");
        if (resourcePath && user == null) {
            throw BusinessException.unauthorized("자료실은 로그인 후 이용할 수 있습니다.");
        }

        if (path.matches("/api/line7/stations/\\d+/transfer")) {
            if (user == null) throw BusinessException.unauthorized("빠른 환승 정보는 로그인 후 이용할 수 있습니다.");
        }

        boolean needLogin = path.equals("/api/members") || path.startsWith("/api/members/")
                || path.equals("/api/auth/logout");
        if (needLogin && user == null) throw BusinessException.unauthorized("로그인이 필요합니다.");
        return true;
    }
}

