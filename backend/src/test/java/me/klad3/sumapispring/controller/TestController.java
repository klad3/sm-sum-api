package me.klad3.sumapispring.controller;

import me.klad3.sumapispring.exception.AuthenticationException;
import me.klad3.sumapispring.exception.BadRequestException;
import me.klad3.sumapispring.exception.ExternalApiException;
import me.klad3.sumapispring.exception.ResourceAlreadyExistsException;
import me.klad3.sumapispring.exception.ResourceNotFoundException;
import me.klad3.sumapispring.exception.ApiKeyUnauthorizedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/validation")
    public void triggerValidationException(
            @RequestParam String username,
            @RequestParam String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is mandatory");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is mandatory");
        }
    }

    @GetMapping("/authentication")
    public void triggerAuthenticationException() {
        throw new AuthenticationException("Invalid credentials");
    }

    @GetMapping("/resource-not-found")
    public void triggerResourceNotFoundException() {
        throw new ResourceNotFoundException("User not found");
    }

    @GetMapping("/bad-request")
    public void triggerBadRequestException() {
        throw new BadRequestException("Invalid input");
    }

    @GetMapping("/external-api-error")
    public void triggerExternalApiException() {
        throw new ExternalApiException("External API call failed");
    }

    @GetMapping("/resource-already-exists")
    public void triggerResourceAlreadyExistsException() {
        throw new ResourceAlreadyExistsException("Resource already exists");
    }

    @GetMapping("/api-key-unauthorized")
    public void triggerApiKeyUnauthorizedException() {
        throw new ApiKeyUnauthorizedException("API Key is invalid");
    }

    @GetMapping("/general-error")
    public void triggerGeneralException() {
        throw new RuntimeException("General error");
    }
}
