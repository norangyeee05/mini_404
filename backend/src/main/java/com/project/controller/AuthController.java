package com.project.controller;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.common.ApiResponse;
import com.project.common.BusinessException;
import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.config.CsrfTokenService;
import com.project.dto.MemberDto.LoginRequest;
import com.project.dto.MemberDto.LoginResponse;
import com.project.dto.MemberDto.SignupRequest;
import com.project.dto.MemberDto.FindIdRequest;
import com.project.dto.MemberDto.FindIdResponse;
import com.project.service.AuthService;
import com.project.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final CsrfTokenService csrfTokenService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Map<String, Long>>> signup(@Valid @RequestBody SignupRequest request) {
        Long memberId = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("memberId", memberId), "회원가입이 완료되었습니다."));
    }

    @GetMapping("/check-id")
    public ApiResponse<Map<String, Boolean>> checkId(@RequestParam String loginId) {
        if (loginId.isBlank()) {
            throw BusinessException.badRequest("아이디를 입력해주세요.");
        }
        return ApiResponse.ok(Map.of("available", authService.isAvailable(loginId.trim())));
    }

    @GetMapping("/me")
    public ApiResponse<SessionResponse> me(
            @org.springframework.web.bind.annotation.SessionAttribute(name = SessionKeys.LOGIN_USER, required = false) LoginUser user,
            HttpServletRequest request, HttpServletResponse response) {
        if (user == null) return ApiResponse.ok(new SessionResponse(false, "GUEST", null, null, false));
        HttpSession session = request.getSession(false);
        csrfTokenService.writeCookie(response, csrfTokenService.ensureToken(session));
        return ApiResponse.ok(new SessionResponse(true, user.role().name(), user.id(), user.name(), user.adminAccount()));
    }

    @PostMapping("/find-id")
    public ApiResponse<FindIdResponse> findId(@Valid @RequestBody FindIdRequest request) {
        String masked = memberService.findMaskedLoginId(request.name(), request.email()).orElse(null);
        return ApiResponse.ok(new FindIdResponse(masked));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest,
                                            HttpServletResponse httpResponse) {
        LoginUser user = authService.login(request);


        HttpSession old = httpRequest.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SessionKeys.LOGIN_USER, user);
        csrfTokenService.writeCookie(httpResponse, csrfTokenService.ensureToken(session));

        return ApiResponse.ok(new LoginResponse(user.id(), user.name(), user.role().name(), user.adminAccount()), "로그인되었습니다.");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        csrfTokenService.clearCookie(httpResponse);
        return ApiResponse.ok();
    }

    public record SessionResponse(boolean authenticated, String role, Long memberId, String name, boolean adminAccount) { }
}
