package com.rideshare.userservice.service;


import com.rideshare.userservice.dto.*;
import com.rideshare.userservice.entity.UserProfile;
import com.rideshare.userservice.exception.ProfileAlreadyCreatedException;
import com.rideshare.userservice.exception.UserProfileNotFoundException;
import com.rideshare.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper;


    @Override
    public String createEmptyUserProfile(CreateEmptyProfileRequestDto userProfileRequestDto) {
        log.info("{}" , userProfileRequestDto.getAuthUserId());
        if (userProfileRepository.existsByAuthUserId(userProfileRequestDto.getAuthUserId()))
            throw new ProfileAlreadyCreatedException("Profile already Created.");
        UserProfile userProfile = new UserProfile();
        userProfile.setAuthUserId(userProfileRequestDto.getAuthUserId());
        userProfileRepository.save(userProfile);
        return "Empty Profile Created";
    }

    @Override
    public UserProfileResponseDto updateUserProfile(
            UpdateUserProfileRequestDto request,
            UUID authUserId) {

        UserProfile userProfile = userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User profile not found"));

        modelMapper.map(request, userProfile);

        userProfile.setProfileCompleted(
                userProfile.getFirstName() != null &&
                        userProfile.getPhoneNumber() != null &&
                        userProfile.getOccupation() != null
        );
        UserProfile updatedProfile = userProfileRepository.save(userProfile);

        return modelMapper.map(updatedProfile, UserProfileResponseDto.class);
    }

    @Override
    public UserProfileResponseDto getUserProfileById(UUID id) {
        UserProfile userProfile = userProfileRepository.findByAuthUserId(id).orElseThrow(() -> new UserProfileNotFoundException("Profile Not found"));
        return modelMapper.map(userProfile,UserProfileResponseDto.class);
    }
    @Override
    public List<UserSummaryDto> getUserSummaries(List<UUID> authUserIds) {

        List<UserProfile> userProfiles =
                userProfileRepository.findByAuthUserIdIn(authUserIds);

        return userProfiles.stream()
                .map(profile -> {

                    UserSummaryDto dto =
                            modelMapper.map(profile, UserSummaryDto.class);

                    if (profile.getVehicle() != null) {

                        dto.setVehicle(
                                modelMapper.map(profile.getVehicle(), VehicleSummaryDto.class));

                    }

                    return dto;

                })
                .toList();
    }
    @Override
    public UserProfileResponseDto updateUserMode(
            UUID authUserId,
            UpdateUserModeRequestDto request) {

        UserProfile profile = userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("Profile not found"));

        profile.setUserMode(request.getUserMode());

        UserProfile saved = userProfileRepository.save(profile);

        return modelMapper.map(saved, UserProfileResponseDto.class);
    }

    public boolean getProfileStatus(UUID authUserId) {

        UserProfile profile = userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("Profile not found"));

        return profile.isProfileCompleted();
    }
    @Override
    public PassengerProfileDto getPassengerProfile(UUID authUserId) {

        UserProfile profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found"));

        return modelMapper.map(profile, PassengerProfileDto.class);
    }

}
