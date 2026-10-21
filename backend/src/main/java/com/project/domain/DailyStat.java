package com.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "daily_stat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "stat_date", nullable = false, unique = true)
	private LocalDate statDate;

	@Column(name = "member_count", nullable = false)
	private long memberCount;

	@Column(name = "notice_count", nullable = false)
	private long noticeCount;

	@Column(name = "qna_pending_count", nullable = false)
	private long qnaPendingCount;

	@Column(name = "service_total", nullable = false)
	private long serviceTotal;

	@Column(name = "normal_count", nullable = false)
	private long normalCount;

	@Column(name = "maintenance_count", nullable = false)
	private long maintenanceCount;

	@Column(name = "fault_count", nullable = false)
	private long faultCount;

	@Builder
	public DailyStat(LocalDate statDate, long memberCount, long noticeCount, long qnaPendingCount,
					 long serviceTotal, long normalCount, long maintenanceCount, long faultCount) {
		this.statDate = statDate;
		record(memberCount, noticeCount, qnaPendingCount, serviceTotal, normalCount, maintenanceCount, faultCount);
	}

	public void record(long memberCount, long noticeCount, long qnaPendingCount,
					   long serviceTotal, long normalCount, long maintenanceCount, long faultCount) {
		this.memberCount = memberCount;
		this.noticeCount = noticeCount;
		this.qnaPendingCount = qnaPendingCount;
		this.serviceTotal = serviceTotal;
		this.normalCount = normalCount;
		this.maintenanceCount = maintenanceCount;
		this.faultCount = faultCount;
	}
}
