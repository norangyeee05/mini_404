package com._404._404.service;

import com._404._404.domain.DailyStat;
import com._404._404.domain.MemberStatus;
import com._404._404.domain.OperationStatus;
import com._404._404.dto.DashboardResponse;
import com._404._404.dto.DashboardResponse.Cards;
import com._404._404.dto.DashboardResponse.CountCard;
import com._404._404.dto.DashboardResponse.Share;
import com._404._404.dto.DashboardResponse.ShareChart;
import com._404._404.dto.DashboardResponse.ServiceCard;
import com._404._404.dto.DashboardResponse.StationRow;
import com._404._404.dto.DashboardResponse.TrainRow;
import com._404._404.dto.DashboardResponse.TrendPoint;
import com._404._404.repository.DailyStatRepository;
import com._404._404.repository.MemberRepository;
import com._404._404.repository.NoticeRepository;
import com._404._404.repository.QnaRepository;
import com._404._404.repository.StationRepository;
import com._404._404.repository.TrainRepository;
import com._404._404.repository.TransitServiceRepository;
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
				new ServiceCard(live.serviceTotal(), live.normal()));

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

		List<TrainRow> trains = trainRepository.findTop3ByOrderByIdDesc().stream()
				.map(t -> new TrainRow(t.getId(), t.getStatus().name(), t.getStatus().getOperationLabel(),
						t.getRoute(), t.getTrainNo(), t.getDriver(),
						t.getDirection().name(), t.getDirection().getLabel()))
				.toList();

		List<StationRow> stations = stationRepository.findTop3ByOrderByIdDesc().stream()
				.map(s -> new StationRow(s.getId(), s.getStatus().name(), s.getStatus().getOperationLabel(),
						s.getName(), s.getStationNo(), s.getStaff()))
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
