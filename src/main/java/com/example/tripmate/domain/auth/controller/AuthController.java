package com.example.tripmate.domain.auth.controller;

import static com.example.tripmate.common.enums.SuccessMessage.AUTH_SIGNUP_SUCCESS;

import com.example.tripmate.common.dto.CommonResponse;
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
     * 쿠키 생성기
     */
    private void addCookies(HttpServletResponse response, AuthTokenResponse tokenResponse) {

        String at = tokenResponse.getToken();
        String rt = tokenResponse.getRefreshToken();

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", at)
            .path("/")
            .httpOnly(true)
            .secure(true)
            .maxAge(3600)
            .sameSite("None")
            .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", rt)
            .path("/")
            .httpOnly(true)
            .secure(true)
            .maxAge(604800)
            .sameSite("None")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
}
