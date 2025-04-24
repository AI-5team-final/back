package com.example.backend.config.security;

import com.example.backend.config.jwt.JwtUtil;
import com.example.backend.dto.sign.LoginRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUtil jwtUtil;

    // JwtUtil을 추가로 주입받도록 생성자를 수정합니다.
    public JsonUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/auth/login"); // loginProcessingUrl과 같아야 함
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        // 요청 헤더에 Authorization 값이 존재하는지 먼저 확인합니다.
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.verifyToken(token)) {
                    // 이미 유효한 토큰이 존재하므로, 토큰 내부에서 사용자 정보를 추출하고
                    // 인증 객체를 생성하여 반환합니다.
                    String email = jwtUtil.getUid(token);
                    // 여기서는 기본 USER 권한으로 설정합니다.
                    UsernamePasswordAuthenticationToken existingAuth =
                            new UsernamePasswordAuthenticationToken(email, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    existingAuth.setAuthenticated(true);
                    return existingAuth;
                }
            } catch (Exception e) {
                // 토큰 검증에 실패하면 로그인 절차를 진행하도록 합니다.
            }
        }

        // 기존 로그인 요청 처리 로직: 클라이언트가 JSON 형태로 전송한 로그인 정보를 파싱하여 인증을 진행합니다.
        try {
            LoginRequestDTO loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
