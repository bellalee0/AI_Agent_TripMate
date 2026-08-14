package com.example.tripmate.domain.user.dto.response;

import com.example.tripmate.common.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserGetProfileResponse {

    private final Long userId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;

    public static UserGetProfileResponse from(User user) {
        return new UserGetProfileResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getProfileImgUrl()
        );
    }
}
