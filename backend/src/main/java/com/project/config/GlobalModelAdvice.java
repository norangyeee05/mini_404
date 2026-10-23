package com.project.config;

import com.project.dto.LoginMember;
import com.project.web.SessionKeys;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;


@ControllerAdvice
public class GlobalModelAdvice {

	@ModelAttribute("loginMember")
	public LoginMember loginMember(
			@SessionAttribute(name = SessionKeys.LOGIN_MEMBER, required = false) LoginMember loginMember) {
		return loginMember;
	}
}
