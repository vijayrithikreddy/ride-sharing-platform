package com.rideshare.authservice.dto;

import com.rideshare.authservice.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank(message = "Email is empty")
    private String email;

    @NotBlank(message = "Password is empty")
    private String password;
    private Role role;
}
