package com.project.controller;

import com.project.service.MemberService;
import com.project.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FindController {

	private final MemberService memberService;


	@GetMapping("/find-id")
	public String findIdForm() {
		return "find-id";
	}

	@PostMapping("/find-id")
	public String findId(@RequestParam String name, @RequestParam String email, Model model) {
		Optional<String> maskedId = memberService.findMaskedLoginId(name, email);
		model.addAttribute("searched", true);
		maskedId.ifPresent(id -> model.addAttribute("maskedId", id));
		return "find-id";
	}


	@GetMapping("/find-password")
	public String findPasswordForm() {
		return "find-password";
	}

	@PostMapping("/find-password")
	public String verify(@RequestParam String loginId,
						 @RequestParam String name,
						 @RequestParam String email,
						 HttpSession session, Model model) {
		if (memberService.verifyIdentity(loginId, name, email)) {
			session.setAttribute(SessionKeys.RESET_LOGIN_ID, loginId.trim());
			return "redirect:/reset-password";
		}
		model.addAttribute("failed", true);
		return "find-password";
	}


	@GetMapping("/reset-password")
	public String resetForm(HttpSession session) {
		if (session.getAttribute(SessionKeys.RESET_LOGIN_ID) == null) {
			return "redirect:/find-password";
		}
		return "reset-password";
	}

	@PostMapping("/reset-password")
	public String reset(@RequestParam String password,
						@RequestParam String passwordConfirm,
						HttpSession session, Model model) {
		String loginId = (String) session.getAttribute(SessionKeys.RESET_LOGIN_ID);
		if (loginId == null) {
			return "redirect:/find-password";
		}
		if (password.length() < 8 || password.length() > 30) {
			model.addAttribute("error", "비밀번호는 8~30자로 입력해 주세요.");
			return "reset-password";
		}
		if (!password.equals(passwordConfirm)) {
			model.addAttribute("error", "비밀번호 확인이 일치하지 않습니다.");
			return "reset-password";
		}
		memberService.resetPassword(loginId, password);
		session.removeAttribute(SessionKeys.RESET_LOGIN_ID);
		return "redirect:/login?reset";
	}
}
