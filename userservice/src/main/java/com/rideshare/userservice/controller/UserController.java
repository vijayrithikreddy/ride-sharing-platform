package com.rideshare.userservice.controller;

import com.rideshare.userservice.dto.CreateEmptyProfileRequestDto;
import com.rideshare.userservice.dto.UpdateUserProfileRequestDto;
import com.rideshare.userservice.dto.UserProfileResponseDto;
import com.rideshare.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userprofiles")
public class UserController {
    private final UserService userService;

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
}
