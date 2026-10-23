package com.project.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import com.project.common.BusinessException;
import com.project.common.LoginUser;
import com.project.domain.Admin;
import com.project.domain.AdminRole;
import com.project.domain.Member;
import com.project.domain.MemberStatus;
import com.project.domain.Role;
import com.project.dto.MemberDto.LoginRequest;
import com.project.dto.MemberDto.SignupRequest;
import com.project.repository.AdminRepository;
import com.project.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final String LOGIN_FAIL = "아이디 또는 비밀번호가 올바르지 않습니다.";

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(MemberRepository memberRepository, AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long signup(SignupRequest req) {
        if (!isAvailable(req.loginId())) {
            throw BusinessException.conflict("이미 사용 중인 아이디입니다.");
        }
        String phone = req.phone() == null || req.phone().isBlank() ? null : req.phone().trim();
        Member member = Member.create(
                req.loginId(),
                passwordEncoder.encode(req.password()),
                req.name().trim(),
                req.email().trim(),
                phone);
        member.acceptLegalAgreements(LocalDateTime.now());
        return memberRepository.save(member).getId();
    }

    public boolean isAvailable(String loginId) {
        return !memberRepository.existsByLoginId(loginId) && !adminRepository.existsByLoginId(loginId);
    }


    public LoginUser login(LoginRequest req) {
        Member member = memberRepository.findByLoginId(req.loginId()).orElse(null);
        if (member != null) {
            if (!passwordEncoder.matches(req.password(), member.getPassword())) {
                throw BusinessException.unauthorized(LOGIN_FAIL);
            }
            ensureAccountCanLogin(member.getStatus());
            return new LoginUser(member.getId(), member.getName(), member.getRole());
        }

        Admin admin = adminRepository.findByLoginId(req.loginId()).orElse(null);
        if (admin == null || !passwordEncoder.matches(req.password(), admin.getPassword())) {
            throw BusinessException.unauthorized(LOGIN_FAIL);
        }
        ensureAccountCanLogin(admin.getStatus());
        Role role = admin.getRole() == AdminRole.MANAGER ? Role.WEB_ADMIN : Role.ADMIN;
        return new LoginUser(admin.getId(), admin.getName(), role, true);
    }

    private void ensureAccountCanLogin(MemberStatus status) {
        switch (status) {
            case ACTIVE -> { }
            case BANNED -> throw BusinessException.forbidden("강퇴된 계정입니다. 관리자에게 확인해 주세요.");
            case INACTIVE -> throw BusinessException.forbidden("비활성화된 계정입니다. 관리자에게 문의해 주세요.");
            case BLOCKED -> throw BusinessException.forbidden("임시 차단된 계정입니다. 관리자에게 문의해 주세요.");
            case WITHDRAWN -> throw BusinessException.forbidden("탈퇴한 계정은 로그인할 수 없습니다.");
        }
    }
}
