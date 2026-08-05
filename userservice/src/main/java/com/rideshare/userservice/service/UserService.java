package com.rideshare.userservice.service;

import com.rideshare.userservice.dto.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    String createEmptyUserProfile(CreateEmptyProfileRequestDto userProfileRequestDto);
    UserProfileResponseDto updateUserProfile(UpdateUserProfileRequestDto request, UUID authUserId);
    UserProfileResponseDto getUserProfileById(UUID id);
    List<UserSummaryDto> getUserSummaries(List<UUID> authUserIds);
    UserProfileResponseDto updateUserMode(UUID authUserId, UpdateUserModeRequestDto request);
    boolean getProfileStatus(UUID authUserId);
    PassengerProfileDto getPassengerProfile(UUID authUserId);
}
