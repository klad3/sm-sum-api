package me.klad3.sumapispring.service;

import me.klad3.sumapispring.dto.LoginResponse;
import me.klad3.sumapispring.exception.AuthenticationException;
import me.klad3.sumapispring.exception.BadRequestException;
import me.klad3.sumapispring.util.HtmlParserUtil;
import me.klad3.sumapispring.util.HttpClientUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private HttpClientUtil httpClientUtil;

    @Mock
    private HtmlParserUtil htmlParserUtil;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<MultiValueMap<String, String>> formDataCaptor;

    private static final String TEST_LOGIN_URL = "https://sum.unmsm.edu.pe/alumnoWebSum/login";
    private static final String TEST_SESSION_URL = "https://sum.unmsm.edu.pe/alumnoWebSum/sesionIniciada";
    private static final String TEST_RESTART_SESSION_URL = "https://sum.unmsm.edu.pe/alumnoWebSum/reiniciarSesion?us=";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(httpClientUtil.getFinalUrl(any(HttpResponse.class))).thenAnswer(invocation -> {
            HttpResponse<?> response = invocation.getArgument(0);
            return response.uri().toString();
        });
    }

    @Test
    void login_Success() throws IOException, InterruptedException {
        String username = "john_doe";
        String password = "securePassword";
        String csrfToken = "dummyCsrfToken";
        String sessionCookie = "SESSIONID=abc123";

        HttpResponse<String> getResponse = mock(HttpResponse.class);
        when(getResponse.statusCode()).thenReturn(200);
        when(getResponse.body()).thenReturn("<html><input type='hidden' name='_csrf' value='" + csrfToken + "'></html>");
        when(httpClientUtil.get(TEST_LOGIN_URL)).thenReturn(getResponse);

        when(htmlParserUtil.extractCsrfToken(getResponse.body())).thenReturn(csrfToken);

        HttpResponse<String> postResponse = mock(HttpResponse.class);
        when(postResponse.statusCode()).thenReturn(200);
        when(postResponse.uri()).thenReturn(URI.create(TEST_SESSION_URL));
        when(httpClientUtil.post(eq(TEST_LOGIN_URL), any(MultiValueMap.class))).thenReturn(postResponse);

        HttpResponse<String> restartResponse = mock(HttpResponse.class);
        when(restartResponse.statusCode()).thenReturn(200);
        when(httpClientUtil.get(TEST_RESTART_SESSION_URL + username)).thenReturn(restartResponse);

        when(httpClientUtil.getAllCookies()).thenReturn(List.of(sessionCookie));

        LoginResponse response = authService.login(username, password);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals(List.of(sessionCookie), response.getSessionCookies());

        verify(httpClientUtil, times(1)).get(TEST_LOGIN_URL);
        verify(htmlParserUtil, times(1)).extractCsrfToken(getResponse.body());

        verify(httpClientUtil, times(1)).post(eq(TEST_LOGIN_URL), formDataCaptor.capture());

        MultiValueMap<String, String> capturedFormData = formDataCaptor.getValue();
        assertEquals(csrfToken, capturedFormData.getFirst("_csrf"));
        assertEquals(username, capturedFormData.getFirst("login"));
        assertEquals(password, capturedFormData.getFirst("clave"));

        verify(httpClientUtil, times(1)).get(TEST_RESTART_SESSION_URL + username);
        verify(httpClientUtil, times(1)).getAllCookies();

        verify(httpClientUtil, times(1)).getFinalUrl(postResponse);

        verifyNoMoreInteractions(httpClientUtil, htmlParserUtil);
    }

    @Test
    void login_FetchLoginPage_Fails() throws IOException, InterruptedException {
        String username = "john_doe";
        String password = "securePassword";

        HttpResponse<String> getResponse = mock(HttpResponse.class);
        when(getResponse.statusCode()).thenReturn(500);
        when(httpClientUtil.get(TEST_LOGIN_URL)).thenReturn(getResponse);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.login(username, password);
        });

        assertEquals("Failed to fetch login page", exception.getMessage());

        verify(httpClientUtil, times(1)).get(TEST_LOGIN_URL);
        verify(htmlParserUtil, never()).extractCsrfToken(anyString());
        verify(httpClientUtil, never()).post(anyString(), any(MultiValueMap.class));
        verify(httpClientUtil, never()).get(TEST_RESTART_SESSION_URL + username);
        verify(httpClientUtil, never()).getAllCookies();

        verifyNoMoreInteractions(httpClientUtil, htmlParserUtil);
    }

    @Test
    void login_CsrfToken_NotFound() throws IOException, InterruptedException {
        String username = "john_doe";
        String password = "securePassword";

        HttpResponse<String> getResponse = mock(HttpResponse.class);
        when(getResponse.statusCode()).thenReturn(200);
        when(getResponse.body()).thenReturn("<html><form></form></html>");
        when(httpClientUtil.get(TEST_LOGIN_URL)).thenReturn(getResponse);

        when(htmlParserUtil.extractCsrfToken(getResponse.body())).thenReturn(null);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.login(username, password);
        });

        assertEquals("CSRF token not found", exception.getMessage());

        verify(httpClientUtil, times(1)).get(TEST_LOGIN_URL);
        verify(htmlParserUtil, times(1)).extractCsrfToken(getResponse.body());
        verify(httpClientUtil, never()).post(anyString(), any(MultiValueMap.class));
        verify(httpClientUtil, never()).get(TEST_RESTART_SESSION_URL + username);
        verify(httpClientUtil, never()).getAllCookies();

        verifyNoMoreInteractions(httpClientUtil, htmlParserUtil);
    }

    @Test
    void login_InvalidCredentials_ThrowsAuthenticationException() throws IOException, InterruptedException {
        String username = "john_doe";
        String password = "wrongPassword";
        String csrfToken = "dummyCsrfToken";

        HttpResponse<String> getResponse = mock(HttpResponse.class);
        when(getResponse.statusCode()).thenReturn(200);
        when(getResponse.body()).thenReturn("<html><input type='hidden' name='_csrf' value='" + csrfToken + "'></html>");
        when(httpClientUtil.get(TEST_LOGIN_URL)).thenReturn(getResponse);

        when(htmlParserUtil.extractCsrfToken(getResponse.body())).thenReturn(csrfToken);

        HttpResponse<String> postResponse = mock(HttpResponse.class);
        when(postResponse.statusCode()).thenReturn(200);
        when(postResponse.uri()).thenReturn(URI.create(TEST_LOGIN_URL));
        when(httpClientUtil.post(eq(TEST_LOGIN_URL), any(MultiValueMap.class))).thenReturn(postResponse);

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authService.login(username, password);
        });

        assertEquals("Invalid credentials", exception.getMessage());

        verify(httpClientUtil, times(1)).get(TEST_LOGIN_URL);
        verify(htmlParserUtil, times(1)).extractCsrfToken(getResponse.body());
        verify(httpClientUtil, times(1)).post(eq(TEST_LOGIN_URL), formDataCaptor.capture());
        verify(httpClientUtil, never()).get(TEST_RESTART_SESSION_URL + username);
        verify(httpClientUtil, never()).getAllCookies();

        MultiValueMap<String, String> capturedFormData = formDataCaptor.getValue();
        assertEquals(csrfToken, capturedFormData.getFirst("_csrf"));
        assertEquals(username, capturedFormData.getFirst("login"));
        assertEquals(password, capturedFormData.getFirst("clave"));

        verify(httpClientUtil, times(1)).getFinalUrl(postResponse);

        verifyNoMoreInteractions(httpClientUtil, htmlParserUtil);
    }

    @Test
    void login_SessionStarted_Success() throws IOException, InterruptedException {
        String username = "john_doe";
        String password = "securePassword";
        String csrfToken = "dummyCsrfToken";
        String sessionCookie = "SESSIONID=xyz789";

        HttpResponse<String> getResponse = mock(HttpResponse.class);
        when(getResponse.statusCode()).thenReturn(200);
        when(getResponse.body()).thenReturn("<html><input type='hidden' name='_csrf' value='" + csrfToken + "'></html>");
        when(httpClientUtil.get(TEST_LOGIN_URL)).thenReturn(getResponse);

        when(htmlParserUtil.extractCsrfToken(getResponse.body())).thenReturn(csrfToken);

        HttpResponse<String> postResponse = mock(HttpResponse.class);
        when(postResponse.statusCode()).thenReturn(200);
        when(postResponse.uri()).thenReturn(URI.create(TEST_SESSION_URL));
        when(httpClientUtil.post(eq(TEST_LOGIN_URL), any(MultiValueMap.class))).thenReturn(postResponse);

        HttpResponse<String> restartResponse = mock(HttpResponse.class);
        when(restartResponse.statusCode()).thenReturn(200);
        when(httpClientUtil.get(TEST_RESTART_SESSION_URL + username)).thenReturn(restartResponse);

        when(httpClientUtil.getAllCookies()).thenReturn(List.of(sessionCookie));

        LoginResponse response = authService.login(username, password);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals(List.of(sessionCookie), response.getSessionCookies());

        verify(httpClientUtil, times(1)).get(TEST_LOGIN_URL);
        verify(htmlParserUtil, times(1)).extractCsrfToken(getResponse.body());

        verify(httpClientUtil, times(1)).post(eq(TEST_LOGIN_URL), formDataCaptor.capture());

        MultiValueMap<String, String> capturedFormData = formDataCaptor.getValue();
        assertEquals(csrfToken, capturedFormData.getFirst("_csrf"));
        assertEquals(username, capturedFormData.getFirst("login"));
        assertEquals(password, capturedFormData.getFirst("clave"));

        verify(httpClientUtil, times(1)).get(TEST_RESTART_SESSION_URL + username);
        verify(httpClientUtil, times(1)).getAllCookies();

        verify(httpClientUtil, times(1)).getFinalUrl(postResponse);

        verifyNoMoreInteractions(httpClientUtil, htmlParserUtil);
    }
}
