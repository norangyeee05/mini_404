package com.project.config;

import com.project.domain.Admin;
import com.project.domain.DailyStat;
import com.project.domain.InquiryType;
import com.project.domain.Member;
import com.project.domain.MemberStatus;
import com.project.domain.Notice;
import com.project.domain.OperationStatus;
import com.project.domain.Qna;
import com.project.domain.RequestStatus;
import com.project.domain.ResourcePost;
import com.project.domain.ResourceRequest;
import com.project.domain.Train;
import com.project.domain.TrainDirection;
import com.project.domain.TransitService;
import com.project.repository.AdminRepository;
import com.project.repository.DailyStatRepository;
import com.project.repository.MemberRepository;
import com.project.repository.NoticeRepository;
import com.project.repository.QnaRepository;
import com.project.repository.ResourcePostRepository;
import com.project.repository.ResourceRequestRepository;
import com.project.repository.TrainRepository;
import com.project.repository.TransitServiceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 개발/시연용 더미 데이터 초기화.
 * 기존 데이터를 삭제하지 않으며, 각 대상 테이블의 데이터가 목표 수량보다 적을 때만 부족분을 추가합니다.
 * app.seed-sample-data=true 설정일 때만 실행됩니다.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-sample-data", havingValue = "true")
public class DummyDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DummyDataInitializer.class);
    private static final int MEMBER_TARGET = 120;
    private static final int STANDARD_TARGET = 100;
    private static final int QNA_TARGET = 120;
    private static final int DAILY_STAT_TARGET = 100;

    private static final String[] GIVEN_NAMES = {
            "민준", "서준", "도윤", "예준", "시우", "하준", "주원", "지호", "지후", "준우",
            "서연", "서윤", "지우", "하은", "민서", "지유", "윤서", "채원", "수아", "지민",
            "현우", "유진", "수빈", "예린", "은우", "다은", "유나", "시윤", "건우", "소율"
    };

    private static final String[] NOTICE_TOPICS = {
            "7호선 역사 시설 이용 안내", "엘리베이터 정기 점검 안내", "에스컬레이터 운영 변경 안내",
            "역사 내 편의시설 이용 안내", "교통약자 이동 지원 안내", "고객센터 운영시간 안내",
            "역사 내 분실물 처리 절차", "열차 이용 에티켓 안내", "안전사고 예방 안내",
            "시스템 점검 및 서비스 안내", "자료실 이용 방법 안내", "개인정보 처리방침 안내",
            "민원 접수 방법 안내", "시설 상태 정보 업데이트", "혼잡 시간대 이용 안내",
            "역사 내 자전거 보관 안내", "환승 통로 이용 안내", "서비스 개선 의견 접수 안내",
            "회원 서비스 이용 안내", "월간 운영 현황 안내"
    };

    private static final String[] RESOURCE_TOPICS = {
            "역사 시설 이용 안내서", "교통약자 지원 서비스 안내", "엘리베이터 이용 가이드",
            "에스컬레이터 안전 이용 수칙", "역사 편의시설 안내 자료", "고객 문의 처리 절차",
            "교통서비스 상태 분류 기준", "시설 장애 신고 안내", "역사 내 안전 수칙",
            "7호선 운영 서비스 소개", "개인정보 보호 안내 자료", "관리자 업무 매뉴얼",
            "민원 응대 참고 자료", "시설 관리 점검 체크리스트", "서비스 운영 FAQ",
            "역사 내 분실물 대응 안내", "이용자 접근성 개선 자료", "운영 공지 작성 가이드",
            "시스템 사용 안내서", "월간 서비스 운영 보고서"
    };

    private static final String[] QNA_TOPICS = {
            "엘리베이터 이용 관련 문의", "역사 편의시설 위치 문의", "에스컬레이터 운영 문의",
            "교통약자 이동 지원 문의", "역사 내 분실물 문의", "환승 통로 이용 문의",
            "시설 상태 표시 문의", "회원정보 변경 문의", "공지사항 내용 문의",
            "자료실 자료 요청", "서비스 장애 신고", "역사 이용시간 문의",
            "고객센터 연결 문의", "자전거 보관시설 문의", "역사 내 화장실 위치 문의",
            "서비스 개선 제안", "비밀번호 변경 문의", "민원 처리 현황 문의",
            "열차 이용 안내 문의", "기타 서비스 이용 문의"
    };

    private static final String[] STATION_NAMES = {
            "석남", "산곡", "부평구청", "굴포천", "삼산체육관", "상동", "부천시청",
            "신중동", "춘의", "부천종합운동장", "까치울"
    };

    private static final String[] FACILITIES = {
            "엘리베이터", "에스컬레이터", "장애인 화장실", "고객센터", "물품 보관함",
            "자전거 보관소", "역사 조명", "안내 키오스크", "자동 발매기", "환승 통로"
    };

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final NoticeRepository noticeRepository;
    private final ResourcePostRepository resourcePostRepository;
    private final QnaRepository qnaRepository;
    private final ResourceRequestRepository resourceRequestRepository;
    private final TransitServiceRepository transitServiceRepository;
    private final TrainRepository trainRepository;
    private final DailyStatRepository dailyStatRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Admin admin = adminRepository.findAll().stream().findFirst().orElse(null);
        if (admin == null) {
            log.warn("더미 데이터를 생성하지 않았습니다. 관리자 계정이 없습니다. AdminInitializer 설정을 확인하세요.");
            return;
        }

        seedMembers();
        List<Member> members = memberRepository.findAll();
        if (members.isEmpty()) {
            log.warn("회원 데이터가 없어 Q&A와 자료실 요청 더미 데이터를 생성하지 않았습니다.");
            return;
        }

        seedNotices(admin);
        seedResources(admin);
        seedQna(members);
        seedResourceRequests(members);
        seedTransitServices();
        seedTrains();
        seedDailyStats();

        log.info("더미 데이터 초기화 완료: 회원 {}, 공지 {}, 자료실 {}, Q&A {}, 자료 요청 {}, 교통서비스 {}, 열차 {}, 일별 통계 {}",
                memberRepository.count(), noticeRepository.count(), resourcePostRepository.count(), qnaRepository.count(),
                resourceRequestRepository.count(), transitServiceRepository.count(), trainRepository.count(), dailyStatRepository.count());
    }

    private void seedMembers() {
        long current = memberRepository.count();
        if (current >= MEMBER_TARGET) return;

        List<Member> additions = new ArrayList<>();
        String encodedPassword = passwordEncoder.encode("Demo1234!");
        for (int i = 1; current + additions.size() < MEMBER_TARGET; i++) {
            String loginId = String.format("demo_member_%03d", i);
            if (memberRepository.existsByLoginId(loginId)) continue;

            String name = GIVEN_NAMES[(i - 1) % GIVEN_NAMES.length] + (i > GIVEN_NAMES.length ? "" : "");
            String email = String.format("demo_member_%03d@example.com", i);
            String phone = String.format("010-%04d-%04d", 2000 + (i % 7000), 1000 + (i % 8000));
            MemberStatus status = switch (i % 20) {
                case 0 -> MemberStatus.BANNED;
                case 1, 2 -> MemberStatus.BLOCKED;
                case 3 -> MemberStatus.INACTIVE;
                case 4 -> MemberStatus.WITHDRAWN;
                default -> MemberStatus.ACTIVE;
            };
            Member member = Member.builder()
                    .loginId(loginId)
                    .password(encodedPassword)
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .status(status)
                    .createdAt(LocalDate.now().minusDays(i).atTime(10, (i * 7) % 60))
                    .build();
            member.acceptLegalAgreements(LocalDate.now().minusDays(i).atTime(10, (i * 7) % 60));
            additions.add(member);
        }
        memberRepository.saveAll(additions);
        memberRepository.flush();
    }

    private void seedNotices(Admin admin) {
        long current = noticeRepository.count();
        if (current >= STANDARD_TARGET) return;
        List<Notice> additions = new ArrayList<>();
        for (int i = 1; current + additions.size() < STANDARD_TARGET; i++) {
            String topic = NOTICE_TOPICS[(i - 1) % NOTICE_TOPICS.length];
            String title = String.format("[%s] %03d", topic, i);
            String content = topic + "에 대해 안내드립니다.\n\n" +
                    "이용객의 안전하고 편리한 서비스 이용을 위해 관련 내용을 확인해 주세요. " +
                    "시설 상태는 현장 상황에 따라 변경될 수 있으므로 이용 전 최신 안내를 확인하시기 바랍니다.\n\n" +
                    "문의 사항은 고객센터 또는 Q&A 메뉴를 통해 접수해 주세요.\n\n" +
                    "본 게시물은 프로젝트 시연을 위한 예시 데이터입니다.";
            additions.add(Notice.create(title, content, admin.getName()));
        }
        noticeRepository.saveAll(additions);
    }

    private void seedResources(Admin admin) {
        long current = resourcePostRepository.count();
        if (current >= STANDARD_TARGET) return;
        List<ResourcePost> additions = new ArrayList<>();
        for (int i = 1; current + additions.size() < STANDARD_TARGET; i++) {
            String topic = RESOURCE_TOPICS[(i - 1) % RESOURCE_TOPICS.length];
            String title = String.format("%s (%03d)", topic, i);
            String content = topic + "에 관한 자료입니다.\n\n" +
                    "1. 목적: 7호선 교통서비스 정보를 일관되게 안내합니다.\n" +
                    "2. 확인 사항: 시설명, 설치 위치, 운영 상태 및 이용 시 주의사항을 확인합니다.\n" +
                    "3. 이용 안내: 시설 상태가 점검 또는 고장으로 표시되면 현장 안내를 우선 확인해 주세요.\n" +
                    "4. 문의: 추가 자료가 필요한 경우 자료 요청 또는 Q&A 메뉴를 이용해 주세요.\n\n" +
                    "※ 프로젝트 시연을 위한 예시 자료이며 실제 운영 공지나 공식 지침이 아닙니다.";
            additions.add(ResourcePost.create(title, content, admin));
        }
        resourcePostRepository.saveAll(additions);
    }

    private void seedQna(List<Member> members) {
        long current = qnaRepository.count();
        if (current >= QNA_TARGET) return;
        List<Qna> additions = new ArrayList<>();
        for (int i = 1; current + additions.size() < QNA_TARGET; i++) {
            Member member = members.get((i - 1) % members.size());
            String topic = QNA_TOPICS[(i - 1) % QNA_TOPICS.length];
            String title = String.format("%s (%03d)", topic, i);
            String content = "안녕하세요. " + topic + " 관련하여 문의드립니다.\n" +
                    "이용 가능한 위치와 확인 방법을 안내해 주시면 감사하겠습니다.\n" +
                    "프로젝트 시연용으로 등록된 문의입니다.";
            Qna qna = Qna.create(title, content, InquiryType.values()[(i - 1) % InquiryType.values().length],
                    member.getId(), member.getName(), i % 5 != 0);
            if (i % 4 != 0) {
                qna.setAnswer("문의해 주셔서 감사합니다. 해당 내용은 담당자가 확인할 수 있도록 안내하겠습니다. " +
                        "시설별 최신 상태와 현장 안내를 함께 확인해 주세요. (시연용 답변)");
            }
            additions.add(qna);
        }
        qnaRepository.saveAll(additions);
    }

    private void seedResourceRequests(List<Member> members) {
        long current = resourceRequestRepository.count();
        if (current >= STANDARD_TARGET) return;
        List<ResourceRequest> additions = new ArrayList<>();
        for (int i = 1; current + additions.size() < STANDARD_TARGET; i++) {
            Member member = members.get((i - 1) % members.size());
            String title = String.format("자료 추가 요청 - %03d", i);
            String content = "7호선 역사 시설 이용과 관련된 안내 자료를 추가해 주세요. " +
                    "이용 방법, 주의사항 및 관련 편의시설 정보를 확인할 수 있으면 좋겠습니다. (시연용 요청)";
            ResourceRequest request = ResourceRequest.create(member, title, content);
            if (i % 3 != 0) request.changeStatus(RequestStatus.COMPLETED);
            additions.add(request);
        }
        resourceRequestRepository.saveAll(additions);
    }

    private void seedTransitServices() {
        long current = transitServiceRepository.count();
        if (current >= STANDARD_TARGET) return;
        List<TransitService> additions = new ArrayList<>();
        for (int i = 1; current + additions.size() < STANDARD_TARGET; i++) {
            String station = STATION_NAMES[(i - 1) % STATION_NAMES.length];
            String facility = FACILITIES[(i - 1) % FACILITIES.length];
            OperationStatus status = switch (i % 20) {
                case 0, 1 -> OperationStatus.FAULT;
                case 2, 3, 4, 5 -> OperationStatus.MAINTENANCE;
                default -> OperationStatus.NORMAL;
            };
            additions.add(TransitService.builder()
                    .name(String.format("%s역 %s %02d호", station, facility, ((i - 1) / STATION_NAMES.length) + 1))
                    .status(status)
                    .build());
        }
        transitServiceRepository.saveAll(additions);
    }

    private void seedTrains() {
        long current = trainRepository.count();
        if (current >= STANDARD_TARGET) return;
        List<Train> additions = new ArrayList<>();
        for (int i = 1; current + additions.size() < STANDARD_TARGET; i++) {
            OperationStatus status = switch (i % 20) {
                case 0 -> OperationStatus.FAULT;
                case 1, 2, 3 -> OperationStatus.MAINTENANCE;
                default -> OperationStatus.NORMAL;
            };
            additions.add(Train.builder()
                    .trainNo(String.format("7L-%04d", 1000 + i))
                    .route("석남 ↔ 까치울")
                    .driver(String.format("기관사%03d", i))
                    .direction(i % 2 == 0 ? TrainDirection.UP : TrainDirection.DOWN)
                    .status(status)
                    .build());
        }
        trainRepository.saveAll(additions);
    }

    private void seedDailyStats() {
        long current = dailyStatRepository.count();
        if (current >= DAILY_STAT_TARGET) return;
        List<DailyStat> additions = new ArrayList<>();
        for (int offset = 0; current + additions.size() < DAILY_STAT_TARGET && offset < 1000; offset++) {
            LocalDate date = LocalDate.now().minusDays(offset);
            if (dailyStatRepository.findByStatDate(date).isPresent()) continue;

            long memberCount = Math.max(memberRepository.count() - (offset % 7), 0);
            long noticeCount = Math.max(noticeRepository.count() - (offset % 5), 0);
            long pendingQna = Math.max(qnaRepository.countByAnswered(false) - (offset % 4), 0);
            long serviceTotal = transitServiceRepository.count();
            long fault = transitServiceRepository.countByStatus(OperationStatus.FAULT);
            long maintenance = transitServiceRepository.countByStatus(OperationStatus.MAINTENANCE);
            long normal = Math.max(serviceTotal - fault - maintenance, 0);

            additions.add(DailyStat.builder()
                    .statDate(date)
                    .memberCount(memberCount)
                    .noticeCount(noticeCount)
                    .qnaPendingCount(pendingQna)
                    .serviceTotal(serviceTotal)
                    .normalCount(normal)
                    .maintenanceCount(maintenance)
                    .faultCount(fault)
                    .build());
        }
        dailyStatRepository.saveAll(additions);
    }
}
