package com._404._404.config;

import com._404._404.dto.LoginMember;
import com._404._404.web.SessionKeys;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

/** 모든 화면에서 ${loginMember}로 로그인 회원 정보를 쓸 수 있게 한다 */
@ControllerAdvice
public class GlobalModelAdvice {

	@ModelAttribute("loginMember")
	public LoginMember loginMember(
			@SessionAttribute(name = SessionKeys.LOGIN_MEMBER, required = false) LoginMember loginMember) {
		return loginMember;
	}
}
