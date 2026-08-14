package com.example.tripmate.domain.user.dto.request;

import com.example.tripmate.common.constants.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePasswordRequest {

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    private String oldPassword;

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    private String newPassword;
}
