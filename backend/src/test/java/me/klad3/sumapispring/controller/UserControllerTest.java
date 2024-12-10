package me.klad3.sumapispring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.klad3.sumapispring.dto.CreateUserRequest;
import me.klad3.sumapispring.dto.CreateUserResponse;
import me.klad3.sumapispring.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUser_ShouldReturnCreatedResponse() throws Exception {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username("testuser")
                .email("testuser@example.com")
                .institutionId("INST123")
                .studentName("Test User")
                .build();

        CreateUserResponse createUserResponse = CreateUserResponse.builder()
                .message("User created successfully")
                .build();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createUserResponse);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User created successfully")))
                .andExpect(jsonPath("$.data.message", is("User created successfully")));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    void createUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username("testuser")
                .email("invalid-email")
                .institutionId("INST123")
                .studentName("Test User")
                .build();

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.data.email", is("Email should be valid")));

        verify(userService, never()).createUser(any(CreateUserRequest.class));
    }

    @Test
    void createUser_ServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username("testuser")
                .email("testuser@example.com")
                .institutionId("INST123")
                .studentName("Test User")
                .build();

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Internal Server Error")))
                .andExpect(jsonPath("$.data.message", is("An unexpected error occurred")))
                .andExpect(jsonPath("$.data.error", is("Database error")));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }
}
