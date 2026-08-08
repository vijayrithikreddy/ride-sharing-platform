package com.rideshare.authservice.controller;

import com.rideshare.authservice.dto.*;
import com.rideshare.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/signup")
    ResponseEntity<String> registerUser(@RequestBody SignUpRequestDto signUpRequestDto){
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.signUp(signUpRequestDto));
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.login(loginRequestDto));
    }
    @PostMapping("/verifyOtp")
    ResponseEntity<SignUpResponseDto> verifyUser(@RequestParam String email, String otp){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.verifyUser(email,otp));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(
            @RequestBody @Valid RefreshTokenRequestDto request) {

        return ResponseEntity.ok(authService.refreshAccessToken(request));

    }
}
