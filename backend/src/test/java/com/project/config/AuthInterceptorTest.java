package com.project.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import com.project.common.BusinessException;
import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.domain.Role;

import jakarta.servlet.http.Cookie;

class AuthInterceptorTest {
    private final CsrfTokenService csrfTokens = new CsrfTokenService(false, "Lax");
    private final AuthInterceptor interceptor = new AuthInterceptor(csrfTokens);

    @Test
    void acceptsAuthenticatedMutationWithSessionCookieAndHeaderToken() {
        MockHttpServletRequest request = authenticatedRequest();
        request.setCookies(new Cookie(CsrfTokenService.COOKIE_NAME, "valid-token"));
        request.addHeader(CsrfTokenService.HEADER_NAME, "valid-token");

        assertDoesNotThrow(() -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void rejectsAuthenticatedMutationWithoutCsrfToken() {
        MockHttpServletRequest request = authenticatedRequest();

        assertThrows(BusinessException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    private MockHttpServletRequest authenticatedRequest() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionKeys.LOGIN_USER, new LoginUser(1L, "관리자", Role.ADMIN, true));
        session.setAttribute(SessionKeys.CSRF_TOKEN, "valid-token");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("PATCH");
        request.setRequestURI("/api/admin/members/7/role");
        request.setSession(session);
        return request;
    }
}
