// src/main/java/com/example/loginapp/controller/UserController.java

package com.example.loginapp.controller;

import com.example.loginapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("user");
        String password = loginRequest.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password must be provided"));
        }
        try {
            String loginResult = userService.login(username, password);
            if ("Login successful".equals(loginResult)) {
                List<String> cookies = userService.getSessionCookies();
                List<ResponseCookie> responseCookies = cookies.stream()
                        .map(cookieStr -> {
                            String[] parts = cookieStr.split(";", 2);
                            String[] keyValue = parts[0].split("=", 2);
                            if (keyValue.length == 2) {
                                return ResponseCookie.from(keyValue[0], keyValue[1])
                                        .httpOnly(true)
                                        .secure(true)
                                        .path("/")
                                        .build();
                            }
                            return null;
                        })
                        .filter(cookie -> cookie != null)
                        .toList();
                HttpHeaders headers = new HttpHeaders();
                responseCookies.forEach(cookie -> headers.add(HttpHeaders.SET_COOKIE, cookie.toString()));
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(Map.of("message", "Login successful"));
            } else if ("Invalid credentials".equals(loginResult)) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
            } else {
                return ResponseEntity.status(500).body(Map.of("message", loginResult));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to get session token"));
        }
    }
}
