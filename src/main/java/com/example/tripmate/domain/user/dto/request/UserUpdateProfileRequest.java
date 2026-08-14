package com.example.tripmate.domain.user.dto.request;

import com.example.tripmate.common.constants.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateProfileRequest {

    @NotBlank(message = ValidationMessage.NICKNAME_NOT_BLANK)
    @Size(max = 20, message = ValidationMessage.NICKNAME_SIZE)
    private String nickname;
}
