package com.example.tripmate.domain.auth.service;

import com.example.tripmate.common.exception.CustomException;
import com.example.tripmate.common.exception.ErrorCode;
import com.example.tripmate.domain.auth.dto.request.EmailConfirmRequest;
import com.example.tripmate.domain.auth.dto.request.EmailVerificationRequest;
import com.example.tripmate.domain.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;

    /**
     * 이메일 중복 확인 및 인증번호 전송
     */
    public void sendVerificationEmail(EmailVerificationRequest request) {

        String email = request.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_EXIST);
        }

        try {

            String verificationCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
            redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("TripMate 이메일 인증");
            helper.setText("인증 번호는 <b>" + verificationCode + "</b> 입니다.<br>유출에 주의해주시고, 5분 이내에 입력해주세요.", true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 이메일 인증 번호 확인
     */
    public boolean verifyCode(EmailConfirmRequest request) {

        String email = request.getEmail();
        String code = request.getVerificationCode();

        String savedCode = redisTemplate.opsForValue().get(email);

        if (savedCode == null || !ObjectUtils.nullSafeEquals(savedCode, code)) {
            return false;
        }

        redisTemplate.delete(email);
        return true;
    }
}
