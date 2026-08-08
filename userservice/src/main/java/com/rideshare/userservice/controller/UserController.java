package com.rideshare.userservice.controller;

import com.rideshare.userservice.dto.*;
import com.rideshare.userservice.service.ImageService;
import com.rideshare.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userprofiles")
public class UserController {
    private final UserService userService;
    private final ImageService imageService;

    @PostMapping
    ResponseEntity<String> createUserProfile(@RequestBody CreateEmptyProfileRequestDto createEmptyProfileRequestDto){
        return ResponseEntity.ok(userService.createEmptyUserProfile(createEmptyProfileRequestDto));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponseDto> updateUserProfile(
            @RequestHeader("X-User-Id") UUID authUserId,
            @RequestBody @Valid UpdateUserProfileRequestDto request) {

        return ResponseEntity.ok(
                userService.updateUserProfile(request, authUserId)
        );
    }

    @GetMapping("/{id}")
    public  ResponseEntity<UserProfileResponseDto> getMyProfile(@PathVariable UUID id){
        return ResponseEntity.ok(userService.getUserProfileById(id));
    }
    @PostMapping("/summaries")
    public ResponseEntity<List<UserSummaryDto>> getUserSummaries(
            @RequestBody List<UUID> authUserIds) {

        return ResponseEntity.ok(userService.getUserSummaries(authUserIds));
    }
    @PostMapping(value = "/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> upload(@RequestParam MultipartFile image){
        ImageUploadResponse response = imageService.uploadProfilePicture(image);
        return ResponseEntity.ok(response);
    }
    @PatchMapping("/mode")
    public ResponseEntity<UserProfileResponseDto> updateUserMode(
            @RequestHeader("X-User-Id") UUID authUserId,
            @RequestBody @Valid UpdateUserModeRequestDto request) {

        return ResponseEntity.ok(userService.updateUserMode(authUserId, request));
    }

    @GetMapping("/profile-status")
    public ResponseEntity<Boolean> getProfileStatus(@RequestHeader("X-User-Id") UUID authUserId){
        return ResponseEntity.ok(userService.getProfileStatus(authUserId));
    }
    @GetMapping("/{authUserId}/passenger-profile")
    public ResponseEntity<PassengerProfileDto> getPassengerProfile(
            @PathVariable UUID authUserId) {

        return ResponseEntity.ok(
                userService.getPassengerProfile(authUserId)
        );
    }
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getProfile(@RequestHeader("X-User-Id") UUID authUserId) {
        return ResponseEntity.ok(userService.getProfile(authUserId));
    }
}
