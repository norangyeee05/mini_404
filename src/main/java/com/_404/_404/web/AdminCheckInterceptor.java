package com._404._404.web;

import com._404._404.domain.Member;
import com._404._404.dto.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(false);
		Object attribute = session == null ? null : session.getAttribute(SessionKeys.LOGIN_MEMBER);
		if (attribute instanceof LoginMember member && Member.ROLE_ADMIN.equals(member.role())) {
			return true;
		}
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
		return false;
	}
}
