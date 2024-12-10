package me.klad3.sumapispring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.klad3.sumapispring.dto.LoginRequest;
import me.klad3.sumapispring.dto.LoginResponse;
import me.klad3.sumapispring.exception.AuthenticationException;
import me.klad3.sumapispring.service.AuthService;
import me.klad3.sumapispring.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_ShouldReturnSuccessResponse() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .user("testuser")
                .password("testpassword")
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .message("Login successful")
                .sessionCookies(List.of("SESSIONID=abc123; Path=/; HttpOnly", "CSRF-TOKEN=def456; Path=/; Secure"))
                .build();

        when(authService.login(any(String.class), any(String.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Login successful")))
                .andExpect(jsonPath("$.data.message", is("Login successful")))
                .andExpect(jsonPath("$.data.sessionCookies", hasSize(2)))
                .andExpect(header().string("Set-Cookie", containsString("SESSIONID=abc123")));

        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        verify(authService, times(1)).login(usernameCaptor.capture(), passwordCaptor.capture());
        assertEquals("testuser", usernameCaptor.getValue());
        assertEquals("testpassword", passwordCaptor.getValue());
    }

    @Test
    void login_WithInvalidInput_ShouldReturnBadRequest() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .user("")
                .password("")
                .build();

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.data.user", is("Username is mandatory")))
                .andExpect(jsonPath("$.data.password", is("Password is mandatory")));

        verify(authService, never()).login(any(String.class), any(String.class));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .user("invaliduser")
                .password("wrongpassword")
                .build();

        when(authService.login(any(String.class), any(String.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Authentication Error")))
                .andExpect(jsonPath("$.data", is(notNullValue())));

        verify(authService, times(1)).login("invaliduser", "wrongpassword");
    }

    @Test
    void login_ServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .user("testuser")
                .password("testpassword")
                .build();

        when(authService.login(any(String.class), any(String.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Internal Server Error")))
                .andExpect(jsonPath("$.data.message", is("An unexpected error occurred")))
                .andExpect(jsonPath("$.data.error", is("Database connection failed")));

        verify(authService, times(1)).login("testuser", "testpassword");
    }
}
