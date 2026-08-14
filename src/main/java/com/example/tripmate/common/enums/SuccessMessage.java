package com.example.tripmate.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    // 인증
    AUTH_SIGNUP_SUCCESS("유저 회원가입 성공"),
    AUTH_LOGIN_SUCCESS("로그인 성공"),
    AUTH_LOGOUT_SUCCESS("로그아웃 성공"),
    AUTH_REISSUE_SUCCESS("Access 토큰 재발급 성공"),
    AUTH_EMAIL_SEND_SUCCESS("인증 번호 발송 성공"),
    AUTH_EMAIL_VERIFY_SUCCESS("이메일 인증 성공"),
    AUTH_NICKNAME_AVAILABLE("닉네임 중복 확인 성공"),

    ;

    private final String message;
}
