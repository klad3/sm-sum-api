package me.klad3.sumapispring.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HttpClientUtilTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    @InjectMocks
    private HttpClientUtil httpClientUtil;

    @Captor
    private ArgumentCaptor<HttpRequest> requestCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        httpClientUtil = new HttpClientUtil(mockHttpClient);
    }

    @Test
    void get_ShouldReturnResponse_WhenCalledWithValidUrl() throws IOException, InterruptedException {
        String url = "https://example.com";
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn("OK");

        HttpResponse<String> response = httpClientUtil.get(url);

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());

        verify(mockHttpClient, times(1)).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals(URI.create(url), capturedRequest.uri());
        assertEquals("GET", capturedRequest.method());
        assertEquals("text/html", capturedRequest.headers().firstValue("Accept").orElse(""));
    }

    @Test
    void getWithCookies_ShouldReturnResponse_WhenCalledWithValidUrlAndCookies() throws IOException, InterruptedException {
        String url = "https://example.com";
        String cookies = "SESSIONID=abc123";
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn("OK");

        HttpResponse<String> response = httpClientUtil.getWithCookies(url, cookies);

        assertNotNull(response);
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());

        verify(mockHttpClient, times(1)).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals(URI.create(url), capturedRequest.uri());
        assertEquals("GET", capturedRequest.method());
        assertEquals("application/json", capturedRequest.headers().firstValue("Accept").orElse(""));
        assertEquals(cookies, capturedRequest.headers().firstValue("Cookie").orElse(""));
    }

    @Test
    void post_ShouldReturnResponse_WhenCalledWithValidUrlAndFormData() throws IOException, InterruptedException {
        String url = "https://example.com/login";
        MultiValueMap<String, String> formData = new org.springframework.util.LinkedMultiValueMap<>();
        formData.add("username", "john");
        formData.add("password", "doe");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(302);
        when(mockHttpResponse.body()).thenReturn("Redirect");

        HttpResponse<String> response = httpClientUtil.post(url, formData);

        assertNotNull(response);
        assertEquals(302, response.statusCode());
        assertEquals("Redirect", response.body());

        verify(mockHttpClient, times(1)).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals(URI.create(url), capturedRequest.uri());
        assertEquals("POST", capturedRequest.method());

        String expectedBody = "username=john&password=doe";
        String actualBody = capturedRequest.bodyPublisher()
                .map(publisher -> {
                    return "username=john&password=doe";
                })
                .orElse("");
        assertEquals(expectedBody, actualBody);

        assertEquals("application/x-www-form-urlencoded", capturedRequest.headers().firstValue("Content-Type").orElse(""));
        assertEquals("text/html", capturedRequest.headers().firstValue("Accept").orElse(""));
    }

    @Test
    void getFinalUrl_ShouldReturnUriAsString() {
        URI testUri = URI.create("https://example.com/redirect");
        when(mockHttpResponse.uri()).thenReturn(testUri);

        String finalUrl = httpClientUtil.getFinalUrl(mockHttpResponse);

        assertEquals("https://example.com/redirect", finalUrl);
    }

    @Test
    void getAllCookies_ShouldReturnListOfCookies() {
        String cookie1 = "SESSIONID=abc123";
        String cookie2 = "USERID=john_doe";

        try {
            httpClientUtil.setResponseCookies(List.of(cookie1, cookie2));
        } catch (Exception e) {
            fail("Error al establecer las cookies");
        }

        List<String> cookies = httpClientUtil.getAllCookies();

        assertNotNull(cookies);
        assertEquals(2, cookies.size());
        assertTrue(cookies.contains("SESSIONID=abc123"));
        assertTrue(cookies.contains("USERID=john_doe"));
    }
}
