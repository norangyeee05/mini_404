package com.project.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.BusinessException;
import com.project.domain.Member;
import com.project.dto.MemberDto.MeResponse;
import com.project.dto.MemberDto.UpdateRequest;
import com.project.dto.MemberDto.WithdrawRequest;
import com.project.repository.MemberRepository;

/** 로그인한 일반 회원 본인의 정보 조회·수정·탈퇴 */
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MeResponse me(Long memberId) {
        Member m = find(memberId);
        return new MeResponse(m.getLoginId(), m.getName(), m.getEmail(), m.getPhone());
    }

    /** @return 수정된 회원의 이름 (세션 갱신용) */
    @Transactional
    public Member update(Long memberId, UpdateRequest req) {
        Member m = find(memberId);

        String encoded = null;
        if (req.password() != null && !req.password().isBlank()) {
            if (req.password().length() < 8) {
                throw BusinessException.badRequest("비밀번호는 8~64자로 입력해주세요.");
            }
            encoded = passwordEncoder.encode(req.password());
        }
        m.updateProfile(req.name().trim(), req.email().trim(), encoded);
        return m;
    }

    @Transactional
    public void withdraw(Long memberId, WithdrawRequest req) {
        Member m = find(memberId);

        if (!m.getLoginId().equals(req.loginId())) {
            throw BusinessException.badRequest("로그인한 계정의 아이디와 일치하지 않습니다.");
        }
        if (!req.password().equals(req.passwordConfirm())) {
            throw BusinessException.badRequest("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        if (!passwordEncoder.matches(req.password(), m.getPassword())) {
            throw BusinessException.badRequest("비밀번호가 올바르지 않습니다.");
        }
        m.withdraw();
    }

    private Member find(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> BusinessException.notFound("회원 정보를 찾을 수 없습니다."));
    }
}
