package com._404._404.service;

import com._404._404.domain.Member;
import com._404._404.domain.MemberStatus;
import com._404._404.dto.AdminMemberDto.MemberDetail;
import com._404._404.dto.AdminMemberDto.MemberListResponse;
import com._404._404.dto.AdminMemberDto.MemberRow;
import com._404._404.dto.AdminMemberDto.MemberSummary;
import com._404._404.dto.AdminMemberDto.MemberUpdateRequest;
import com._404._404.dto.AdminMemberDto.PasswordResetResult;
import com._404._404.dto.PageInfo;
import com._404._404.repository.MemberRepository;
import com._404._404.util.PasswordHasher;
import com._404._404.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

	private static final int PAGE_SIZE = 10;
	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final Pattern PHONE = Pattern.compile("^[0-9\\-]{9,13}$");
	private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
	private static final int TEMP_PASSWORD_LENGTH = 10;

	private final SecureRandom random = new SecureRandom();
	private final MemberRepository memberRepository;
	private final PasswordHasher passwordHasher;

	public MemberListResponse list(String type, String keyword, int page) {
		String kw = keyword == null ? "" : keyword.trim();
		String searchType = type == null ? "ALL" : type.trim().toUpperCase();
		Pageable pageable = PageRequest.of(Math.max(page, 1) - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

		Page<Member> result;
		if (kw.isEmpty()) {
			result = memberRepository.findAll(pageable);
		} else {
			result = switch (searchType) {
				case "NAME" -> memberRepository.findByNameContainingIgnoreCase(kw, pageable);
				case "LOGIN" -> memberRepository.findByLoginIdContainingIgnoreCase(kw, pageable);
				case "EMAIL" -> memberRepository.findByEmailContainingIgnoreCase(kw, pageable);
				default -> memberRepository
						.findByNameContainingIgnoreCaseOrLoginIdContainingIgnoreCaseOrEmailContainingIgnoreCase(
								kw, kw, kw, pageable);
			};
		}

		List<MemberRow> rows = result.getContent().stream().map(this::toRow).toList();
		MemberSummary summary = new MemberSummary(
				memberRepository.count(),
				memberRepository.countByStatus(MemberStatus.ACTIVE),
				memberRepository.countByStatus(MemberStatus.BLOCKED),
				memberRepository.countByStatus(MemberStatus.INACTIVE),
				memberRepository.countByStatus(MemberStatus.BANNED));
		return new MemberListResponse(summary, rows, PageInfo.from(result), searchType, kw);
	}

	public MemberDetail detail(Long id) {
		return toDetail(find(id));
	}

	@Transactional
	public MemberDetail update(Long id, MemberUpdateRequest request) {
		Member member = find(id);
		String name = Validators.requireText(request.name(), "이름", 50);
		String email = Validators.requireText(request.email(), "이메일", 100);
		if (!EMAIL.matcher(email).matches()) {
			throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
		}
		String phone = request.phone() == null ? "" : request.phone().trim();
		if (!phone.isEmpty() && !PHONE.matcher(phone).matches()) {
			throw new IllegalArgumentException("연락처는 숫자와 하이픈(-)으로 9~13자 입력해 주세요.");
		}
		member.updateProfile(name, email, phone.isEmpty() ? null : phone);
		memberRepository.saveAndFlush(member);
		return toDetail(member);
	}

	@Transactional
	public MemberDetail changeStatus(Long id, MemberStatus status) {
		Validators.requireNotNull(status, "회원 상태");
		Member member = find(id);
		rejectAdminTarget(member, "관리자 계정의 상태는 변경할 수 없습니다.");
		member.changeStatus(status);
		memberRepository.saveAndFlush(member);
		return toDetail(member);
	}

	@Transactional
	public PasswordResetResult resetPassword(Long id) {
		Member member = find(id);
		rejectAdminTarget(member, "관리자 계정의 비밀번호는 초기화할 수 없습니다.");
		String temporary = generateTemporaryPassword();
		member.changePassword(passwordHasher.encode(temporary));
		memberRepository.saveAndFlush(member);
		return new PasswordResetResult(temporary);
	}

	private Member find(Long id) {
		return memberRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));
	}

	private void rejectAdminTarget(Member member, String message) {
		if (Member.ROLE_ADMIN.equals(member.getRole())) {
			throw new IllegalArgumentException(message);
		}
	}

	private String generateTemporaryPassword() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
			sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
		}
		return sb.toString();
	}

	private MemberRow toRow(Member m) {
		return new MemberRow(m.getId(), memberNo(m), m.getName(), m.getLoginId(), m.getEmail(),
				m.getStatus().name(), m.getStatus().getLabel());
	}

	private MemberDetail toDetail(Member m) {
		String grade = Member.ROLE_ADMIN.equals(m.getRole()) ? "관리자" : "일반회원";
		return new MemberDetail(m.getId(), memberNo(m), m.getName(), m.getLoginId(), m.getEmail(),
				m.getPhone() == null ? "" : m.getPhone(),
				m.getStatus().name(), m.getStatus().getLabel(), m.getRole(), grade,
				format(m.getCreatedAt()), format(m.getUpdatedAt()));
	}

	private String memberNo(Member m) {
		return String.format("%06d", m.getId());
	}

	private String format(LocalDateTime time) {
		return time == null ? "-" : time.format(DATE_TIME);
	}
}
