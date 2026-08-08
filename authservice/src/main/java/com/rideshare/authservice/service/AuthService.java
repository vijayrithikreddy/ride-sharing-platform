package com.rideshare.authservice.service;

import com.rideshare.authservice.dto.*;

public interface AuthService {
    String signUp(SignUpRequestDto signUpRequestDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    SignUpResponseDto verifyUser(String email,String otp);
    RefreshTokenResponseDto refreshAccessToken(RefreshTokenRequestDto request);
}
