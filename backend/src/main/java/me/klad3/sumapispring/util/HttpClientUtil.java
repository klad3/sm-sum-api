package me.klad3.sumapispring.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
@Slf4j
public class HttpClientUtil {

    private static final String ACCEPT_HEADER = "Accept";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private static final String ACCEPT_JSON = "application/json";
    private static final String ACCEPT_HTML = "text/html";
    private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    private static final String BASE_URL = "https://sum.unmsm.edu.pe";

    private final HttpClient httpClient;
    private final CookieManager cookieManager;

    public HttpClientUtil(HttpClient httpClient) {
        this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.httpClient = httpClient;
    }

    public HttpClientUtil() {
        this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .cookieHandler(cookieManager)
                .build();
    }

    public HttpResponse<String> get(String url) throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header(ACCEPT_HEADER, ACCEPT_HTML)
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Error en la solicitud GET a {}: {}", url, e.getMessage());
            throw e;
        }
    }

    public HttpResponse<String> getWithCookies(String url, String cookies) throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header(ACCEPT_HEADER, ACCEPT_JSON)
                    .header(COOKIE_HEADER, cookies)
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Error en la solicitud GET con cookies a {}: {}", url, e.getMessage());
            throw e;
        }
    }

    public HttpResponse<String> post(String url, MultiValueMap<String, String> formData) throws IOException, InterruptedException {
        try {
            StringBuilder formBody = new StringBuilder();
            formData.forEach((key, values) -> {
                for (String value : values) {
                    if (formBody.length() > 0) {
                        formBody.append("&");
                    }
                    formBody.append(key).append("=").append(value);
                }
            });
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(formBody.toString()))
                    .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_FORM)
                    .header(ACCEPT_HEADER, ACCEPT_HTML)
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Error en la solicitud POST a {}: {}", url, e.getMessage());
            throw e;
        }
    }

    public HttpResponse<String> postWithCookies(String url, String body, String cookies, String contentType) throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header(CONTENT_TYPE_HEADER, contentType)
                    .header(ACCEPT_HEADER, ACCEPT_JSON)
                    .header(COOKIE_HEADER, cookies)
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("Error en la solicitud POST con cookies a {}: {}", url, e.getMessage());
            throw e;
        }
    }

    public String getFinalUrl(HttpResponse<?> response) {
        return response.uri().toString();
    }

    public List<String> getAllCookies() {
        return cookieManager.getCookieStore().get(URI.create(BASE_URL)).stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .toList();
    }

    public void setResponseCookies(List<String> cookies) {
        cookies.forEach(cookie -> {
            try {
                cookieManager.getCookieStore().add(URI.create(BASE_URL), HttpCookie.parse(cookie).get(0));
            } catch (Exception e) {
                log.error("Error al agregar cookie: {}", cookie, e);
            }
        });
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }
}
