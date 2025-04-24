package com.example.backend.swagger;

import com.example.backend.dto.TokenRefreshRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@Tag(name = "Token", description = "AccessToken/RefreshToken 관리 API")
public interface TokenControllerDocs {

    @Operation(
            summary = "Logout",
            description = "Authorization 헤더의 AccessToken 기반 로그아웃 처리. RefreshToken 삭제 및 쿠키 만료.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer accessToken",
                            required = true,
                            in = ParameterIn.HEADER
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                            content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
            }
    )
    ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader, HttpServletResponse response);

    @Operation(
            summary = "AccessToken 재발급",
            description = "유효한 refreshToken 쿠키를 기반으로 accessToken을 새로 발급합니다.",
            parameters = {
                    @Parameter(
                            name = "refreshToken",
                            description = "refreshToken 값 (쿠키에서 전달됨)",
                            required = false,
                            in = ParameterIn.COOKIE
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "accessToken 재발급 성공",
                            content = @Content(schema = @Schema(implementation = TokenRefreshRequestDTO.class))),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰 또는 유저 없음")
            }
    )
    ResponseEntity<TokenRefreshRequestDTO> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken);
}
