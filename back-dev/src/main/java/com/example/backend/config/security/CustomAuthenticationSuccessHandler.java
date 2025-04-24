package com.example.backend.config.security;

import com.example.backend.config.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.example.backend.dto.sign.SignInResponseDTO;
import com.example.backend.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import java.io.IOException;

// RT 재사용 or 발급 + 쿠키 저장
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String refreshToken;
        String accessToken;

        // 쿠키에서 refreshToken 꺼내기
        String existingRefreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    existingRefreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // 기존 refreshToken 유효성 검증
        boolean reuseRefreshToken = false;
        if (existingRefreshToken != null && jwtUtil.verifyToken(existingRefreshToken)) {
            try {
                // DB에도 존재해야 함
                User tokenOwner = jwtUtil.getRefreshTokenService().getUserByRefreshToken(existingRefreshToken);
                if (tokenOwner != null && tokenOwner.getEmail().equals(user.getEmail())) {
                    reuseRefreshToken = true;
                }
            } catch (Exception e) {
                log.warn("기존 refreshToken이 DB에 존재하지 않음 또는 사용자 불일치: {}", e.getMessage());
            }
        }

        if (reuseRefreshToken) {
            refreshToken = existingRefreshToken;
        } else {
            refreshToken = jwtUtil.generateRefreshToken(user);
            jwtUtil.getRefreshTokenService().saveRefreshToken(user, refreshToken); // DB에 새로 저장
        }

        accessToken = jwtUtil.generateAccessToken(user);

        // refreshToken을 다시 쿠키로 저장
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // 로컬에서는 false 가능
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주
        response.addCookie(refreshCookie);

        // 수동으로 CSRF 토큰 꺼내서 쿠키로 내려보내기
//        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
//        if (csrfToken != null) {
//            Cookie cookie = new Cookie("XSRF-TOKEN", csrfToken.getToken());
//            cookie.setPath("/");
//            cookie.setHttpOnly(false); // JS에서 읽을 수 있어야 함
//            cookie.setSecure(true); // 필요 시
//            response.addCookie(cookie);
//        }

        // accessToken과 사용자 정보를 프론트에 JSON으로 전달
        SignInResponseDTO responseDTO = SignInResponseDTO.builder()
                .accessToken(accessToken)
                .isLoggedIn(true)
                .message("로그인 성공")
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .credit(user.getCredit())
                .role(user.getRole())
                .build();

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
    }

}
