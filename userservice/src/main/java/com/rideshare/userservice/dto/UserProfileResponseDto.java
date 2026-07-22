package com.rideshare.userservice.dto;

import com.rideshare.userservice.enums.Gender;
import com.rideshare.userservice.enums.Occupation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDto {

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private LocalDate dateOfBirth;

    private Gender gender;

    private Occupation occupation;

    private String organization;

    private String profilePictureUrl;

    private String bio;

    private boolean profileCompleted;
}