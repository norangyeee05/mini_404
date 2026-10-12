package com._404._404.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 아직 담당자가 구현하지 않은 메뉴의 임시 화면.
 * 각 기능(공지사항 등)을 구현할 때 이 매핑을 해당 컨트롤러로 옮기면 된다.
 */
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
