package me.klad3.sumapispring.security;

import me.klad3.sumapispring.exception.ApiKeyUnauthorizedException;
import me.klad3.sumapispring.model.User;
import me.klad3.sumapispring.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("security-test")
public class ApiKeyAuthFilterTest {

    @Mock
    private UserService userService;

    @Mock
    private HandlerExceptionResolver resolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private ApiKeyAuthFilter apiKeyAuthFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(apiKeyAuthFilter, "apiKeyHeaderName", "API-Key");
        ReflectionTestUtils.setField(apiKeyAuthFilter, "apiSecretHeaderName", "API-Secret");
        when(request.getHeader("API-Key")).thenReturn(null);
        when(request.getHeader("API-Secret")).thenReturn(null);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidApiKeyAndSecret_ShouldAuthenticateAndProceed() throws ServletException, IOException {
        when(request.getHeader("API-Key")).thenReturn("valid-api-key");
        when(request.getHeader("API-Secret")).thenReturn("valid-api-secret");

        User mockUser = mock(User.class);
        when(userService.findByApiKey("valid-api-key")).thenReturn(Optional.of(mockUser));
        when(mockUser.verifyApiSecret("valid-api-secret")).thenReturn(true);
        when(mockUser.getApiKey()).thenReturn("valid-api-key");

        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("valid-api-key", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_API_CLIENT")));

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMissingApiKey_ShouldInvokeResolverWithException() throws ServletException, IOException {
        when(request.getHeader("API-Key")).thenReturn(null);
        when(request.getHeader("API-Secret")).thenReturn("some-secret");

        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        ArgumentCaptor<ApiKeyUnauthorizedException> exceptionCaptor = ArgumentCaptor.forClass(ApiKeyUnauthorizedException.class);
        verify(resolver, times(1)).resolveException(eq(request), eq(response), any(), exceptionCaptor.capture());
        assertEquals("Missing API Key or Secret", exceptionCaptor.getValue().getMessage());

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidApiKey_ShouldInvokeResolverWithException() throws ServletException, IOException {
        when(request.getHeader("API-Key")).thenReturn("invalid-api-key");
        when(request.getHeader("API-Secret")).thenReturn("invalid-api-secret");

        when(userService.findByApiKey("invalid-api-key")).thenReturn(Optional.empty());

        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        ArgumentCaptor<ApiKeyUnauthorizedException> exceptionCaptor = ArgumentCaptor.forClass(ApiKeyUnauthorizedException.class);
        verify(resolver, times(1)).resolveException(eq(request), eq(response), any(), exceptionCaptor.capture());
        assertEquals("Invalid API Key or Secret", exceptionCaptor.getValue().getMessage());

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidApiSecret_ShouldInvokeResolverWithException() throws ServletException, IOException {
        when(request.getHeader("API-Key")).thenReturn("valid-api-key");
        when(request.getHeader("API-Secret")).thenReturn("invalid-api-secret");

        User mockUser = mock(User.class);
        when(userService.findByApiKey("valid-api-key")).thenReturn(Optional.of(mockUser));
        when(mockUser.verifyApiSecret("invalid-api-secret")).thenReturn(false);

        apiKeyAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        ArgumentCaptor<ApiKeyUnauthorizedException> exceptionCaptor = ArgumentCaptor.forClass(ApiKeyUnauthorizedException.class);
        verify(resolver, times(1)).resolveException(eq(request), eq(response), any(), exceptionCaptor.capture());
        assertEquals("Invalid API Key or Secret", exceptionCaptor.getValue().getMessage());

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotFilterExcludedEndpoints() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/user/create");

        apiKeyAuthFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }
}
