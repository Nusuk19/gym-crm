package com.gym.crm.security;

import com.gym.crm.exception.AuthenticationException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthenticationAspect {

    @Before(value = "@annotation(com.gym.crm.security.Authenticated) && args(username, ..)", argNames = "username")
    public void checkAuthentication(String username) {
        String currentUser = SecurityContext.getCurrentUser();

        if (currentUser == null) {
            throw new AuthenticationException("User is not authenticated");
        }
        if (!currentUser.equals(username)) {
            throw new AuthenticationException("Authenticated user '%s' does not match requested user '%s'"
                    .formatted(currentUser, username));
        }
    }
}