// src/main/java/com/example/loginapp/service/UserService.java

package me.klad3.sumapispring.service;

import me.klad3.sumapispring.util.HtmlParserUtil;
import me.klad3.sumapispring.util.HttpClientUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class UserService {

    private final HttpClientUtil httpClientUtil;
    private final HtmlParserUtil htmlParserUtil;
    private List<String> sessionCookies;

    public UserService(HttpClientUtil httpClientUtil, HtmlParserUtil htmlParserUtil) {
        this.httpClientUtil = httpClientUtil;
        this.htmlParserUtil = htmlParserUtil;
    }

    public String login(String username, String password) throws Exception {
        HttpResponse<String> getResponse = httpClientUtil.get("https://sum.unmsm.edu.pe/alumnoWebSum/login");
        if (getResponse.statusCode() != 200) {
            throw new Exception("Failed to fetch login page");
        }
        String csrfToken = htmlParserUtil.extractCsrfToken(getResponse.body());
        if (csrfToken == null) {
            throw new Exception("CSRF token not found");
        }
        MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
        loginData.add("_csrf", csrfToken);
        loginData.add("login", username);
        loginData.add("clave", password);
        HttpResponse<String> postResponse = httpClientUtil.post("https://sum.unmsm.edu.pe/alumnoWebSum/login", loginData);
        String responseUrl = httpClientUtil.getFinalUrl(postResponse);
        if ("https://sum.unmsm.edu.pe/alumnoWebSum/login".equals(responseUrl)) {
            return "Invalid credentials";
        }
        if ("https://sum.unmsm.edu.pe/alumnoWebSum/sesionIniciada".equals(responseUrl)) {
            httpClientUtil.get("https://sum.unmsm.edu.pe/alumnoWebSum/reiniciarSesion?us=" + username);
        }
        sessionCookies = httpClientUtil.getAllCookies();
        return "Login successful";
    }

    public List<String> getSessionCookies() {
        return sessionCookies;
    }
}
