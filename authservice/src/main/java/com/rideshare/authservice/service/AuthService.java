package com.rideshare.authservice.service;

import com.rideshare.authservice.dto.LoginRequestDto;
import com.rideshare.authservice.dto.LoginResponseDto;
import com.rideshare.authservice.dto.SignUpRequestDto;
import com.rideshare.authservice.dto.SignUpResponseDto;

public interface AuthService {
    SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
}
