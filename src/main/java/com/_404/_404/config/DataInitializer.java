package com._404._404.config;

import com._404._404.domain.DailyStat;
import com._404._404.domain.Member;
import com._404._404.domain.MemberStatus;
import com._404._404.domain.Notice;
import com._404._404.domain.OperationStatus;
import com._404._404.domain.Qna;
import com._404._404.domain.Station;
import com._404._404.domain.Train;
import com._404._404.domain.TrainDirection;
import com._404._404.domain.TransitService;
import com._404._404.repository.DailyStatRepository;
import com._404._404.repository.MemberRepository;
import com._404._404.repository.NoticeRepository;
import com._404._404.repository.QnaRepository;
import com._404._404.repository.StationRepository;
import com._404._404.repository.TrainRepository;
import com._404._404.repository.TransitServiceRepository;
import com._404._404.service.DashboardService;
import com._404._404.util.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** 시연용 샘플 데이터. app.seed-sample-data=true 일 때, 비어 있는 테이블에만 넣는다 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-sample-data", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

	private static final String[] DEMO_NAMES = {
			"이서연", "박민준", "정우성", "한지민", "오세훈", "조예린", "윤지호", "김지현", "이준원", "최지은"};

	private final MemberRepository memberRepository;
	private final NoticeRepository noticeRepository;
	private final QnaRepository qnaRepository;
	private final TransitServiceRepository transitServiceRepository;
	private final TrainRepository trainRepository;
	private final StationRepository stationRepository;
	private final DailyStatRepository dailyStatRepository;
	private final PasswordHasher passwordHasher;

	@Override
	public void run(String... args) {
		seedBasic();
		seedAdminAndDemoMembers();
		seedTransit();
		seedDailyStats();
	}

	private void seedBasic() {
		if (memberRepository.count() == 0) {
			memberRepository.save(Member.builder()
					.loginId("hong")
					.passwordHash(passwordHasher.encode("hong1234!"))
					.name("홍길동")
					.email("hong@example.com")
					.phone("010-1234-5678")
					.build());
		}

		if (noticeRepository.count() == 0) {
			noticeRepository.save(notice("인천 패스 안내", 106, 2026, 10, 1, 8, 26));
			noticeRepository.save(notice("운행 지연 안내", 107, 2026, 10, 1, 11, 6));
			noticeRepository.save(notice("시설 점검 일정 안내", 108, 2026, 10, 1, 13, 45));
			noticeRepository.save(notice("7호선 운행 재개 안내", 109, 2026, 10, 1, 16, 22));
			noticeRepository.save(notice("교통카드 이용 안내 변경사항", 110, 2026, 10, 1, 19, 36));
		}

		if (qnaRepository.count() == 0) {
			qnaRepository.save(qna("에어컨 좀 틀어주세요 ㅠㅠ", "이무개", 103, 2026, 9, 30, 16, 45));
			qnaRepository.save(qna("막차 시간 언제인가요?", "김아무", 104, 2026, 9, 30, 22, 25));
			qnaRepository.save(qna("보관함 있나요??", "홍길동", 105, 2026, 10, 1, 5, 18));
		}
	}

	private void seedAdminAndDemoMembers() {
		if (!memberRepository.existsByLoginId("admin")) {
			memberRepository.save(Member.builder()
					.loginId("admin")
					.passwordHash(passwordHasher.encode("admin1234!"))
					.name("관리자")
					.email("admin@example.com")
					.role(Member.ROLE_ADMIN)
					.build());
		}

		if (memberRepository.count() < 10) {
			String demoHash = passwordHasher.encode("demo1234!");
			List<Member> demo = new ArrayList<>();
			for (int i = 1; i <= 129; i++) {
				MemberStatus status = i <= 98 ? MemberStatus.ACTIVE
						: i <= 119 ? MemberStatus.BLOCKED : MemberStatus.BANNED;
				demo.add(Member.builder()
						.loginId(String.format("user%03d", i))
						.passwordHash(demoHash)
						.name(DEMO_NAMES[(i - 1) % DEMO_NAMES.length])
						.email(String.format("user%03d@example.com", i))
						.phone(String.format("010-%04d-%04d", 1000 + i, 5000 + i))
						.createdAt(LocalDateTime.of(2026, 8, 1, 9, 0).plusHours(i * 8L))
						.status(status)
						.build());
			}
			memberRepository.saveAll(demo);
		}
	}

	private void seedTransit() {
		if (transitServiceRepository.count() == 0) {
			List<TransitService> services = new ArrayList<>();
			for (int i = 1; i <= 110; i++) {
				OperationStatus status = i <= 80 ? OperationStatus.NORMAL
						: i <= 105 ? OperationStatus.MAINTENANCE : OperationStatus.FAULT;
				services.add(TransitService.builder()
						.name(String.format("교통서비스 %03d", i))
						.status(status)
						.build());
			}
			transitServiceRepository.saveAll(services);
		}

		if (trainRepository.count() == 0) {
			trainRepository.save(train("1002", "석남 ~ 까치울", "박민준", TrainDirection.UP, OperationStatus.FAULT));
			trainRepository.save(train("1001", "까치울 ~ 석남", "이영희", TrainDirection.DOWN, OperationStatus.MAINTENANCE));
			trainRepository.save(train("1000", "석남 ~ 까치울", "김철수", TrainDirection.UP, OperationStatus.NORMAL));
		}

		if (stationRepository.count() == 0) {
			stationRepository.save(station("산곡", "1002", "박민준", OperationStatus.FAULT));
			stationRepository.save(station("까치울", "1001", "이영희", OperationStatus.MAINTENANCE));
			stationRepository.save(station("석남", "1000", "김철수", OperationStatus.NORMAL));
		}
	}

	private void seedDailyStats() {
		if (dailyStatRepository.count() > 0) {
			return;
		}
		LocalDate today = LocalDate.now(DashboardService.KST);
		long members = memberRepository.count();
		long notices = noticeRepository.count();
		long pending = qnaRepository.countByAnswered(false);

		long[] memberMinus = {30, 25, 20, 16, 14, 12};
		long[] noticeMinus = {7, 6, 5, 4, 3, 2};
		long[] pendingPlus = {0, 1, 2, 2, 3, 3};
		long[] normal = {74, 75, 76, 77, 78, 79};
		long[] maintenance = {28, 27, 27, 26, 27, 26};
		long[] fault = {8, 8, 7, 7, 5, 5};

		for (int i = 0; i < 6; i++) {
			dailyStatRepository.save(DailyStat.builder()
					.statDate(today.minusDays(6 - i))
					.memberCount(Math.max(0, members - memberMinus[i]))
					.noticeCount(Math.max(0, notices - noticeMinus[i]))
					.qnaPendingCount(pending + pendingPlus[i])
					.serviceTotal(normal[i] + maintenance[i] + fault[i])
					.normalCount(normal[i])
					.maintenanceCount(maintenance[i])
					.faultCount(fault[i])
					.build());
		}
	}

	private Notice notice(String title, int views, int y, int mo, int d, int h, int mi) {
		return Notice.builder().title(title).writer("관리자").viewCount(views)
				.createdAt(LocalDateTime.of(y, mo, d, h, mi)).build();
	}

	private Qna qna(String title, String writer, int views, int y, int mo, int d, int h, int mi) {
		return Qna.builder().title(title).writer(writer).viewCount(views)
				.createdAt(LocalDateTime.of(y, mo, d, h, mi)).build();
	}

	private Train train(String no, String route, String driver, TrainDirection direction, OperationStatus status) {
		return Train.builder().trainNo(no).route(route).driver(driver).direction(direction).status(status).build();
	}

	private Station station(String name, String no, String staff, OperationStatus status) {
		return Station.builder().name(name).stationNo(no).staff(staff).status(status).build();
	}
}
