// src/main/java/me/klad3/sumapispring/dto/CreateUserResponse.java

package me.klad3.sumapispring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateUserResponse {
    private String username;
    private String email;
    private String institutionId;
    private String studentName;
    private String apiKey;
    private String apiSecret;
    private String message;
}
