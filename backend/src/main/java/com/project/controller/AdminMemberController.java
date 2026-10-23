package com.project.controller;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.common.ApiResponse;
import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.domain.Role;
import com.project.common.PageResponse;
import com.project.domain.Member;
import com.project.domain.MemberStatus;
import com.project.dto.AdminMemberDto.Detail;
import com.project.dto.AdminMemberDto.ListItem;
import com.project.dto.AdminMemberDto.StatusUpdate;
import com.project.dto.AdminMemberDto.RoleUpdate;
import com.project.service.AdminMemberService;

import jakarta.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
public class AdminMemberController {

    private final AdminMemberService memberService;


    @GetMapping
    public ApiResponse<PageResponse<ListItem>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MemberStatus status) {
        return ApiResponse.ok(memberService.list(searchType, keyword, status, page, size));
    }

    @GetMapping("/{memberId}")
    public ApiResponse<Detail> detail(@PathVariable Long memberId) {
        return ApiResponse.ok(memberService.detail(memberId));
    }

    @PatchMapping("/{memberId}/status")
    public ApiResponse<Map<String, Object>> changeStatus(@PathVariable Long memberId,
                                                         @Valid @RequestBody StatusUpdate body) {
        Member m = memberService.changeStatus(memberId, body.status());
        return ApiResponse.ok(Map.of("memberId", m.getId(), "status", m.getStatus()), "회원 상태가 변경되었습니다.");
    }

    @PatchMapping("/{memberId}/role")
    public ApiResponse<Map<String, Object>> changeRole(@PathVariable Long memberId,
                                                       @Valid @RequestBody RoleUpdate body,
                                                       @org.springframework.web.bind.annotation.SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
                                                       jakarta.servlet.http.HttpServletRequest request) {
        if (user.role() != Role.ADMIN) throw com.project.common.BusinessException.forbidden("관리자만 회원 등급을 변경할 수 있습니다.");
        Member m = memberService.changeRole(memberId, body.role());
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null && !user.adminAccount() && user.id().equals(memberId)) {
            session.setAttribute(SessionKeys.LOGIN_USER, new LoginUser(m.getId(), m.getName(), m.getRole()));
        }
        return ApiResponse.ok(Map.of("memberId", m.getId(), "role", m.getRole().name()), "회원 등급이 변경되었습니다.");
    }

    @org.springframework.web.bind.annotation.PostMapping("/{memberId}/password-reset")
    public ApiResponse<Map<String, Object>> resetPassword(@PathVariable Long memberId) {
        return ApiResponse.ok(Map.of("memberId", memberId, "temporaryPassword", memberService.resetPassword(memberId)),
                "임시 비밀번호가 발급되었습니다.");
    }
}
