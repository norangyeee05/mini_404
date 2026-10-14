package com.project.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.common.ApiResponse;
import com.project.common.PageResponse;
import com.project.domain.Member;
import com.project.domain.MemberStatus;
import com.project.dto.AdminMemberDto.Detail;
import com.project.dto.AdminMemberDto.ListItem;
import com.project.dto.AdminMemberDto.StatusUpdate;
import com.project.service.AdminMemberService;

import jakarta.validation.Valid;

/** 관리자 회원관리 (2-6, 대시보드 제외). 접근 제어는 AuthInterceptor(/api/admin/**)에서 처리 */
@RestController
@RequestMapping("/api/admin/members")
public class AdminMemberController {

    private final AdminMemberService memberService;

    public AdminMemberController(AdminMemberService memberService) {
        this.memberService = memberService;
    }

    /** 공통 쿼리 + status 필터(선택), searchType: name(기본) / loginId / email */
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
}
