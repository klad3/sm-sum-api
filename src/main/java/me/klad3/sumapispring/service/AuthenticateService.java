package me.klad3.sumapispring.service;

import lombok.RequiredArgsConstructor;
import me.klad3.sumapispring.model.User;
import me.klad3.sumapispring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticateService {

    private final UserRepository userRepository;

    public Optional<User> findByApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey);
    }
}
