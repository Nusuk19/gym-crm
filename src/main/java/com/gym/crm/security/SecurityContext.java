package com.gym.crm.security;

public final class SecurityContext {

    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    private SecurityContext() {
    }

    public static void setCurrentUser(String username) {
        currentUser.set(username);
    }

    public static String getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}