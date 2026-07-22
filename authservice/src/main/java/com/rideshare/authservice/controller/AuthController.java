package com.rideshare.authservice.controller;

import com.rideshare.authservice.dto.LoginRequestDto;
import com.rideshare.authservice.dto.LoginResponseDto;
import com.rideshare.authservice.dto.SignUpRequestDto;
import com.rideshare.authservice.dto.SignUpResponseDto;
import com.rideshare.authservice.service.AuthService;
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
}
