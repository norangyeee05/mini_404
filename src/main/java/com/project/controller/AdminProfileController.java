package com.project.controller;

import java.util.Map;
import com.project.common.ApiResponse;
import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.domain.Admin;
import com.project.dto.MemberDto.UpdateRequest;
import com.project.repository.AdminRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/me")
public class AdminProfileController {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ApiResponse<AdminMe> me(@SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user) {
        Admin admin = find(user.id());
        return ApiResponse.ok(new AdminMe(admin.getLoginId(), admin.getName(), admin.getEmail(), admin.getPhone(), "ADMIN"));
    }

    @PutMapping
    public ApiResponse<Map<String, Long>> update(@SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
            @Valid @RequestBody UpdateRequest request, HttpServletRequest httpRequest) {
        Admin admin = find(user.id());
        admin.updateProfile(request.name().trim(), request.email().trim(), request.phone(),
                request.password() == null || request.password().isBlank() ? null : passwordEncoder.encode(request.password()));
        HttpSession session = httpRequest.getSession(false);
        if (session != null) session.setAttribute(SessionKeys.LOGIN_USER, new LoginUser(admin.getId(), admin.getName(), com.project.domain.Role.ADMIN, true));
        return ApiResponse.ok(Map.of("memberId", admin.getId()), "관리자 정보가 저장되었습니다.");
    }

    private Admin find(Long id) {
        return adminRepository.findById(id).orElseThrow(() -> com.project.common.BusinessException.notFound("관리자 정보를 찾을 수 없습니다."));
    }

    public record AdminMe(String loginId, String name, String email, String phone, String role) { }
}
