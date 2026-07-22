package com.rideshare.authservice.service;

import com.rideshare.authservice.dto.*;
import com.rideshare.authservice.entity.AuthUser;
import com.rideshare.authservice.exception.*;
import com.rideshare.authservice.model.PendingUser;
import com.rideshare.authservice.repository.AuthRepository;
import com.rideshare.authservice.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthServiceImpl Test Suite")
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisTemplate<String, PendingUser> redisTemplate;

    @Mock
    private ValueOperations<String, PendingUser> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    private SignUpRequestDto signUpRequestDto;
    private LoginRequestDto loginRequestDto;
    private PendingUser pendingUser;
    private AuthUser authUser;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Starting AuthService Tests...");
    }

    @BeforeEach
    void setUp() {

        signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setEmail("test@gmail.com");
        signUpRequestDto.setPassword("password");

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@gmail.com");
        loginRequestDto.setPassword("password");

        pendingUser = new PendingUser();
        pendingUser.setEmail("test@gmail.com");
        pendingUser.setPassword("encodedPassword");
        pendingUser.setOtp("123456");

        authUser = new AuthUser();
        authUser.setEmail("test@gmail.com");
        authUser.setPassword("encodedPassword");
    }

    @AfterEach
    void afterEach() {
        System.out.println("Test Completed");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("All Tests Finished");
    }

    // ========================== SIGN UP ==========================

    @Test
    @DisplayName("Should return otp sent when user registers successfully")
    void shouldReturnOtpSentWhenUserRegistersSuccessfully() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(authRepository.existsByEmail(signUpRequestDto.getEmail())).thenReturn(false);

        when(valueOperations.get(anyString()))
                .thenReturn(null)
                .thenReturn(pendingUser);

        when(modelMapper.map(signUpRequestDto, PendingUser.class)).thenReturn(pendingUser);

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        String result = authService.signUp(signUpRequestDto);

        assertEquals("Otp Sent", result);

        verify(valueOperations).set(
                eq("signup:test@gmail.com"),
                any(PendingUser.class),
                eq(Duration.ofMinutes(10))
        );
    }

    @Test
    @DisplayName("Should throw user already exists exception when email already exists")
    void shouldThrowUserAlreadyExistsExceptionWhenEmailAlreadyExists() {

        when(authRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.signUp(signUpRequestDto));

        verify(authRepository).existsByEmail(signUpRequestDto.getEmail());
    }

    @Test
    @DisplayName("Should throw otp already sent exception when otp already exists")
    void shouldThrowOtpAlreadySentExceptionWhenOtpAlreadyExists() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(authRepository.existsByEmail(anyString())).thenReturn(false);

        when(valueOperations.get(anyString())).thenReturn(pendingUser);

        assertThrows(OtpAlreadySentException.class,
                () -> authService.signUp(signUpRequestDto));
    }

    // ========================== LOGIN ==========================

    @Test
    @DisplayName("Should return login response when credentials are valid")
    void shouldReturnLoginResponseWhenCredentialsAreValid() {

        SignUpResponseDto responseDto = new SignUpResponseDto();

        when(authRepository.findByEmail(loginRequestDto.getEmail()))
                .thenReturn(Optional.of(authUser));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        when(jwtUtil.generateAccessToken(authUser))
                .thenReturn("access-token");

        when(jwtUtil.generateRefreshToken(authUser))
                .thenReturn("refresh-token");

        when(modelMapper.map(authUser, SignUpResponseDto.class))
                .thenReturn(responseDto);

        LoginResponseDto response = authService.login(loginRequestDto);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals("access-token", response.getAccessToken()),
                () -> assertEquals("refresh-token", response.getRefreshToken()),
                () -> assertNotNull(response.getUserResponse())
        );
    }

    @Test
    @DisplayName("Should throw user not found exception when user does not exist")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {

        when(authRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authService.login(loginRequestDto));
    }

    @Test
    @DisplayName("Should throw invalid credentials exception when password is incorrect")
    void shouldThrowInvalidCredentialsExceptionWhenPasswordIsIncorrect() {

        when(authRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(authUser));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(loginRequestDto));
    }

    // ========================== VERIFY USER ==========================

    @Test
    @DisplayName("Should return user response when otp is valid")
    void shouldReturnUserResponseWhenOtpIsValid() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        SignUpResponseDto responseDto = new SignUpResponseDto();

        when(valueOperations.get(anyString()))
                .thenReturn(pendingUser);

        when(modelMapper.map(pendingUser, AuthUser.class))
                .thenReturn(authUser);

        when(authRepository.save(authUser))
                .thenReturn(authUser);

        when(modelMapper.map(authUser, SignUpResponseDto.class))
                .thenReturn(responseDto);

        SignUpResponseDto response =
                authService.verifyUser("test@gmail.com", "123456");

        assertNotNull(response);

        verify(redisTemplate).delete("signup:test@gmail.com");
    }

    @Test
    @DisplayName("Should throw otp expired exception when otp is expired")
    void shouldThrowOtpExpiredExceptionWhenOtpIsExpired() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(anyString()))
                .thenReturn(null);

        assertThrows(OtpExpiredException.class,
                () -> authService.verifyUser("test@gmail.com", "123456"));
    }

    @Test
    @DisplayName("Should throw invalid otp exception when otp is incorrect")
    void shouldThrowInvalidOtpExceptionWhenOtpIsIncorrect() {

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.get(anyString()))
                .thenReturn(pendingUser);

        assertThrows(InvalidOtpException.class,
                () -> authService.verifyUser("test@gmail.com", "999999"));
    }

    // ========================== GENERATE OTP ==========================

    @Test
    @DisplayName("Should generate numeric otp with specified length")
    void shouldGenerateNumericOtpWhenLengthIsSpecified() {

        String otp = authService.generateOTP(6);

        assertAll(
                () -> assertNotNull(otp),
                () -> assertEquals(6, otp.length()),
                () -> assertTrue(otp.matches("\\d{6}"))
        );
    }
}