package com._404._404.controller;

import com._404._404.dto.LoginMember;
import com._404._404.dto.SignupForm;
import com._404._404.service.MemberService;
import com._404._404.web.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

	private final MemberService memberService;

	// ----- 로그인 / 로그아웃 -----
	@GetMapping("/login")
	public String loginPage(@SessionAttribute(name = SessionKeys.LOGIN_MEMBER, required = false) LoginMember member) {
		return member != null ? "redirect:/" : "login";
	}

	@PostMapping("/login")
	public String login(@RequestParam String loginId,
						@RequestParam String password,
						HttpServletRequest request, Model model) {
		Optional<LoginMember> member = memberService.authenticate(loginId.trim(), password);
		if (member.isEmpty()) {
			model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
			model.addAttribute("loginId", loginId);
			return "login";
		}
		// 세션 고정 공격 방지: 기존 세션을 폐기하고 새 세션 발급
		HttpSession old = request.getSession(false);
		if (old != null) {
			old.invalidate();
		}
		request.getSession(true).setAttribute(SessionKeys.LOGIN_MEMBER, member.get());
		return "redirect:/";
	}

	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login?logout";
	}

	// ----- 회원가입 -----
	@GetMapping("/signup")
	public String signupPage(Model model) {
		model.addAttribute("signupForm", new SignupForm());
		return "signup";
	}

	@PostMapping("/signup")
	public String signup(@ModelAttribute SignupForm signupForm, Model model) {
		Map<String, String> errors = signupForm.validate();
		if (errors.isEmpty()) {
			try {
				memberService.signup(signupForm);
				return "redirect:/login?signup";
			} catch (IllegalStateException e) {
				errors.put("loginId", e.getMessage());
			}
		}
		model.addAttribute("errors", errors);
		return "signup";
	}

	// ----- 약관 -----
	@GetMapping("/terms")
	public String terms() {
		return "terms";
	}

	@GetMapping("/privacy")
	public String privacy() {
		return "privacy";
	}
}
