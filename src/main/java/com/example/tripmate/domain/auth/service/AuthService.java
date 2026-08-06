package com.example.tripmate.domain.auth.service;

import com.example.tripmate.common.entity.RefreshToken;
import com.example.tripmate.common.entity.User;
import com.example.tripmate.common.enums.UserRole;
import com.example.tripmate.common.exception.CustomException;
import com.example.tripmate.common.exception.ErrorCode;
import com.example.tripmate.common.utils.JwtUtil;
import com.example.tripmate.domain.auth.dto.request.AuthLoginRequest;
import com.example.tripmate.domain.auth.dto.request.AuthSignupRequest;
import com.example.tripmate.domain.auth.dto.response.AuthTokenResponse;
import com.example.tripmate.domain.auth.repository.RefreshTokenRepository;
import com.example.tripmate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    @Transactional
    public AuthTokenResponse signup(AuthSignupRequest request) {

        String email = request.getEmail();
        String nickname = request.getNickname();

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_EXIST);
        }

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_EXIST);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(email, request.getName(), nickname, encodedPassword, null, UserRole.USER);
        userRepository.saveAndFlush(user);

        AuthTokenResponse authTokenResponse = generateToken(user);

        return authTokenResponse;
    }

    /**
     * 로그인
     */
    @Transactional
    public AuthTokenResponse login(AuthLoginRequest request) {

        User user = userRepository.findActivateUserByEmail(request.getEmail());

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!matches) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        return generateToken(user);
    }

    /**
     * 액세스 토큰 재발급
     */
    @Transactional
    public AuthTokenResponse reissueToken(String refreshToken) {

        if (jwtUtil.isExpired(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtUtil.extractUserId(refreshToken);

        RefreshToken userRefresh = refreshTokenRepository.findUserRefreshByUserId(userId);

        if (!ObjectUtils.nullSafeEquals(refreshToken, userRefresh.getToken())) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRefresh.getUser();

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole());

        if (jwtUtil.expireInTwoDays(refreshToken)) {
            refreshToken = jwtUtil.generateRefreshToken(userId);
            userRefresh.updateRefreshToken(refreshToken);
        }

        return new AuthTokenResponse(accessToken, refreshToken);
    }

    /**
     * 유저의 Access 토큰, Refresh 토큰 생성
     */
    private AuthTokenResponse generateToken(User user) {

        Long userId = user.getId();

        String accessToken = jwtUtil.generateAccessToken(userId, user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(userId);

        RefreshToken userRefresh = refreshTokenRepository.findByUserId(userId)
            .map(ur -> {
                ur.updateRefreshToken(refreshToken);
                return ur;
            })
            .orElseGet(() -> new RefreshToken(user, refreshToken));

        refreshTokenRepository.save(userRefresh);

        return new AuthTokenResponse(accessToken, refreshToken);
    }
}
