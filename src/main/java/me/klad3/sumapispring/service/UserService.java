package me.klad3.sumapispring.service;

import lombok.RequiredArgsConstructor;
import me.klad3.sumapispring.dto.LoginResponse;
import me.klad3.sumapispring.util.HttpClientUtil;
import me.klad3.sumapispring.util.HtmlParserUtil;
import me.klad3.sumapispring.exception.AuthenticationException;
import me.klad3.sumapispring.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.http.HttpResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final HttpClientUtil httpClientUtil;
    private final HtmlParserUtil htmlParserUtil;

    public LoginResponse login(String username, String password) throws Exception {
        HttpResponse<String> getResponse = httpClientUtil.get("https://sum.unmsm.edu.pe/alumnoWebSum/login");
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

        HttpResponse<String> postResponse = httpClientUtil.post("https://sum.unmsm.edu.pe/alumnoWebSum/login", loginData);
        String responseUrl = httpClientUtil.getFinalUrl(postResponse);

        if ("https://sum.unmsm.edu.pe/alumnoWebSum/login".equals(responseUrl)) {
            throw new AuthenticationException("Invalid credentials");
        }

        if ("https://sum.unmsm.edu.pe/alumnoWebSum/sesionIniciada".equals(responseUrl)) {
            httpClientUtil.get("https://sum.unmsm.edu.pe/alumnoWebSum/reiniciarSesion?us=" + username);
        }

        List<String> sessionCookies = httpClientUtil.getAllCookies();
        return new LoginResponse("Login successful", sessionCookies);
    }
}