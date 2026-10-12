package com._404._404.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/** 로그인하지 않은 사용자가 보호된 페이지에 접근하면 로그인 화면으로 보낸다 */
public class LoginCheckInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute(SessionKeys.LOGIN_MEMBER) == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return false;
		}
		return true;
	}
}
