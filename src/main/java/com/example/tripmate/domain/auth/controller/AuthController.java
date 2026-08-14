package com.example.tripmate.domain.auth.controller;

import static com.example.tripmate.common.enums.SuccessMessage.AUTH_LOGIN_SUCCESS;
import static com.example.tripmate.common.enums.SuccessMessage.AUTH_LOGOUT_SUCCESS;
import static com.example.tripmate.common.enums.SuccessMessage.AUTH_REISSUE_SUCCESS;
import static com.example.tripmate.common.enums.SuccessMessage.AUTH_SIGNUP_SUCCESS;

import com.example.tripmate.common.dto.CommonResponse;
import com.example.tripmate.domain.auth.dto.request.AuthLoginRequest;
import com.example.tripmate.domain.auth.dto.request.AuthSignupRequest;
import com.example.tripmate.domain.auth.dto.response.AuthTokenResponse;
import com.example.tripmate.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     */
    @Operation(
        summary = "회원가입",
        description = """
                    필요한 정보들을 입력하여 새로운 사용자를 생성합니다.
                    """
    )
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Void>> signup(
        @Valid @RequestBody AuthSignupRequest request,
        HttpServletResponse response
    ) {
        AuthTokenResponse authTokenResponse = authService.signup(request);

        addCookies(response, authTokenResponse);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CommonResponse.successNodata(AUTH_SIGNUP_SUCCESS));
    }

    /**
     * 로그인
     */
    @Operation(
        summary = "로그인",
        description = """
                    등록된 이메일과 비밀번호를 입력하여 토큰을 발급받습니다.
                    
                     - 토큰 만료 : 1시간
                    """
    )
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Void>> login(
        @Valid @RequestBody AuthLoginRequest request,
        HttpServletResponse response
    ) {
        AuthTokenResponse authTokenResponse = authService.login(request);

        addCookies(response, authTokenResponse);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.successNodata(AUTH_LOGIN_SUCCESS));
    }

    /**
     * 액세스 토큰 재발급
     */
    @Operation(
        summary = "액세스 토큰 재발급",
        description = "Refresh 토큰을 기반으로 Access 토큰을 재발급합니다."
    )
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<Void>> reissueToken(
        @CookieValue(name = "refreshToken") String refreshToken,
        HttpServletResponse response
    ) {
        AuthTokenResponse authTokenResponse = authService.reissueToken(refreshToken);

        addCookies(response, authTokenResponse);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.successNodata(AUTH_REISSUE_SUCCESS));
    }

    /**
     * 로그아웃 (쿠키 삭제)
     */
    @Operation(
        summary = "로그아웃",
        description = "쿠키를 만료시켜 로그아웃 처리합니다."
    )
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(HttpServletResponse response) {

        ResponseCookie accessTokenCookie = createCookie("accessToken", "", 0);
        ResponseCookie refreshTokenCookie = createCookie("refreshToken", "", 0);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(CommonResponse.successNodata(AUTH_LOGOUT_SUCCESS));
    }

    /**
     * 쿠키 생성 후 response에 저장
     */
    private void addCookies(HttpServletResponse response, AuthTokenResponse tokenResponse) {

        ResponseCookie accessTokenCookie = createCookie("accessToken", tokenResponse.getToken(), 3600);
        ResponseCookie refreshTokenCookie = createCookie("refreshToken", tokenResponse.getRefreshToken(), 604800);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    /**
     * 쿠키 생성
     */
    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
            .path("/")
            .httpOnly(true)
            .secure(true)
            .maxAge(maxAge)
            .sameSite("None")
            .build();
    }
}
