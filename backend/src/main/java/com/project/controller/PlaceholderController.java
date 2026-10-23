package com.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;





@Controller
public class PlaceholderController {

	@GetMapping("/notices")
	public String notices(Model model) {
		return page(model, "공지사항");
	}

	@GetMapping("/resources")
	public String resources(Model model) {
		return page(model, "자료실");
	}

	@GetMapping("/qna")
	public String qna(Model model) {
		return page(model, "Q&A");
	}

	@GetMapping("/railway")
	public String railway(Model model) {
		return page(model, "철도정보");
	}

	@GetMapping("/mypage")
	public String mypage(Model model) {
		return page(model, "마이페이지");
	}

	private String page(Model model, String title) {
		model.addAttribute("pageTitle", title);
		return "placeholder";
	}
}
