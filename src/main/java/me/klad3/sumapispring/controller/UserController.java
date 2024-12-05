package me.klad3.sumapispring.controller;

import lombok.RequiredArgsConstructor;
import me.klad3.sumapispring.dto.ApiResponse;
import me.klad3.sumapispring.dto.LoginRequest;
import me.klad3.sumapispring.dto.LoginResponse;
import me.klad3.sumapispring.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Validated @RequestBody LoginRequest loginRequest) throws Exception {
        String username = loginRequest.getUser();
        String password = loginRequest.getPassword();

        LoginResponse loginResponse = userService.login(username, password);

        List<String> cookies = loginResponse.getSessionCookies();
        List<ResponseCookie> responseCookies = cookies.stream()
                .map(cookieStr -> {
                    String[] parts = cookieStr.split(";", 2);
                    String[] keyValue = parts[0].split("=", 2);
                    if (keyValue.length == 2) {
                        return ResponseCookie.from(keyValue[0], keyValue[1])
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .sameSite("Strict")
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        HttpHeaders headers = new HttpHeaders();
        responseCookies.forEach(cookie -> headers.add(HttpHeaders.SET_COOKIE, cookie.toString()));

        ApiResponse<LoginResponse> response = ApiResponse.success("Login successful", loginResponse);

        return ResponseEntity.ok()
                .headers(headers)
                .body(response);
    }
}