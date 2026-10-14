package com.project.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 일반 회원 관련 요청/응답 DTO 모음 (회원가입·로그인·내 정보·탈퇴) */
public final class MemberDto {

    private MemberDto() {
    }

    /** 회원가입 요청 */
    public record SignupRequest(
            @NotBlank(message = "아이디를 입력해주세요.")
            @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "아이디는 영문, 숫자, 밑줄(_) 4~20자로 입력해주세요.")
            String loginId,

            @NotBlank(message = "비밀번호를 입력해주세요.")
            @Size(min = 8, max = 64, message = "비밀번호는 8~64자로 입력해주세요.")
            String password,

            @NotBlank(message = "이름을 입력해주세요.")
            @Size(max = 50, message = "이름은 50자 이내로 입력해주세요.")
            String name,

            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            @Size(max = 100, message = "이메일은 100자 이내로 입력해주세요.")
            String email,

            @Size(max = 20, message = "전화번호는 20자 이내로 입력해주세요.")
            String phone,

            @AssertTrue(message = "이용약관 및 개인정보 처리방침에 동의해주세요.")
            boolean agreeTerms
    ) {
    }

    /** 로그인 요청 */
    public record LoginRequest(
            @NotBlank(message = "아이디를 입력해주세요.") String loginId,
            @NotBlank(message = "비밀번호를 입력해주세요.") String password
    ) {
    }

    /** 로그인 응답 */
    public record LoginResponse(Long memberId, String name, String role) {
    }

    /** 내 정보 조회 응답 */
    public record MeResponse(String loginId, String name, String email, String phone) {
    }

    /** 내 정보 수정 요청 (아이디 변경 불가). password는 선택 - 비어 있으면 기존 비밀번호 유지 */
    public record UpdateRequest(
            @NotBlank(message = "이름을 입력해주세요.")
            @Size(max = 50, message = "이름은 50자 이내로 입력해주세요.")
            String name,

            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            @Size(max = 100, message = "이메일은 100자 이내로 입력해주세요.")
            String email,

            @Size(max = 64, message = "비밀번호는 8~64자로 입력해주세요.")
            String password
    ) {
    }

    /** 회원 탈퇴 요청 */
    public record WithdrawRequest(
            @NotBlank(message = "아이디를 입력해주세요.") String loginId,
            @NotBlank(message = "비밀번호를 입력해주세요.") String password,
            @NotBlank(message = "비밀번호 확인을 입력해주세요.") String passwordConfirm
    ) {
    }
}
