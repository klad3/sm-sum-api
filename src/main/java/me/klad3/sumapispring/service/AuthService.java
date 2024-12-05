package me.klad3.sumapispring.service;

import lombok.RequiredArgsConstructor;
import me.klad3.sumapispring.dto.LoginResponse;
import me.klad3.sumapispring.exception.AuthenticationException;
import me.klad3.sumapispring.exception.BadRequestException;
import me.klad3.sumapispring.util.HtmlParserUtil;
import me.klad3.sumapispring.util.HttpClientUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final HttpClientUtil httpClientUtil;
    private final HtmlParserUtil htmlParserUtil;

    private static final String LOGIN_URL = "https://sum.unmsm.edu.pe/alumnoWebSum/login";
    private static final String SESSION_URL = "https://sum.unmsm.edu.pe/alumnoWebSum/sesionIniciada";
    private static final String RESTART_SESSION_URL = "https://sum.unmsm.edu.pe/alumnoWebSum/reiniciarSesion?us=";

    public LoginResponse login(String username, String password) throws IOException, InterruptedException {
        HttpResponse<String> getResponse = httpClientUtil.get(LOGIN_URL);
        if (getResponse.statusCode() != 200) {
            throw new BadRequestException("Failed to fetch login page");
        }

        String csrfToken = htmlParserUtil.extractCsrfToken(getResponse.body());
        if (csrfToken == null) {
            throw new BadRequestException("CSRF token not found");
        }

        MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
        loginData.add("_csrf", csrfToken);
        loginData.add("login", username);
        loginData.add("clave", password);

        HttpResponse<String> postResponse = httpClientUtil.post(LOGIN_URL, loginData);
        String responseUrl = httpClientUtil.getFinalUrl(postResponse);

        if (LOGIN_URL.equals(responseUrl)) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (SESSION_URL.equals(responseUrl)) {
            httpClientUtil.get(RESTART_SESSION_URL + username);
        }

        List<String> sessionCookies = httpClientUtil.getAllCookies();
        return new LoginResponse("Login successful", sessionCookies);
    }
}