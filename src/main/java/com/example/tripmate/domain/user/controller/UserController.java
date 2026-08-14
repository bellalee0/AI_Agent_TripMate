package com.example.tripmate.domain.user.controller;

import static com.example.tripmate.common.enums.SuccessMessage.USER_GET_MY_PROFILE_SUCCESS;
import static com.example.tripmate.common.enums.SuccessMessage.USER_GET_USER_PROFILE_SUCCESS;
import static com.example.tripmate.common.enums.SuccessMessage.USER_UPDATE_PROFILE_SUCCESS;

import com.example.tripmate.common.dto.AuthUser;
import com.example.tripmate.common.dto.CommonResponse;
import com.example.tripmate.domain.user.dto.request.UserUpdatePasswordRequest;
import com.example.tripmate.domain.user.dto.request.UserUpdateProfileRequest;
import com.example.tripmate.domain.user.dto.response.UserGetProfileResponse;
import com.example.tripmate.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    /**
     * 내 프로필 조회
     */
    @Operation(
        summary = "내 프로필 조회",
        description = """
                    내 프로필 정보를 조회합니다.
                    """
    )
    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserGetProfileResponse>> getMyProfile(
        @AuthenticationPrincipal AuthUser authUser
    ) {

        UserGetProfileResponse response = userService.getMyProfile(authUser);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(USER_GET_MY_PROFILE_SUCCESS, response));
    }

    /**
     * 다른 유저 프로필 조회
     */
    @Operation(
        summary = "다른 유저 프로필 조회",
        description = """
                    다른 유저의 프로필 정보를 조회합니다.
                    """
    )
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserGetProfileResponse>> getUserProfile(
        @PathVariable Long userId
    ) {

        UserGetProfileResponse response = userService.getUserProfile(userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(USER_GET_USER_PROFILE_SUCCESS, response));
    }

    /**
     * 내 프로필 수정
     */
    @Operation(
        summary = "내 프로필 수정",
        description = """
                    닉네임을 수정합니다
                    """
    )
    @PatchMapping("/me")
    public ResponseEntity<CommonResponse<UserGetProfileResponse>> updateProfile(
        @AuthenticationPrincipal AuthUser authUser,
        @Valid @RequestBody UserUpdateProfileRequest request
    ) {

        UserGetProfileResponse response = userService.updateProfile(authUser, request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(USER_UPDATE_PROFILE_SUCCESS, response));
    }

    /**
     * 비빌번호 변경
     */
    @Operation(
        summary = "비밀번호 변경",
        description = """
                    비밀번호를 변경합니다
                    """
    )
    @PatchMapping("/me/password")
    public ResponseEntity<CommonResponse<Void>> updatePassword(
        @AuthenticationPrincipal AuthUser authUser,
        @Valid @RequestBody UserUpdatePasswordRequest request
    ) {

        userService.updatePassword(authUser, request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.successNodata(USER_UPDATE_PROFILE_SUCCESS));
    }
}
