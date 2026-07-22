package com.rideshare.authservice.model;

import com.rideshare.authservice.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingUser implements Serializable  {
    @NotBlank(message = "Email is empty")
    private String email;

    @NotBlank(message = "Password is empty")
    private String password;
    private Role role;
    private String otp;

}
