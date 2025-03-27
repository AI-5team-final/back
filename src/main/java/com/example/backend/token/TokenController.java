package com.example.backend.token;

import com.example.backend.config.jwt.JwtUtil;
import com.example.backend.dto.TokenRefreshRequestDTO;
import com.example.backend.entity.User;
import com.example.backend.user.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auths/token")
public class TokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader, HttpServletResponse response) {

        try {
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            User findUser = userRepository.findByEmail(jwtUtil.getUid(authHeader))
                    .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

            // Refresh Token 삭제
            refreshTokenService.deleteRefreshToken(findUser);

            System.out.println("삭제 완료");

            // Authorization 쿠키 만료
            Cookie authorizationCookie = new Cookie("Authorization", null);
            authorizationCookie.setHttpOnly(true);
            authorizationCookie.setSecure(true); // HTTPS 환경에서 사용
            authorizationCookie.setPath("/"); // 쿠키 경로 설정
            authorizationCookie.setMaxAge(0); // 즉시 만료
            response.addCookie(authorizationCookie);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Map.of("message", "Logout successful."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Map.of("message", "An error occurred during logout."));
        }
    }

    // front에서 이런식으로 요청 보내시면 돼요!
    /**
     *
     * // API 요청 함수
     * const makeApiRequest = async () => {
     *   const accessToken = localStorage.getItem('accessToken'); // 로컬 스토리지에서 accessToken 가져오기
     *
     *   try {
     *     // API 요청 (accessToken을 Authorization 헤더에 담아서)
     *     const response = await axios.get('/some-protected-resource', {
     *       headers: {
     *         'Authorization': `Bearer ${accessToken}` // Authorization 헤더에 accessToken을 담아 요청
     *       }
     *     });
     *     console.log(response.data);  // 서버 응답 데이터 처리
     *   } catch (error) {
     *     // 401 Unauthorized (accessToken이 만료되었을 때 서버가 401 응답을 보냄)
     *     if (error.response && error.response.status === 401) {
     *       console.log('Access token expired. Refreshing token...');
     *
     *       // refreshAccessToken 호출해서 새로운 accessToken을 받아옴
     *       await refreshAccessToken();
     *
     *       // 새로 발급된 accessToken으로 다시 API 요청
     *       makeApiRequest();
     *     }
     *   }
     * };
     *
     * // refreshToken을 사용해 새로운 accessToken을 요청하는 함수
     * const refreshAccessToken = async () => {
     *   const refreshToken = localStorage.getItem('refreshToken'); // 저장된 refreshToken 가져오기
     *
     *   try {
     *     // refreshToken을 서버로 보내서 새로운 accessToken을 요청
     *     const response = await axios.post('/api/token/refresh', { refreshToken });
     *
     *     // 새로 발급받은 accessToken을 로컬 스토리지에 저장
     *     localStorage.setItem('accessToken', response.data.accessToken);
     *     console.log('New access token:', response.data.accessToken);
     *
     *   } catch (error) {
     *     console.error('Error refreshing access token', error);
     *   }
     * };
     *
     * **/
    



    // front에서 refreshToken을 보내서 갱신하려는 경우에.
    // 프론트에서 refreshToken 넘겨주고 갱신하는 부분.
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshRequestDTO> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {

        // refreshToken이 유효한지 검증
        if (refreshToken == null || !jwtUtil.verifyToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenRefreshRequestDTO.builder()
                            .status(0)  // 상태 0 -> 오류 발생
                            .message("Invalid or missing refresh token.")
                            .build());
        }

        // refreshToken으로 사용자 조회
        User user = refreshTokenService.getUserByRefreshToken(refreshToken);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenRefreshRequestDTO.builder()
                            .status(0)
                            .message("User not found for the provided refresh token.")
                            .build());
        }

        // 유효한 refreshToken이 있으면 새로운 accessToken 발급
        String newAccessToken = jwtUtil.generateAccessToken(user);

        // 결과 DTO 반환
        TokenRefreshRequestDTO responseDTO = TokenRefreshRequestDTO.builder()
                .status(1)  // 상태 1 -> 성공
                .accessToken(newAccessToken)  // 새로 발급된 accessToken
                .refreshToken(refreshToken)  // 기존 refreshToken
                .build();

        return ResponseEntity.ok(responseDTO);
    }

}
