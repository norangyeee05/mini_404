package com._404._404.service;

import com._404._404.domain.Member;
import com._404._404.domain.MemberStatus;
import com._404._404.dto.LoginMember;
import com._404._404.dto.SignupForm;
import com._404._404.repository.MemberRepository;
import com._404._404.util.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordHasher passwordHasher;

	@Transactional
	public void signup(SignupForm form) {
		String loginId = form.getLoginId().trim();
		if (memberRepository.existsByLoginId(loginId)) {
			throw new IllegalStateException("이미 사용 중인 아이디입니다.");
		}
		memberRepository.save(Member.builder()
				.loginId(loginId)
				.passwordHash(passwordHasher.encode(form.getPassword()))
				.name(form.getName().trim())
				.email(form.getEmail().trim())
				.build());
	}

	/** 로그인 검증. 성공하면 세션에 담을 LoginMember 반환 */
	@Transactional(readOnly = true)
	public Optional<LoginMember> authenticate(String loginId, String rawPassword) {
		Optional<Member> found = memberRepository.findByLoginId(loginId)
				.filter(m -> passwordHasher.matches(rawPassword, m.getPasswordHash()));
		if (found.isEmpty()) {
			return Optional.empty();
		}
		Member m = found.get();
		if (m.getStatus() != MemberStatus.ACTIVE) {
			throw new AccountRestrictedException(restrictedMessage(m.getStatus()));
		}
		return Optional.of(new LoginMember(m.getId(), m.getLoginId(), m.getName(), m.getRole()));
	}

	/** 이름 + 이메일로 아이디 조회. 앞 2글자만 보여주고 나머지는 *로 가린다 */
	@Transactional(readOnly = true)
	public Optional<String> findMaskedLoginId(String name, String email) {
		return memberRepository.findFirstByNameAndEmail(name.trim(), email.trim())
				.map(m -> mask(m.getLoginId()));
	}

	@Transactional(readOnly = true)
	public boolean verifyIdentity(String loginId, String name, String email) {
		return memberRepository
				.findByLoginIdAndNameAndEmail(loginId.trim(), name.trim(), email.trim())
				.isPresent();
	}

	@Transactional
	public void resetPassword(String loginId, String newPassword) {
		Member member = memberRepository.findByLoginId(loginId)
				.orElseThrow(() -> new IllegalStateException("회원을 찾을 수 없습니다."));
		member.changePassword(passwordHasher.encode(newPassword));
	}

	private String restrictedMessage(MemberStatus status) {
		return switch (status) {
			case BLOCKED -> "임시 차단된 계정입니다. 관리자에게 문의해 주세요.";
			case INACTIVE -> "비활성화된 계정입니다. 관리자에게 문의해 주세요.";
			case BANNED -> "이용이 제한된 계정입니다. 관리자에게 문의해 주세요.";
			default -> "로그인할 수 없는 계정입니다.";
		};
	}

	private String mask(String id) {
		int visible = Math.min(2, id.length());
		return id.substring(0, visible) + "*".repeat(id.length() - visible);
	}
}
