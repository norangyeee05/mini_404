package com.project.web;

import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.domain.Member;
import com.project.domain.Role;
import com.project.dto.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        Object apiUser = session == null ? null : session.getAttribute(SessionKeys.LOGIN_USER);
        Object pageUser = session == null ? null : session.getAttribute(com.project.web.SessionKeys.LOGIN_MEMBER);
        if (apiUser instanceof LoginUser user && user.role() == Role.ADMIN) return true;
        if (pageUser instanceof LoginMember member && Member.ROLE_ADMIN.equals(member.role())) return true;
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
