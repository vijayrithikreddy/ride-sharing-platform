package com.rideshare.authservice.service;

import com.rideshare.authservice.dto.*;
import com.rideshare.authservice.entity.AuthUser;
import com.rideshare.authservice.event.OtpEventPublisher;
import com.rideshare.authservice.event.OtpNotificationEvent;
import com.rideshare.authservice.exception.*;
import com.rideshare.authservice.feign.UserProfileClient;
import com.rideshare.authservice.model.PendingUser;
import com.rideshare.authservice.repository.AuthRepository;
import com.rideshare.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final AuthRepository authRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String,PendingUser> redisTemplate;
    private final UserProfileClient userProfileClient;
    private final OtpEventPublisher otpEventPublisher;

    @Override
    public String signUp(SignUpRequestDto signUpRequestDto) {

        if(authRepository.existsByEmail(signUpRequestDto.getEmail()))
            throw new UserAlreadyExistsException("User Already Exists. Please Login");

        PendingUser pendingUser =  redisTemplate.opsForValue().get("signup:" + signUpRequestDto.getEmail());
        if (pendingUser != null)
            throw new OtpAlreadySentException("Otp already sent. Please verify first");

        //new user
        PendingUser user = modelMapper.map(signUpRequestDto, PendingUser.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setOtp(generateOTP(6));

        redisTemplate.opsForValue().set("signup:" + user.getEmail(),user, Duration.ofMinutes(10));
        pendingUser =  redisTemplate.opsForValue().get("signup:" + signUpRequestDto.getEmail());
        log.info("{}: " + pendingUser);
        OtpNotificationEvent event =
                OtpNotificationEvent.builder()
                        .email(user.getEmail())
                        .otp(user.getOtp())
                        .build();

        otpEventPublisher.publishOtp(event);

        return "Otp Sent";
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

    @Override
    public SignUpResponseDto verifyUser(String email,String otp) {
        PendingUser pendingUser =  redisTemplate.opsForValue().get("signup:" + email);
        if(pendingUser == null)
            throw new OtpExpiredException("Otp has Expired .Please Register");
        if (pendingUser.getOtp().equals(otp)){
            AuthUser user = modelMapper.map(pendingUser, AuthUser.class);
            AuthUser savedUser = authRepository.save(user);//return the id too
            CreateEmptyProfileRequestDto createEmptyProfileRequestDto = new CreateEmptyProfileRequestDto();
            createEmptyProfileRequestDto.setAuthUserId(savedUser.getId());
            redisTemplate.delete("signup:" + email);
            userProfileClient.createEmptyUserProfile(createEmptyProfileRequestDto);
            return modelMapper.map(savedUser,SignUpResponseDto.class);
        } else {
            throw new InvalidOtpException("Otp Invalid . Please Enter Correct Otp");
        }
    }

    public String generateOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Generates 0-9
        }

        return otp.toString();
    }
    @Override
    public RefreshTokenResponseDto refreshAccessToken(
            RefreshTokenRequestDto request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {

            throw new InvalidTokenException(
                    "Refresh token has expired."
            );

        }

        String email =
                jwtUtil.retrieveEmailFromToken(refreshToken);

        AuthUser user =
                authRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "User not found."
                                ));

        String newAccessToken =
                jwtUtil.generateAccessToken(user);

        return RefreshTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .build();

    }
}
