package com._404._404.controller;

import com._404._404.dto.DashboardResponse;
import com._404._404.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminDashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/dashboard")
	public DashboardResponse dashboard() {
		return dashboardService.getDashboard();
	}
}
