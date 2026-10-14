package com.project.controller;

import com.project.dto.DashboardResponse;
import com.project.service.DashboardService;
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
