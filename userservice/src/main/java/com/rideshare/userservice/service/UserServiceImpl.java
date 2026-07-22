package com.rideshare.userservice.service;


import com.rideshare.userservice.dto.CreateEmptyProfileRequestDto;
import com.rideshare.userservice.dto.UpdateUserProfileRequestDto;
import com.rideshare.userservice.dto.UserProfileResponseDto;
import com.rideshare.userservice.entity.UserProfile;
import com.rideshare.userservice.exception.ProfileAlreadyCreatedException;
import com.rideshare.userservice.exception.UserProfileNotFoundException;
import com.rideshare.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

}
