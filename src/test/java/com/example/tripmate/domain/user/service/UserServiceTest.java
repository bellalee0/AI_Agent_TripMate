package com.example.tripmate.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tripmate.common.dto.AuthUser;
import com.example.tripmate.common.entity.User;
import com.example.tripmate.common.exception.CustomException;
import com.example.tripmate.domain.user.dto.request.UserUpdatePasswordRequest;
import com.example.tripmate.domain.user.dto.request.UserUpdateProfileRequest;
import com.example.tripmate.domain.user.dto.response.UserGetProfileResponse;
import com.example.tripmate.domain.user.repository.UserRepository;
import com.example.tripmate.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("내 프로필 조회 정상 처리")
    void getMyProfile_success() {

        // Given
        AuthUser authUser = UserFixture.testAuthUser();

        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findActiveUserById(anyLong())).thenReturn(user);

        // When
        UserGetProfileResponse response = userService.getMyProfile(authUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getNickname()).isEqualTo("testUser");
        assertThat(response.getProfileImageUrl()).isNull();
    }

    @Test
    @DisplayName("다른 유저 프로필 조회 정상 처리")
    void getUserProfile_success() {

        // Given
        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findActiveUserById(anyLong())).thenReturn(user);

        // When
        UserGetProfileResponse response = userService.getUserProfile(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getNickname()).isEqualTo("testUser");
        assertThat(response.getProfileImageUrl()).isNull();
    }

    @Test
    @DisplayName("내 프로필 수정 정상 처리")
    void updateProfile_success() {

        // Given
        AuthUser authUser = UserFixture.testAuthUser();

        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String newNickname = "newNickname";
        UserUpdateProfileRequest request = new UserUpdateProfileRequest(newNickname);

        when(userRepository.findActiveUserById(anyLong())).thenReturn(user);
        when(userRepository.existsByNickname(anyString())).thenReturn(false);

        // When
        UserGetProfileResponse response = userService.updateProfile(authUser, request);

        // Then
        assertThat(response.getNickname()).isEqualTo(newNickname);
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("내 프로필 수정 실패 - 중복된 닉네임")
    void updateProfile_failure_duplicateNickname() {

        // Given
        AuthUser authUser = UserFixture.testAuthUser();

        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String newNickname = "newNickname";
        UserUpdateProfileRequest request = new UserUpdateProfileRequest(newNickname);

        when(userRepository.findActiveUserById(anyLong())).thenReturn(user);
        when(userRepository.existsByNickname(anyString())).thenReturn(true);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updateProfile(authUser, request));

        // Then
        assertEquals("이미 사용 중인 닉네임입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 정상 처리")
    void updatePassword_success() {

        // Given
        AuthUser authUser = UserFixture.testAuthUser();

        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String newEncodedPassword = "newEncodedPassword";
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(oldPassword, newPassword);

        when(userRepository.findActiveUserById(anyLong())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn(newEncodedPassword);

        // When
        userService.updatePassword(authUser, request);

        // Then
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 현재 비밀번호와 새 비밀번호 동일")
    void updatePassword_failure_samePassword() {

        // Given
        AuthUser authUser = UserFixture.testAuthUser();

        String oldPassword = "oldPassword";
        String newPassword = "oldPassword";
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(oldPassword, newPassword);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updatePassword(authUser, request));

        // Then
        assertEquals("현재 비밀번호와 새 비밀번호가 동일합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 잘못된 현재 비밀번호 입력")
    void updatePassword_failure_wrongPassword() {

        // Given
        AuthUser authUser = UserFixture.testAuthUser();

        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        UserUpdatePasswordRequest request = new UserUpdatePasswordRequest(oldPassword, newPassword);

        when(userRepository.findActiveUserById(anyLong())).thenReturn(user);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> userService.updatePassword(authUser, request));

        // Then
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원 탈퇴 정상 처리")
    void deleteUser() {

        // Given
        AuthUser authUser = UserFixture.testAuthUser();

        User user = UserFixture.testUser();
        ReflectionTestUtils.setField(user, "id", 1L);

        when(userRepository.findActiveUserById(anyLong())).thenReturn(user);

        // When
        userService.deleteUser(authUser);

        // Then
        verify(userRepository).saveAndFlush(any(User.class));
    }
}