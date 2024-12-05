package me.klad3.sumapispring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.klad3.sumapispring.dto.ApiResponse;
import me.klad3.sumapispring.dto.CreateUserRequest;
import me.klad3.sumapispring.dto.CreateUserResponse;
import me.klad3.sumapispring.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        ApiResponse<CreateUserResponse> apiResponse = ApiResponse.success(response.getMessage(), response);
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}
