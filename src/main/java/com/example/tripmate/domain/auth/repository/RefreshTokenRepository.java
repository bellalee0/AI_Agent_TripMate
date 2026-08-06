package com.example.tripmate.domain.auth.repository;

import com.example.tripmate.common.entity.RefreshToken;
import com.example.tripmate.common.exception.CustomException;
import com.example.tripmate.common.exception.ErrorCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(Long userId);

    default RefreshToken findUserRefreshByUserId(Long userId) {
        return findByUserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_NOT_FOUND));
    }
}
