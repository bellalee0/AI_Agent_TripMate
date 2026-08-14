package com.example.tripmate.domain.user.service;

import com.example.tripmate.common.dto.AuthUser;
import com.example.tripmate.common.entity.User;
import com.example.tripmate.common.exception.CustomException;
import com.example.tripmate.common.exception.ErrorCode;
import com.example.tripmate.domain.user.dto.request.UserUpdatePasswordRequest;
import com.example.tripmate.domain.user.dto.request.UserUpdateProfileRequest;
import com.example.tripmate.domain.user.dto.response.UserGetProfileResponse;
import com.example.tripmate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    /**
     * 내 프로필 수정
     */
    @Transactional
    public UserGetProfileResponse updateProfile(AuthUser authUser, UserUpdateProfileRequest request) {

        User user = userRepository.findActiveUserById(authUser.getId());

        String newNickname = request.getNickname();

        if (userRepository.existsByNickname(newNickname)) {
            throw new CustomException(ErrorCode.NICKNAME_EXIST);
        }

        user.updateNickname(newNickname);
        userRepository.saveAndFlush(user);

        return UserGetProfileResponse.from(user);
    }

    /**
     * 비빌번호 변경
     */
    @Transactional
    public void updatePassword(AuthUser authUser, UserUpdatePasswordRequest request) {

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        User user = userRepository.findActiveUserById(authUser.getId());

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        if (ObjectUtils.nullSafeEquals(oldPassword, newPassword)) {
            throw new CustomException(ErrorCode.SAME_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.updatePassword(encodedPassword);
        userRepository.saveAndFlush(user);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public boolean deleteUser(AuthUser authUser) {

        User user = userRepository.findActiveUserById(authUser.getId());

        user.delete();
        userRepository.saveAndFlush(user);

        return true;
    }
}
