package com.rideshare.authservice.dto;

import com.rideshare.authservice.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SignUpResponseDto {
    private UUID id;
    private String email;
    private String password;
    private Role role;
    private boolean enabled;
    private boolean accountLocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
