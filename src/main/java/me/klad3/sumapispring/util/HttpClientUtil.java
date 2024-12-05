// src/main/java/com/example/loginapp/util/HttpClientUtil.java

package me.klad3.sumapispring.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
@Slf4j
public class HttpClientUtil {

    private final HttpClient httpClient;
    private final CookieManager cookieManager;

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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "text/html")
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> post(String url, MultiValueMap<String, String> formData) throws IOException, InterruptedException {
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
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "text/html")
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String getFinalUrl(HttpResponse<?> response) {
        return response.uri().toString();
    }

    public List<String> getAllCookies() {
        return cookieManager.getCookieStore().get(URI.create("https://sum.unmsm.edu.pe")).stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .toList();
    }

    public void setResponseCookies(List<String> cookies) {
        cookies.forEach(cookie -> {
            try {
                cookieManager.getCookieStore().add(URI.create("https://sum.unmsm.edu.pe"), HttpCookie.parse(cookie).get(0));
            } catch (Exception e) {
                log.error("Error al agregar cookie: {}", cookie, e);
            }
        });
    }
}
