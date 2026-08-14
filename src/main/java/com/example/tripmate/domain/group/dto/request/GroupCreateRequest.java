package com.example.tripmate.domain.group.dto.request;

import com.example.tripmate.common.constants.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateRequest {

    @NotBlank(message = ValidationMessage.GROUP_NAME_NOT_BLANK)
    private String name;

    @NotBlank(message = ValidationMessage.PASSWORD_NOT_BLANK)
    @Pattern(regexp = "^[0-9]{4}$", message = ValidationMessage.GROUP_PASSWORD_PATTERN)
    private String password;
}
