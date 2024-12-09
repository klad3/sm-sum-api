package me.klad3.sumapispring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.klad3.sumapispring.controller.AuthController;
import me.klad3.sumapispring.controller.CourseController;
import me.klad3.sumapispring.controller.TestController;
import me.klad3.sumapispring.controller.UserController;
import me.klad3.sumapispring.dto.CreateUserRequest;
import me.klad3.sumapispring.dto.CreateUserResponse;
import me.klad3.sumapispring.dto.LoginRequest;
import me.klad3.sumapispring.dto.LoginResponse;
import me.klad3.sumapispring.exception.ApiKeyUnauthorizedException;
import me.klad3.sumapispring.security.ApiKeyAuthFilter;
import me.klad3.sumapispring.service.AuthService;
import me.klad3.sumapispring.service.CourseService;
import me.klad3.sumapispring.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthController.class,
        UserController.class,
        CourseController.class,
        TestController.class
})
@Import({SecurityConfig.class, ApiKeyAuthFilter.class})
@ActiveProfiles("security-test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private CourseService courseService;

    private final String API_KEY = "valid-api-key";
    private final String API_SECRET = "valid-api-secret";

    @BeforeEach
    void setUp() {
        me.klad3.sumapispring.model.User mockUser = Mockito.mock(me.klad3.sumapispring.model.User.class);
        Mockito.when(mockUser.getApiKey()).thenReturn(API_KEY);
        Mockito.when(mockUser.getApiSecretHash()).thenReturn(API_SECRET);
        Mockito.when(mockUser.verifyApiSecret(API_SECRET)).thenReturn(true);

        Mockito.when(userService.findByApiKey(API_KEY)).thenReturn(Optional.of(mockUser));

        CreateUserResponse createUserResponse = CreateUserResponse.builder()
                .message("User created successfully")
                .build();
        Mockito.when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createUserResponse);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenAccessPublicEndpoint_thenOk() throws Exception {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username("testuser")
                .email("testuser@example.com")
                .institutionId("INST123")
                .studentName("Test User")
                .build();

        String createUserJson = objectMapper.writeValueAsString(createUserRequest);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserJson))
                .andExpect(status().isCreated());
    }

    @Test
    void whenAccessProtectedEndpointWithoutAuth_thenUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUser("testuser");
        loginRequest.setPassword("password123");

        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenAccessProtectedEndpointWithValidApiKey_thenOk() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUser("testuser");
        loginRequest.setPassword("password123");

        String loginJson = objectMapper.writeValueAsString(loginRequest);

        LoginResponse mockLoginResponse = LoginResponse.builder()
                .sessionCookies(List.of("SESSIONID=abc123; HttpOnly", "OTHERCOOKIE=xyz789; Secure"))
                .build();

        when(authService.login("testuser", "password123")).thenReturn(mockLoginResponse);

        mockMvc.perform(post("/user/login")
                        .header("API-Key", API_KEY)
                        .header("API-Secret", API_SECRET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessProtectedEndpointWithInvalidApiKey_thenUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUser("testuser");
        loginRequest.setPassword("password123");

        String loginJson = objectMapper.writeValueAsString(loginRequest);

        when(authService.login("testuser", "password123")).thenThrow(new ApiKeyUnauthorizedException("Invalid API Key or Secret"));

        mockMvc.perform(post("/user/login")
                        .header("API-Key", "invalid-key")
                        .header("API-Secret", "invalid-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }
}
