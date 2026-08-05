package com.example.tripmate.common.enums;

import com.example.tripmate.common.exception.CustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER(Authority.USER),
    ADMIN(Authority.ADMIN);

    private final String userRole;

    public static UserRole from(String userRole) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(userRole))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER_ROLE));
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
