package com.gym.crm.security;

import com.gym.crm.exception.AuthenticationFailedException;
import com.gym.crm.exception.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationAspectTest {

    private static final String USERNAME = "john.doe";

    private final AuthenticationAspect aspect = new AuthenticationAspect();

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should pass when logged user matches target username")
    void checkAuthentication_whenUserMatchesTarget_shouldPass() {
        mockSession(USERNAME);
        JoinPoint joinPoint = mockJoinPoint(USERNAME);

        assertDoesNotThrow(() -> aspect.checkAuthentication(joinPoint));
    }

    @Test
    @DisplayName("Should pass when method has no username argument (no target to check)")
    void checkAuthentication_whenNoArgs_shouldPass() {
        mockSession(USERNAME);
        JoinPoint joinPoint = mockJoinPoint();

        assertDoesNotThrow(() -> aspect.checkAuthentication(joinPoint));
    }

    @Test
    @DisplayName("Should throw AuthenticationFailedException when no session (not logged in)")
    void checkAuthentication_whenNoSession_shouldThrowAuthenticationFailed() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(request.getSession(false)).thenReturn(null);

        assertThrows(AuthenticationFailedException.class, () -> aspect.checkAuthentication(mockJoinPoint(USERNAME)));
    }

    @Test
    @DisplayName("Should throw AuthenticationFailedException when no request context (not logged in)")
    void checkAuthentication_whenNoRequestAttributes_shouldThrowAuthenticationFailed() {
        RequestContextHolder.resetRequestAttributes();

        assertThrows(AuthenticationFailedException.class, () -> aspect.checkAuthentication(mockJoinPoint(USERNAME)));
    }

    @Test
    @DisplayName("Should throw AuthenticationFailedException when session has no username (not logged in)")
    void checkAuthentication_whenSessionHasNoUsername_shouldThrowAuthenticationFailed() {
        mockSession(null);

        assertThrows(AuthenticationFailedException.class, () -> aspect.checkAuthentication(mockJoinPoint(USERNAME)));
    }

    @Test
    @DisplayName("Should throw AuthorizationException when logged user differs from target (different user)")
    void checkAuthentication_whenUserDoesNotMatchTarget_shouldThrowAuthorizationException() {
        mockSession("other.user");

        assertThrows(AuthorizationException.class, () -> aspect.checkAuthentication(mockJoinPoint(USERNAME)));
    }

    @Test
    @DisplayName("Should pass when first arg is object with getUsername matching logged user")
    void checkAuthentication_whenArgHasGetUsernameMatchingLoggedUser_shouldPass() {
        mockSession(USERNAME);
        Object argWithUsername = new Object() {
            public String getUsername() {
                return USERNAME;
            }
        };

        JoinPoint joinPoint = mockJoinPoint(argWithUsername);

        assertDoesNotThrow(() -> aspect.checkAuthentication(joinPoint));
    }

    @Test
    @DisplayName("Should throw AuthorizationException when arg object getUsername differs from logged user")
    void checkAuthentication_whenArgHasGetUsernameDifferentFromLoggedUser_shouldThrowAuthorizationException() {
        mockSession("other.user");
        Object argWithUsername = new Object() {
            public String getUsername() {
                return USERNAME;
            }
        };

        JoinPoint joinPoint = mockJoinPoint(argWithUsername);

        assertThrows(AuthorizationException.class, () -> aspect.checkAuthentication(joinPoint));
    }

    @Test
    @DisplayName("Should pass when first arg has no getUsername method")
    void checkAuthentication_whenArgHasNoGetUsernameMethod_shouldPass() {
        mockSession(USERNAME);

        JoinPoint joinPoint = mockJoinPoint(42);

        assertDoesNotThrow(() -> aspect.checkAuthentication(joinPoint));
    }

    private void mockSession(String username) {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(session.getAttribute("username")).thenReturn(username);
        when(request.getSession(false)).thenReturn(session);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private JoinPoint mockJoinPoint(Object... args) {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(args);

        return joinPoint;
    }
}