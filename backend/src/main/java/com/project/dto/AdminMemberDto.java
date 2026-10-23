package com.project.dto;

import java.time.LocalDateTime;

import com.project.domain.MemberStatus;

import jakarta.validation.constraints.NotNull;


public final class AdminMemberDto {

    private AdminMemberDto() {
    }


    public record ListItem(
            Long memberId,
            String name,
            String loginId,
            String email,
            String role,
            MemberStatus status,
            LocalDateTime updatedAt
    ) {
    }


    public record Detail(
            Long memberId,
            String name,
            String loginId,
            String email,
            String phone,
            String role,
            MemberStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }


    public record StatusUpdate(@NotNull(message = "회원 상태를 선택해주세요.") MemberStatus status) {
    }

    public record RoleUpdate(
            @jakarta.validation.constraints.NotBlank(message = "회원 등급을 선택해주세요.")
            @jakarta.validation.constraints.Pattern(regexp = "USER|WEB_ADMIN|ADMIN", message = "지원하지 않는 회원 등급입니다.")
            String role) { }
}
