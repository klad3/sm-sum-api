package me.klad3.sumapispring.service;

import lombok.RequiredArgsConstructor;
import me.klad3.sumapispring.dto.CreateUserRequest;
import me.klad3.sumapispring.dto.CreateUserResponse;
import me.klad3.sumapispring.exception.ResourceAlreadyExistsException;
import me.klad3.sumapispring.model.User;
import me.klad3.sumapispring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public Optional<User> findByApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey);
    }

    public CreateUserResponse createUser(CreateUserRequest request) throws ResourceAlreadyExistsException {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }
        if (userRepository.existsByInstitutionId(request.getInstitutionId())) {
            throw new ResourceAlreadyExistsException("Institution ID already exists");
        }

        String apiKey = generateApiKey();
        String apiSecret = generateApiSecret();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .institutionId(request.getInstitutionId())
                .studentName(request.getStudentName())
                .apiKey(apiKey)
                .build();
        user.setApiSecret(apiSecret);
        userRepository.save(user);

        return new CreateUserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getInstitutionId(),
                user.getStudentName(),
                apiKey,
                apiSecret,
                "User created successfully"
        );
    }

    private String generateApiKey() {
        byte[] key = new byte[24];
        secureRandom.nextBytes(key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key);
    }

    private String generateApiSecret() {
        byte[] secret = new byte[32];
        secureRandom.nextBytes(secret);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(secret);
    }
}
