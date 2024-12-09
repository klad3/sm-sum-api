package me.klad3.sumapispring.service;

import me.klad3.sumapispring.dto.CreateUserRequest;
import me.klad3.sumapispring.dto.CreateUserResponse;
import me.klad3.sumapispring.exception.ResourceAlreadyExistsException;
import me.klad3.sumapispring.model.User;
import me.klad3.sumapispring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_Success() throws ResourceAlreadyExistsException {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .institutionId("INST123")
                .studentName("John Doe")
                .build();

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByInstitutionId("INST123")).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        CreateUserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("john_doe", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("INST123", response.getInstitutionId());
        assertEquals("John Doe", response.getStudentName());
        assertNotNull(response.getApiKey());
        assertNotNull(response.getApiSecret());
        assertEquals("User created successfully", response.getMessage());

        verify(userRepository, times(1)).existsByUsername("john_doe");
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, times(1)).existsByInstitutionId("INST123");
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("john_doe", savedUser.getUsername());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("INST123", savedUser.getInstitutionId());
        assertEquals("John Doe", savedUser.getStudentName());
        assertNotNull(savedUser.getApiKey());
        assertNotNull(savedUser.getApiSecretHash());
    }

    @Test
    void createUser_UsernameExists_ThrowsException() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .institutionId("INST123")
                .studentName("John Doe")
                .build();

        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.createUser(request);
        });

        assertEquals("Username already exists", exception.getMessage());

        verify(userRepository, times(1)).existsByUsername("john_doe");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).existsByInstitutionId(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailExists_ThrowsException() {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .institutionId("INST123")
                .studentName("John Doe")
                .build();

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.createUser(request);
        });

        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, times(1)).existsByUsername("john_doe");
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, never()).existsByInstitutionId(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_InstitutionIdExists_ThrowsException() {
        // Arrange
        CreateUserRequest request = CreateUserRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .institutionId("INST123")
                .studentName("John Doe")
                .build();

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByInstitutionId("INST123")).thenReturn(true);

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> {
            userService.createUser(request);
        });

        assertEquals("Institution ID already exists", exception.getMessage());

        verify(userRepository, times(1)).existsByUsername("john_doe");
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, times(1)).existsByInstitutionId("INST123");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByApiKey_UserExists_ReturnsUser() {
        String apiKey = "testApiKey";
        User user = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .institutionId("INST123")
                .studentName("John Doe")
                .apiKey(apiKey)
                .apiSecretHash("testApiSecret")
                .build();

        when(userRepository.findByApiKey(apiKey)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByApiKey(apiKey);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());

        verify(userRepository, times(1)).findByApiKey(apiKey);
    }

    @Test
    void findByApiKey_UserDoesNotExist_ReturnsEmpty() {
        String apiKey = "nonExistingApiKey";

        when(userRepository.findByApiKey(apiKey)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByApiKey(apiKey);

        assertFalse(result.isPresent());

        verify(userRepository, times(1)).findByApiKey(apiKey);
    }
}
