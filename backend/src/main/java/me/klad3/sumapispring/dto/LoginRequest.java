package me.klad3.sumapispring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username is mandatory")
    private String user;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
