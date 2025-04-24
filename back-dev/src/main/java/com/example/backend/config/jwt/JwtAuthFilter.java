package com.example.backend.config.jwt;

import com.example.backend.dto.sign.SecurityUserDto;
import com.example.backend.entity.User;
import com.example.backend.user.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// AccessToken 검증만 담당, 만료 시 401 응답
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request Header에서 AccessToken을 가져온다.
        String atc = request.getHeader("Authorization");
        if (StringUtils.hasText(atc) && atc.startsWith("Bearer ")) {
            atc = atc.substring(7);
        }

        // 토큰 검사 생략(모두 허용 URL의 경우 토큰 검사 통과)
        if (!StringUtils.hasText(atc)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // AccessToken이 유효한지 확인
            if (jwtUtil.verifyToken(atc)) {
                // AccessToken 내부의 payload에서 email 정보를 추출한다.
                String email = jwtUtil.getUid(atc);

                // email로 user를 조회한다. 없다면 예외를 발생시킨다.
                User findUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

                // SecurityContext에 등록할 User 객체를 만들어준다.
                SecurityUserDto userDto = SecurityUserDto.builder()
                        .id(findUser.getId())
                        .email(findUser.getEmail())
                        .phone(findUser.getPhone())
                        .role(findUser.getRole())
                        .age(findUser.getAge())
                        .name(findUser.getName())
                        .build();

                // 액세스 토큰이 유효하면, 기존 토큰을 그대로 사용
                Authentication auth = getAuthentication(userDto);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // 액세스 토큰이 만료된 경우, 401을 반환하고 클라이언트가 refreshToken을 보내게끔 처리합니다.
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access token is expired. Please refresh your token.");
                return;
            }
        } catch (JwtException e) {
            logger.error("JWT token validation failed: {}", e);  // 예외 객체를 넘겨야 함
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    public Authentication getAuthentication(SecurityUserDto userDto) {
        return new UsernamePasswordAuthenticationToken(userDto, "",
                List.of(new SimpleGrantedAuthority(userDto.getRole())));
    }

}
