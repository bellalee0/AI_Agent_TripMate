package com.example.tripmate.domain.auth.controller;

import com.example.tripmate.common.dto.CommonResponse;
import com.example.tripmate.common.enums.SuccessMessage;
import com.example.tripmate.domain.auth.dto.request.EmailConfirmRequest;
import com.example.tripmate.domain.auth.dto.request.EmailVerificationRequest;
import com.example.tripmate.domain.auth.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class EmailController {

    private final EmailService emailService;

    /**
     * 이메일 중복 확인 및 인증번호 전송
     */
    @Operation(
        summary = "이메일 중복 확인 및 인증번호 전송",
        description = """
                    이메일 중복을 확인하고, 사용자의 이메일에 인증번호를 전송합니다.
                    """
    )
    @PostMapping("/email/verification-code")
    public ResponseEntity<CommonResponse<Void>> sendVerificationEmail(@Valid @RequestBody EmailVerificationRequest request) {

        emailService.sendVerificationEmail(request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.successNodata(SuccessMessage.AUTH_EMAIL_SEND_SUCCESS));
    }

    /**
     * 이메일 인증 번호 확인
     */
    @Operation(
        summary = "이메일 인증 번호 확인",
        description = """
                    전송된 이메일 인증 번호와 사용자가 입력한 인증 번호가 일치한지 확인합니다.
                    """
    )
    @PostMapping("/email/verify")
    public ResponseEntity<CommonResponse<Boolean>> verifyCode(
        @Valid @RequestBody EmailConfirmRequest request
    ) {
        boolean isVerified = emailService.verifyCode(request);

        return ResponseEntity.status(HttpStatus.OK)
            .body(CommonResponse.success(SuccessMessage.AUTH_EMAIL_VERIFY_SUCCESS, isVerified));
    }
}
