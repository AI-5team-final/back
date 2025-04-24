package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "AccessToken 재발급 응답 DTO")
public class TokenRefreshRequestDTO {

    @Schema(description = "요청 처리 상태 (1: 성공, 0: 실패)", example = "1")
    private int status;

    @Schema(description = "재발급된 accessToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "요청 시 사용된 refreshToken", example = "refresh-token-value")
    private String refreshToken;

    @Schema(description = "accessToken 만료 시간", example = "2025-04-04T01:00:00Z")
    private String expiresAt;

    @Schema(description = "오류 메시지 또는 설명", example = "Invalid or missing refresh token.")
    private String message;
}