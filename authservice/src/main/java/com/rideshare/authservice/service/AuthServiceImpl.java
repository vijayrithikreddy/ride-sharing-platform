package com.rideshare.authservice.service;

import com.rideshare.authservice.dto.LoginRequestDto;
import com.rideshare.authservice.dto.LoginResponseDto;
import com.rideshare.authservice.dto.SignUpRequestDto;
import com.rideshare.authservice.dto.SignUpResponseDto;
import com.rideshare.authservice.entity.AuthUser;
import com.rideshare.authservice.exception.InvalidCredentialsException;
import com.rideshare.authservice.exception.UserAlreadyExistsException;
import com.rideshare.authservice.exception.UserNotFoundException;
import com.rideshare.authservice.repository.AuthRepository;
import com.rideshare.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final AuthRepository authRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {

        if(authRepository.existsByEmail(signUpRequestDto.getEmail()))
            throw new UserAlreadyExistsException("User Already Exists. Please Login");

        //new user
        AuthUser user = modelMapper.map(signUpRequestDto,AuthUser.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        AuthUser savedUser = authRepository.save(user);

        return modelMapper.map(savedUser,SignUpResponseDto.class);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        AuthUser user = authRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User doesn't exist. Please sign up."));
        if (passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())){
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            return LoginResponseDto.builder()
                                   .accessToken(accessToken)
                                   .refreshToken(refreshToken)
                                   .userResponse(modelMapper.map(user,SignUpResponseDto.class))
                                   .build();
        } else {
            throw new InvalidCredentialsException("Invalid Email or Password");
        }
    }
}
