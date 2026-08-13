package com.example.tripmate.domain.auth.dto.request;

import com.example.tripmate.common.constants.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthSignupRequest {

    @NotBlank(message = ValidationMessage.USERNAME_NOT_BLANK)
    private String name;

    @NotBlank(message = ValidationMessage.NICKNAME_NOT_BLANK)
    @Size(max = 20, message = ValidationMessage.USERNAME_SIZE)
    private String nickname;

    @NotBlank(message = ValidationMessage.EMAIL_NOT_BLANK)
    @Email(message = ValidationMessage.EMAIL_FORMAT)
    private String email;

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    private String password;
}
