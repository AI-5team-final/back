package com.example.backend.config.jwt;

import com.example.backend.entity.User;
import com.example.backend.token.GeneratedToken;
import com.example.backend.token.RefreshTokenService;
import com.example.backend.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;
    @Getter
    private final RefreshTokenService refreshTokenService; // RefreshTokenService를 통해 DB에 refresh token 저장 및 조회
    private final UserRepository userRepository;
    @Getter
    private String secretKey;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(jwtProperties.getSecret().getBytes());
    }

    // AccessToken과 RefreshToken을 생성하고, RefreshToken은 DB에 저장
    public GeneratedToken generateToken(User user) {
        String refreshToken = generateRefreshToken(user); // RefreshToken 생성
        String accessToken = generateAccessToken(user);   // AccessToken 생성

        // DB에 RefreshToken 저장
        refreshTokenService.saveRefreshToken(user, refreshToken); // DB에 저장하는 서비스 호출

        return new GeneratedToken(accessToken, refreshToken);
    }

    // RefreshToken 생성
    public String generateRefreshToken(User user) {
        long refreshPeriod = 1000L * 60L * 60L * 24L * 14L; // 2주

        String email = user.getEmail();
        String role = user.getRole();
        String name = user.getName();

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        claims.put("name", name);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // AccessToken 생성
    public String generateAccessToken(User user) {
        String email = user.getEmail();
        String role = user.getRole();
        String name = user.getName();

        long accessPeriod = 1000L * 60L * 120L; // 120분
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        claims.put("name", name);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰 검증
    public boolean verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey) // 비밀키를 설정하여 파싱
                    .parseClaimsJws(token);   // JWT 파싱

            // 토큰 만료 여부 확인
            return claims.getBody()
                    .getExpiration()
                    .after(new Date()); // 만료 시간 체크
        } catch (Exception e) {
            logger.error("Token verification failed: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 이메일을 추출
    public String getUid(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getName(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("name", String.class);
    }

    public Date getExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // 시크릿 키는 기존 verifyToken()과 동일하게
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public User getUserFromToken(String accessToken) {
        String email = getUid(accessToken);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new IllegalArgumentException("Invalid access token");
        }
    }

    public String extractAccessTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
