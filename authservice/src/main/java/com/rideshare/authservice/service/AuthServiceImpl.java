package com.rideshare.authservice.service;

import com.rideshare.authservice.dto.SignUpRequestDto;
import com.rideshare.authservice.dto.SignUpResponseDto;
import com.rideshare.authservice.entity.AuthUser;
import com.rideshare.authservice.exception.UserAlreadyExistsException;
import com.rideshare.authservice.repository.AuthRepository;
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
}
