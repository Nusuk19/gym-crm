package com.gym.crm.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String USERNAME = "Abdul.Hariton";
    private static final String TOKEN = "valid.jwt.token";
    private static final String BEARER_TOKEN = "Bearer " + TOKEN;

    @Mock
    private JwtService jwtService;

    @Mock
    private GymUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void doFilter_validBearerToken_shouldSetAuthentication() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, BEARER_TOKEN);
        UserDetails userDetails = buildUserDetails();
        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(USERNAME);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_missingAuthorizationHeader_shouldContinueFilterChain() throws Exception {
        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).isTokenValid(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_notBearerAuthorizationHeader_shouldContinueFilterChain() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic abc123");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).isTokenValid(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_invalidToken_shouldNotSetAuthentication() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, BEARER_TOKEN);
        when(jwtService.isTokenValid(TOKEN)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(userDetailsService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_alreadyAuthenticated_shouldNotReAuthenticate() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, BEARER_TOKEN);
        UserDetails userDetails = buildUserDetails();
        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);
        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername(USERNAME);
    }

    @Test
    void doFilter_usernameIsNull_shouldNotSetAuthentication() throws Exception {
        request.addHeader(HttpHeaders.AUTHORIZATION, BEARER_TOKEN);
        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractUsername(TOKEN)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verify(jwtService).isTokenValid(TOKEN);
        verify(jwtService).extractUsername(TOKEN);
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    private UserDetails buildUserDetails() {
        return User.builder()
                .username(USERNAME)
                .password("password")
                .authorities(List.of())
                .build();
    }
}