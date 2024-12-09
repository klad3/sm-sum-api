package me.klad3.sumapispring.repository;

import me.klad3.sumapispring.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Guardar y encontrar un usuario por ID")
    void saveAndFindById() {
        User user = User.builder()
                .username("johndoe")
                .email("john@example.com")
                .institutionId("INST123")
                .apiKey("APIKEY123")
                .apiSecretHash("SECRET123")
                .studentName("John Doe")
                .build();

        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("johndoe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Encontrar usuario por apiKey")
    void findByApiKey() {
        User user = User.builder()
                .username("janedoe")
                .email("jane@example.com")
                .institutionId("INST456")
                .apiKey("APIKEY456")
                .apiSecretHash("SECRET456")
                .studentName("Jane Doe")
                .build();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByApiKey("APIKEY456");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("janedoe");
    }

    @Test
    @DisplayName("Verificar existencia de usuario por username")
    void existsByUsername() {
        User user = User.builder()
                .username("alice")
                .email("alice@example.com")
                .institutionId("INST789")
                .apiKey("APIKEY789")
                .apiSecretHash("SECRET789")
                .studentName("Alice Wonderland")
                .build();
        userRepository.save(user);

        boolean exists = userRepository.existsByUsername("alice");
        boolean notExists = userRepository.existsByUsername("bob");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Verificar existencia de usuario por email")
    void existsByEmail() {
        User user = User.builder()
                .username("charlie")
                .email("charlie@example.com")
                .institutionId("INST101")
                .apiKey("APIKEY101")
                .apiSecretHash("SECRET101")
                .studentName("Charlie Brown")
                .build();
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("charlie@example.com");
        boolean notExists = userRepository.existsByEmail("dave@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Verificar existencia de usuario por institutionId")
    void existsByInstitutionId() {
        User user = User.builder()
                .username("eve")
                .email("eve@example.com")
                .institutionId("INST202")
                .apiKey("APIKEY202")
                .apiSecretHash("SECRET202")
                .studentName("Eve Online")
                .build();
        userRepository.save(user);

        boolean exists = userRepository.existsByInstitutionId("INST202");
        boolean notExists = userRepository.existsByInstitutionId("INST303");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Actualizar un usuario existente")
    void updateUser() {
        User user = User.builder()
                .username("frank")
                .email("frank@example.com")
                .institutionId("INST404")
                .apiKey("APIKEY404")
                .apiSecretHash("SECRET404")
                .studentName("Frank Sinatra")
                .build();
        User savedUser = userRepository.save(user);

        savedUser.setEmail("frank.new@example.com");
        User updatedUser = userRepository.save(savedUser);
        Optional<User> foundUser = userRepository.findById(updatedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("frank.new@example.com");
    }

    @Test
    @DisplayName("Eliminar un usuario por ID")
    void deleteById() {
        User user = User.builder()
                .username("grace")
                .email("grace@example.com")
                .institutionId("INST505")
                .apiKey("APIKEY505")
                .apiSecretHash("SECRET505")
                .studentName("Grace Hopper")
                .build();
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        userRepository.deleteById(userId);
        Optional<User> foundUser = userRepository.findById(userId);

        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("Buscar usuario por apiKey inexistente")
    void findByApiKey_NotFound() {
        Optional<User> foundUser = userRepository.findByApiKey("NONEXISTENT_APIKEY");

        assertThat(foundUser).isNotPresent();
    }
}
