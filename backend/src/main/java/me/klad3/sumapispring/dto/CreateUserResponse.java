package me.klad3.sumapispring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
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
