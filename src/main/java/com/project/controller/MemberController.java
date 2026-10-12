package com.project.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.project.common.ApiResponse;
import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.domain.Member;
import com.project.domain.Role;
import com.project.dto.MemberDto.MeResponse;
import com.project.dto.MemberDto.UpdateRequest;
import com.project.dto.MemberDto.WithdrawRequest;
import com.project.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/** 내 정보 (2-1): 조회 / 수정 / 탈퇴 - 일반 회원 전용 */
@RestController
@RequestMapping("/api/members/me")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ApiResponse<MeResponse> me(@SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user) {
        user.requireUser();
        return ApiResponse.ok(memberService.me(user.id()));
    }

    @PutMapping
    public ApiResponse<Map<String, Long>> update(@SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
                                                 @Valid @RequestBody UpdateRequest request,
                                                 HttpServletRequest httpRequest) {
        user.requireUser();
        Member updated = memberService.update(user.id(), request);

        // 이름이 바뀌었을 수 있으므로 세션 정보 갱신
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.setAttribute(SessionKeys.LOGIN_USER, new LoginUser(updated.getId(), updated.getName(), Role.USER));
        }
        return ApiResponse.ok(Map.of("memberId", updated.getId()), "회원 정보가 수정되었습니다.");
    }

    @DeleteMapping
    public ApiResponse<Void> withdraw(@SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
                                      @Valid @RequestBody WithdrawRequest request,
                                      HttpServletRequest httpRequest) {
        user.requireUser();
        memberService.withdraw(user.id(), request);

        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.ok(null, "회원 탈퇴가 완료되었습니다.");
    }
}
