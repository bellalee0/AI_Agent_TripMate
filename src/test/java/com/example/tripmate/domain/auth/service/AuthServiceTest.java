package com.example.tripmate.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tripmate.common.entity.RefreshToken;
import com.example.tripmate.common.entity.User;
import com.example.tripmate.common.enums.UserRole;
import com.example.tripmate.common.exception.CustomException;
import com.example.tripmate.common.utils.JwtUtil;
import com.example.tripmate.domain.auth.dto.request.AuthLoginRequest;
import com.example.tripmate.domain.auth.dto.request.AuthSignupRequest;
import com.example.tripmate.domain.auth.dto.response.AuthTokenResponse;
import com.example.tripmate.domain.auth.repository.RefreshTokenRepository;
import com.example.tripmate.domain.user.repository.UserRepository;
import com.example.tripmate.fixture.UserFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 정상 처리")
    void signup_success() {

        // Given
        String name = "test";
        String nickname = "testUser";
        String email = "test@test.com";
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        AuthSignupRequest request = new AuthSignupRequest(
            name,
            nickname,
            email,
            rawPassword
        );

        User user = new User(
            email,
            name,
            nickname,
            encodedPassword,
            null,
            UserRole.USER
        );
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(anyLong())).thenReturn(refreshToken);
        when(refreshTokenRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // When
        authService.signup(request);

        // Then
        verify(userRepository).saveAndFlush(any(User.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일")
    void signup_failure_duplicateEmail() {

        // Given
        String name = "test";
        String nickname = "testUser";
        String email = "test@test.com";
        String rawPassword = "rawPassword";

        AuthSignupRequest request = new AuthSignupRequest(
            name,
            nickname,
            email,
            rawPassword
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signup(request));

        // Then
        assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 닉네임")
    void signup_failure_duplicateNickname() {

        // Given
        String name = "test";
        String nickname = "testUser";
        String email = "test@test.com";
        String rawPassword = "rawPassword";

        AuthSignupRequest request = new AuthSignupRequest(
            name,
            nickname,
            email,
            rawPassword
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNickname(anyString())).thenReturn(true);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.signup(request));

        // Then
        assertEquals("이미 사용 중인 닉네임입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("로그인 정상 처리")
    void login_success() {

        // Given
        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String email = "test@test.com";
        String rawPassword = "rawPassword";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        AuthLoginRequest request = new AuthLoginRequest(
            email,
            rawPassword
        );

        when(userRepository.findActivateUserByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(anyLong())).thenReturn(refreshToken);
        when(refreshTokenRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // When
        AuthTokenResponse response = authService.login(request);

        // Then
        assertThat(response.getToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_failure_wrongPassword() {

        // Given
        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String email = "test@test.com";
        String rawPassword = "rawPassword";

        AuthLoginRequest request = new AuthLoginRequest(
            email,
            rawPassword
        );

        when(userRepository.findActivateUserByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.login(request));

        // Then
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("Refresh 토큰 재발급 정상 처리")
    void generateToken_success() {

        // Given
        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String email = "test@test.com";
        String rawPassword = "rawPassword";

        AuthLoginRequest request = new AuthLoginRequest(
            email,
            rawPassword
        );

        String accessToken = "accessToken";
        String oldRefreshToken = "oldRefreshToken";
        String newRefreshToken = "newRefreshToken";

        RefreshToken refreshToken = new RefreshToken(user, oldRefreshToken);

        when(userRepository.findActivateUserByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(anyLong())).thenReturn(newRefreshToken);
        when(refreshTokenRepository.findByUserId(anyLong())).thenReturn(Optional.of(refreshToken));

        // When
        AuthTokenResponse response = authService.login(request);

        // Then
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("액세스 토큰 재발급 정상 처리")
    void reissueToken_success() {

        // Given
        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        RefreshToken userRefreshToken = new RefreshToken(user, refreshToken);
        ReflectionTestUtils.setField(userRefreshToken, "id", 1L);

        when(jwtUtil.isExpired(anyString())).thenReturn(false);
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        when(refreshTokenRepository.findUserRefreshByUserId(anyLong())).thenReturn(userRefreshToken);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.expireInTwoDays(anyString())).thenReturn(false);

        // When
        AuthTokenResponse response = authService.reissueToken(refreshToken);

        // Then
        assertThat(response.getToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("액세스 토큰 재발급 정상 처리 - RefreshToken 재발급")
    void reissueToken_success_updateRefreshToken() {

        // Given
        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String accessToken = "accessToken";
        String oldRefreshToken = "oldRefreshToken";
        String newRefreshToken = "newRefreshToken";

        RefreshToken userRefreshToken = new RefreshToken(user, oldRefreshToken);
        ReflectionTestUtils.setField(userRefreshToken, "id", 1L);

        when(jwtUtil.isExpired(anyString())).thenReturn(false);
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        when(jwtUtil.extractUserId(anyString())).thenReturn(1L);
        when(refreshTokenRepository.findUserRefreshByUserId(anyLong())).thenReturn(userRefreshToken);
        when(jwtUtil.generateAccessToken(anyLong(), anyString(), any(UserRole.class))).thenReturn(accessToken);
        when(jwtUtil.expireInTwoDays(anyString())).thenReturn(true);
        when(jwtUtil.generateRefreshToken(anyLong())).thenReturn(newRefreshToken);

        // When
        AuthTokenResponse response = authService.reissueToken(oldRefreshToken);

        // Then
        assertThat(response.getToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("액세스 토큰 재발급 실패 - 만료된 Refresh 토큰")
    void reissueToken_failure_expiredRefreshToken() {

        // Given
        String refreshToken = "refreshToken";

        when(jwtUtil.isExpired(anyString())).thenReturn(true);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.reissueToken(refreshToken));

        // Then
        assertEquals("유효 기간이 만료된 토큰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("액세스 토큰 재발급 실패 - 유효하지 않은 Refresh 토큰")
    void reissueToken_failure_notValidatedRefreshToken() {

        // Given
        String refreshToken = "refreshToken";

        when(jwtUtil.isExpired(anyString())).thenReturn(false);
        when(jwtUtil.validateToken(anyString())).thenReturn(false);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.reissueToken(refreshToken));

        // Then
        assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("액세스 토큰 재발급 실패 - 잘못된 Refresh 토큰")
    void reissueToken_failure_wrongRefreshToken() {

        // Given
        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String rightRefreshToken = "rightRefreshToken";
        String wrongRefreshToken = "wrongRefreshToken";

        RefreshToken userRefreshToken = new RefreshToken(user, rightRefreshToken);
        ReflectionTestUtils.setField(userRefreshToken, "id", 1L);

        when(jwtUtil.isExpired(anyString())).thenReturn(false);
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(refreshTokenRepository.findUserRefreshByUserId(anyLong())).thenReturn(userRefreshToken);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> authService.reissueToken(wrongRefreshToken));

        // Then
        assertEquals("유효하지 않은 토큰입니다.", exception.getMessage());
    }
}