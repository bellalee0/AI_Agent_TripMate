package com.example.tripmate.domain.auth.dto.request;

import com.example.tripmate.common.constants.ValidationMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationRequest {

    @NotBlank(message = ValidationMessage.EMAIL_NOT_BLANK)
    @Email(message = ValidationMessage.EMAIL_FORMAT)
    private String email;
}
