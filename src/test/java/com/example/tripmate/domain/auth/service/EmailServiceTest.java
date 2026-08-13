package com.example.tripmate.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tripmate.common.exception.CustomException;
import com.example.tripmate.domain.auth.dto.request.EmailConfirmRequest;
import com.example.tripmate.domain.auth.dto.request.EmailVerificationRequest;
import com.example.tripmate.domain.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("이메일 중복 확인 및 인증번호 전송 정상 동작")
    void sendVerificationEmail_success() {

        // Given
        String email = "test@test.com";
        EmailVerificationRequest request = new EmailVerificationRequest(email);

        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendVerificationEmail(request);

        // Then
        verify(valueOperations).set(eq(email), anyString(), eq(5L), eq(TimeUnit.MINUTES));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("이메일 중복 확인 및 인증번호 전송 실패 - 중복된 이메일")
    void sendVerificationEmail_failure_duplicateEmail() {

        // Given
        String email = "test@test.com";
        EmailVerificationRequest request = new EmailVerificationRequest(email);

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> emailService.sendVerificationEmail(request));

        // Then
        assertEquals("이미 사용 중인 이메일입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("이메일 중복 확인 및 인증번호 전송 실패 - 인증번호 전송 실패")
    void sendVerificationEmail_failure_failToSendEmail() {

        // Given
        String email = "test@test.com";
        EmailVerificationRequest request = new EmailVerificationRequest(email);

        MimeMessage mimeMessage = mock(MimeMessage.class, invocation -> {
            throw new MessagingException("강제로 발생시킨 예외");
        });

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> emailService.sendVerificationEmail(request));

        // Then
        assertEquals("이메일 발송에 실패했습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("이메일 인증 번호 확인 정상 동작")
    void verifyCode_success() {

        // Given
        String email = "test@test.com";
        String code = "verifyCode";
        String savedCode = "verifyCode";

        EmailConfirmRequest request = new EmailConfirmRequest(email, code);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(savedCode);

        // When
        boolean response = emailService.verifyCode(request);

        // Then
        assertThat(response).isEqualTo(true);
        verify(redisTemplate).delete(email);
    }

    @Test
    @DisplayName("이메일 인증 번호 확인 실패 - savedCode 불러오기 실패")
    void verifyCode_failure_nullSavedCode() {

        // Given
        String email = "test@test.com";
        String code = "verifyCode";
        String savedCode = null;

        EmailConfirmRequest request = new EmailConfirmRequest(email, code);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(savedCode);

        // When
        boolean response = emailService.verifyCode(request);

        // Then
        assertThat(response).isEqualTo(false);
    }

    @Test
    @DisplayName("이메일 인증 번호 확인 실패 - 잘못된 코드 입력")
    void verifyCode_failure_wrondCode() {

        // Given
        String email = "test@test.com";
        String code = "wrongVerifyCode";
        String savedCode = "verifyCode";

        EmailConfirmRequest request = new EmailConfirmRequest(email, code);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(savedCode);

        // When
        boolean response = emailService.verifyCode(request);

        // Then
        assertThat(response).isEqualTo(false);
    }
}