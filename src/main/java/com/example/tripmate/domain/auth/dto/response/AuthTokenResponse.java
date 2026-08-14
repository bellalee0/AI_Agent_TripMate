package com.example.tripmate.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthTokenResponse {

    private final String token;
    private final String refreshToken;
}
