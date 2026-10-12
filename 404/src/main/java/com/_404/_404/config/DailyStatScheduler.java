package com._404._404.config;

import com._404._404.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DailyStatScheduler {

	private final DashboardService dashboardService;

	@Scheduled(cron = "0 59 23 * * *", zone = "Asia/Seoul")
	public void saveTodaySnapshot() {
		dashboardService.saveSnapshot(LocalDate.now(DashboardService.KST));
	}
}
