package com.project.config;

import com.project.dto.LoginMember;
import com.project.web.SessionKeys;
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
