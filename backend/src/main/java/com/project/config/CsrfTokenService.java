package com.project.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.project.common.BusinessException;
import com.project.common.SessionKeys;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CsrfTokenService {
    public static final String COOKIE_NAME = "XSRF-TOKEN";
    public static final String HEADER_NAME = "X-XSRF-TOKEN";

    private static final SecureRandom RANDOM = new SecureRandom();
    private final boolean secureCookie;
    private final String sameSite;

    public CsrfTokenService(
            @Value("${app.security.session-cookie-secure:false}") boolean secureCookie,
            @Value("${app.security.session-cookie-same-site:Lax}") String sameSite) {
        this.secureCookie = secureCookie;
        this.sameSite = sameSite;
    }

    public String ensureToken(HttpSession session) {
        String token = (String) session.getAttribute(SessionKeys.CSRF_TOKEN);
        if (token == null || token.isBlank()) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(SessionKeys.CSRF_TOKEN, token);
        }
        return token;
    }

    public void writeCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(false)
                .secure(secureCookie)
                .path("/")
                .sameSite(sameSite)
                .maxAge(Duration.ofHours(8))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(false)
                .secure(secureCookie)
                .path("/")
                .sameSite(sameSite)
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void validate(HttpServletRequest request, HttpSession session) {
        String expected = (String) session.getAttribute(SessionKeys.CSRF_TOKEN);
        String header = request.getHeader(HEADER_NAME);
        String cookie = cookieValue(request, COOKIE_NAME);
        if (expected == null || header == null || cookie == null
                || !constantTimeEquals(expected, header) || !constantTimeEquals(expected, cookie)) {
            throw BusinessException.forbidden("요청 보안 토큰이 유효하지 않습니다. 페이지를 새로고침한 뒤 다시 시도해주세요.");
        }
    }

    private String cookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) if (name.equals(cookie.getName())) return cookie.getValue();
        return null;
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigest.isEqual(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }
}
