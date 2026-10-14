package com.project.controller;

import com.project.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final MainService mainService;

	@GetMapping("/")
	public String main(Model model) {
		model.addAttribute("stations", mainService.getStations());
		model.addAttribute("notices", mainService.getLatestNotices());
		model.addAttribute("qnas", mainService.getLatestQnas());
		return "main";
	}
}
