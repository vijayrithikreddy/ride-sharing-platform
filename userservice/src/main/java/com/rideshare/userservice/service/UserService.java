package com.rideshare.userservice.service;

import com.rideshare.userservice.dto.CreateEmptyProfileRequestDto;
import com.rideshare.userservice.dto.UpdateUserProfileRequestDto;
import com.rideshare.userservice.dto.UserProfileResponseDto;

import java.util.UUID;

public interface UserService {
    String createEmptyUserProfile(CreateEmptyProfileRequestDto userProfileRequestDto);
    UserProfileResponseDto updateUserProfile(UpdateUserProfileRequestDto request, UUID authUserId);
    UserProfileResponseDto getUserProfileById(UUID id);
}
