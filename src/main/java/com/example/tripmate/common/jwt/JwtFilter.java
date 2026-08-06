package com.example.tripmate.common.jwt;

import com.example.tripmate.common.exception.ErrorCode;
import com.example.tripmate.common.exception.CustomException;
import com.example.tripmate.common.dto.AuthUser;
import com.example.tripmate.common.dto.CommonResponse;
import com.example.tripmate.common.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null || jwt.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims;
        try {
            claims = jwtUtil.extractAllClaims(jwt);
        } catch (JwtException | IllegalArgumentException e) {
            handleCustomException(response, new CustomException(ErrorCode.INVALID_TOKEN));
            return;
        }

        if (jwtUtil.isRefreshToken(claims)) {
            handleCustomException(response, new CustomException(ErrorCode.INVALID_TOKEN));
            return;
        }

        AuthUser authUser = jwtUtil.extractAuthUser(claims);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    // Filter 내부에서 발생하는 CustomException 처리
    private void handleCustomException(HttpServletResponse response, CustomException e) throws IOException {

        response.setStatus(e.getErrorCode().getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        CommonResponse<Void> commonResponse = CommonResponse.exception(e.getErrorCode().getMessage());
        writeErrorResponse(response, commonResponse);
    }

    private void writeErrorResponse(HttpServletResponse response, CommonResponse<Void> body) throws IOException {

        String json = objectMapper.writeValueAsString(body);

        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
            writer.flush();
        }
    }
}
