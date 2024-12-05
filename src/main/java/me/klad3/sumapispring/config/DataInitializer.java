package me.klad3.sumapispring.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.klad3.sumapispring.model.User;
import me.klad3.sumapispring.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository apiClientRepository;

    @PostConstruct
    public void init() {
        if (apiClientRepository.findByApiKey("your-api-key").isEmpty()) {
            User client = new User();
            client.setUsername("your-username");
            client.setEmail("your-email");
            client.setInstitutionId("your-institution-id");
            client.setApiKey("your-api-key");
            client.setApiSecret("your-api-secret");
            apiClientRepository.save(client);
        }
    }
}
