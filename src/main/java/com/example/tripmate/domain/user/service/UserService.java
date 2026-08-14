package com.example.tripmate.domain.user.service;

import com.example.tripmate.common.dto.AuthUser;
import com.example.tripmate.common.entity.User;
import com.example.tripmate.domain.user.dto.response.UserGetProfileResponse;
import com.example.tripmate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 내 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserGetProfileResponse getMyProfile(AuthUser authUser) {

        User user = userRepository.findActiveUserById(authUser.getId());

        return UserGetProfileResponse.from(user);
    }

    /**
     * 다른 유저 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserGetProfileResponse getUserProfile(Long userId) {

        User user = userRepository.findActiveUserById(userId);

        return UserGetProfileResponse.from(user);
    }
}
