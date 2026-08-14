package com.example.tripmate.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "유효 기간이 만료된 토큰입니다."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    REFRESH_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레시 토큰이 존재하지 않습니다."),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 권한입니다."),

    EMAIL_EXIST(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다."),
    NICKNAME_DUPLICATION_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "닉네임 생성 재시도 횟수를 초과했습니다."),
    NICKNAME_EXIST(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    INVALID_GENDER_FORMAT(HttpStatus.BAD_REQUEST, "성별은 '남' 또는 '여'만 입력 가능합니다."),

    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
