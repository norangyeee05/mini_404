package com.project.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public final class MemberDto {

    private MemberDto() {
    }


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
            @Pattern(regexp = "(?i)^[^\\s@]+@[^\\s@]+\\.com$", message = "이메일은 example@domain.com 형식으로 입력해주세요.")
            @Size(max = 100, message = "이메일은 100자 이내로 입력해주세요.")
            String email,

            @Size(max = 20, message = "전화번호는 20자 이내로 입력해주세요.")
            String phone,

            @AssertTrue(message = "이용약관에 동의해주세요.")
            boolean agreeTerms,

            @AssertTrue(message = "개인정보 처리방침에 동의해주세요.")
            boolean agreePrivacy
    ) {
    }


    public record LoginRequest(
            @NotBlank(message = "아이디를 입력해주세요.") String loginId,
            @NotBlank(message = "비밀번호를 입력해주세요.") String password
    ) {
    }

    public record FindIdRequest(@NotBlank String name, @NotBlank @Email String email) { }
    public record FindIdResponse(String maskedLoginId) { }


    public record LoginResponse(Long memberId, String name, String role, boolean adminAccount) {
    }


    public record MeResponse(String loginId, String name, String email, String phone) {
    }


    public record UpdateRequest(
            @NotBlank(message = "이름을 입력해주세요.")
            @Size(max = 50, message = "이름은 50자 이내로 입력해주세요.")
            String name,

            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            @Pattern(regexp = "(?i)^[^\\s@]+@[^\\s@]+\\.com$", message = "이메일은 example@domain.com 형식으로 입력해주세요.")
            @Size(max = 100, message = "이메일은 100자 이내로 입력해주세요.")
            String email,

            @Size(max = 20, message = "전화번호는 20자 이내로 입력해주세요.")
            String phone,

            @Size(max = 64, message = "비밀번호는 8~64자로 입력해주세요.")
            String password
    ) {
    }


    public record WithdrawRequest(
            @NotBlank(message = "아이디를 입력해주세요.") String loginId,
            @NotBlank(message = "비밀번호를 입력해주세요.") String password,
            @NotBlank(message = "비밀번호 확인을 입력해주세요.") String passwordConfirm
    ) {
    }
}
