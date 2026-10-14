package com.project.dto;

import java.time.LocalDateTime;

import com.project.domain.MemberStatus;

import jakarta.validation.constraints.NotNull;

/** 관리자 회원관리 DTO 모음. 비밀번호(해시 포함)는 어떤 응답에도 포함하지 않음 */
public final class AdminMemberDto {

    private AdminMemberDto() {
    }

    /** 회원 목록 항목 */
    public record ListItem(
            Long memberId,
            String name,
            String loginId,
            String email,
            MemberStatus status,
            LocalDateTime updatedAt
    ) {
    }

    /** 회원 상세 */
    public record Detail(
            Long memberId,
            String name,
            String loginId,
            String email,
            String phone,
            MemberStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    /** 회원 상태 변경 요청 */
    public record StatusUpdate(@NotNull(message = "회원 상태를 선택해주세요.") MemberStatus status) {
    }
}
