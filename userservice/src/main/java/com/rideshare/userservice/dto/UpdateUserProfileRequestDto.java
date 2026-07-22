package com.rideshare.userservice.dto;

import com.rideshare.userservice.enums.Gender;
import com.rideshare.userservice.enums.Occupation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileRequestDto {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number"
    )
    private String phoneNumber;

    private LocalDate dateOfBirth;

    private Gender gender;

    private Occupation occupation;

    @Size(max = 100, message = "Organization name cannot exceed 100 characters")
    private String organization;

    private String profilePictureUrl;

    @Size(max = 300, message = "Bio cannot exceed 300 characters")
    private String bio;
}