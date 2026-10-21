package com.project.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.BusinessException;
import com.project.domain.Member;
import com.project.dto.MemberDto.MeResponse;
import com.project.dto.MemberDto.UpdateRequest;
import com.project.dto.MemberDto.WithdrawRequest;
import com.project.repository.MemberRepository;

/** 회원 본인의 정보 관리와 계정 찾기 기능 */
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

    @Transactional
    public Member update(Long memberId, UpdateRequest req) {
        Member m = find(memberId);
        String encoded = null;
        if (req.password() != null && !req.password().isBlank()) {
            if (req.password().length() < 8) throw BusinessException.badRequest("비밀번호는 8~64자로 입력해주세요.");
            encoded = passwordEncoder.encode(req.password());
        }
        m.updateProfile(req.name().trim(), req.email().trim(), encoded);
        if (req.phone() != null) {
            String phone = req.phone().trim();
            m.changePhone(phone.isEmpty() ? null : phone);
        }
        return m;
    }

    @Transactional
    public void withdraw(Long memberId, WithdrawRequest req) {
        Member m = find(memberId);
        if (!m.getLoginId().equals(req.loginId())) throw BusinessException.badRequest("로그인한 계정의 아이디와 일치하지 않습니다.");
        if (!req.password().equals(req.passwordConfirm())) throw BusinessException.badRequest("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        if (!passwordEncoder.matches(req.password(), m.getPassword())) throw BusinessException.badRequest("비밀번호가 올바르지 않습니다.");
        m.withdraw();
    }

    public Optional<String> findMaskedLoginId(String name, String email) {
        return memberRepository.findFirstByNameAndEmail(name.trim(), email.trim()).map(m -> mask(m.getLoginId()));
    }

    public boolean verifyIdentity(String loginId, String name, String email) {
        return memberRepository.findByLoginIdAndNameAndEmail(loginId.trim(), name.trim(), email.trim()).isPresent();
    }

    @Transactional
    public void resetPassword(String loginId, String newPassword) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> BusinessException.notFound("회원을 찾을 수 없습니다."));
        member.changePassword(passwordEncoder.encode(newPassword));
    }

    private Member find(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> BusinessException.notFound("회원 정보를 찾을 수 없습니다."));
    }

    private String mask(String id) {
        int visible = Math.min(2, id.length());
        return id.substring(0, visible) + "*".repeat(id.length() - visible);
    }
}
