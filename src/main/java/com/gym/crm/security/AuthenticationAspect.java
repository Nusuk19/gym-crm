package com.gym.crm.security;

import com.gym.crm.exception.AuthenticationFailedException;
import com.gym.crm.exception.AuthorizationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Aspect
@Component
public class AuthenticationAspect {

    private static final String USERNAME_SESSION_KEY = "username";

    @Before("@annotation(com.gym.crm.security.Authenticated)")
    public void checkAuthentication(JoinPoint joinPoint) {
        String authenticatedUsername = requireAuthenticatedUser(joinPoint);
        requireAuthorizedUser(joinPoint, authenticatedUsername);
    }

    private String requireAuthenticatedUser(JoinPoint joinPoint) {
        return getLoggedInUsername()
                .orElseThrow(() -> {
                    log.warn("Unauthenticated access attempt to {}", joinPoint.getSignature());
                    return new AuthenticationFailedException("Authentication required");
                });
    }

    private Optional<String> getLoggedInUsername() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(req -> req.getSession(false))
                .map(session -> (String) session.getAttribute(USERNAME_SESSION_KEY));
    }

    private void requireAuthorizedUser(JoinPoint joinPoint, String authenticatedUsername) {
        extractTargetUsername(joinPoint)
                .filter(targetUsername -> !targetUsername.equals(authenticatedUsername))
                .ifPresent(targetUsername -> {
                    log.warn("User '{}' attempted to access resource of '{}'", authenticatedUsername, targetUsername);
                    throw new AuthorizationException("User is not authorized for request operation");
                });
    }

    private Optional<String> extractTargetUsername(JoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .findFirst()
                .flatMap(this::resolveUsername);
    }

    private Optional<String> resolveUsername(Object arg) {
        if (arg instanceof String username) {
            return Optional.of(username);
        }

        try {
            return Optional.ofNullable(
                    arg.getClass()
                            .getMethod("getUsername")
                            .invoke(arg)
            ).map(String.class::cast);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }
}