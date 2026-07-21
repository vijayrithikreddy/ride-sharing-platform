package com.rideshare.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "Email cannot be empty")
    private String email;
    @NotBlank(message = "Password is Empty")
    private String password;
}
