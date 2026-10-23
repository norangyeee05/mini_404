package com.project.service;

import com.project.domain.DailyStat;
import com.project.domain.MemberStatus;
import com.project.domain.OperationStatus;
import com.project.dto.DashboardResponse;
import com.project.dto.DashboardResponse.Cards;
import com.project.dto.DashboardResponse.CountCard;
import com.project.dto.DashboardResponse.Share;
import com.project.dto.DashboardResponse.ShareChart;
import com.project.dto.DashboardResponse.ServiceCard;
import com.project.dto.DashboardResponse.StationRow;
import com.project.dto.DashboardResponse.TrainRow;
import com.project.dto.DashboardResponse.TrendPoint;
import com.project.repository.DailyStatRepository;
import com.project.repository.MemberRepository;
import com.project.repository.NoticeRepository;
import com.project.repository.QnaRepository;
import com.project.repository.StationRepository;
import com.project.repository.TrainRepository;
import com.project.repository.TransitServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	public static final ZoneId KST = ZoneId.of("Asia/Seoul");

	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");
	private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("MM.dd");

	private final MemberRepository memberRepository;
	private final NoticeRepository noticeRepository;
	private final QnaRepository qnaRepository;
	private final TransitServiceRepository transitServiceRepository;
	private final TrainRepository trainRepository;
	private final StationRepository stationRepository;
	private final DailyStatRepository dailyStatRepository;

	public DashboardResponse getDashboard() {
		LocalDateTime now = LocalDateTime.now(KST);
		LocalDate today = now.toLocalDate();
		Live live = readLive();
		Optional<DailyStat> yesterday = dailyStatRepository.findByStatDate(today.minusDays(1));

		Cards cards = new Cards(
				new CountCard(live.members(), diff(live.members(), yesterday.map(DailyStat::getMemberCount))),
				new CountCard(live.notices(), diff(live.notices(), yesterday.map(DailyStat::getNoticeCount))),
				new CountCard(live.qnaPending(), diff(live.qnaPending(), yesterday.map(DailyStat::getQnaPendingCount))),
				new ServiceCard(live.serviceTotal(), live.normal(), trainRepository.countByStatusAndActiveTrue(OperationStatus.NORMAL)));

		ShareChart serviceStatus = new ShareChart(live.serviceTotal(), List.of(
				serviceShare(OperationStatus.NORMAL, live.normal(), live.serviceTotal()),
				serviceShare(OperationStatus.MAINTENANCE, live.maintenance(), live.serviceTotal()),
				serviceShare(OperationStatus.FAULT, live.fault(), live.serviceTotal())));

		List<TrendPoint> weeklyTrend = new ArrayList<>();
		for (DailyStat s : dailyStatRepository.findByStatDateBetweenOrderByStatDateAsc(
				today.minusDays(6), today.minusDays(1))) {
			weeklyTrend.add(new TrendPoint(s.getStatDate().format(DAY),
					s.getNormalCount(), s.getMaintenanceCount(), s.getFaultCount()));
		}
		weeklyTrend.add(new TrendPoint(today.format(DAY), live.normal(), live.maintenance(), live.fault()));

		ShareChart memberStatus = new ShareChart(live.members(), List.of(
				memberShare(MemberStatus.ACTIVE, live.members()),
				memberShare(MemberStatus.BLOCKED, live.members()),
				memberShare(MemberStatus.INACTIVE, live.members()),
				memberShare(MemberStatus.BANNED, live.members())));

		List<TrainRow> trains = trainRepository.findTop3ByOrderByUpdatedAtDescIdDesc().stream()
				.map(TrainRow::from)
				.toList();

		List<StationRow> stations = stationRepository.findTop3ByOrderByUpdatedAtDescIdDesc().stream()
				.map(StationRow::from)
				.toList();

		return new DashboardResponse(formatNow(now), cards, serviceStatus, weeklyTrend, memberStatus, trains, stations);
	}

	@Transactional
	public void saveSnapshot(LocalDate date) {
		Live live = readLive();
		DailyStat stat = dailyStatRepository.findByStatDate(date).orElse(null);
		if (stat == null) {
			stat = DailyStat.builder()
					.statDate(date)
					.memberCount(live.members())
					.noticeCount(live.notices())
					.qnaPendingCount(live.qnaPending())
					.serviceTotal(live.serviceTotal())
					.normalCount(live.normal())
					.maintenanceCount(live.maintenance())
					.faultCount(live.fault())
					.build();
		} else {
			stat.record(live.members(), live.notices(), live.qnaPending(),
					live.serviceTotal(), live.normal(), live.maintenance(), live.fault());
		}
		dailyStatRepository.save(stat);
	}

	private Live readLive() {
		return new Live(
				memberRepository.count(),
				noticeRepository.count(),
				qnaRepository.countByAnswered(false),
				transitServiceRepository.count(),
				transitServiceRepository.countByStatus(OperationStatus.NORMAL),
				transitServiceRepository.countByStatus(OperationStatus.MAINTENANCE),
				transitServiceRepository.countByStatus(OperationStatus.FAULT));
	}

	private Share serviceShare(OperationStatus status, long count, long total) {
		return new Share(status.name(), status.getLabel(), count, percent(count, total));
	}

	private Share memberShare(MemberStatus status, long total) {
		long count = memberRepository.countByStatus(status);
		return new Share(status.name(), status.getLabel(), count, percent(count, total));
	}

	private long diff(long current, Optional<Long> before) {
		return before.map(b -> current - b).orElse(0L);
	}

	private double percent(long count, long total) {
		return total == 0 ? 0 : Math.round(count * 10000.0 / total) / 100.0;
	}

	private String formatNow(LocalDateTime now) {
		String dayOfWeek = now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
		return now.format(DATE) + "(" + dayOfWeek + ") " + now.format(TIME);
	}

	private record Live(long members, long notices, long qnaPending,
						long serviceTotal, long normal, long maintenance, long fault) {
	}
}
