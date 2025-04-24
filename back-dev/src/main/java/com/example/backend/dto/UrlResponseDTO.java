package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "URL 응답 DTO (페이지 이동 + 메시지 포함)")
public class UrlResponseDTO {

    @Schema(description = "리다이렉트할 URL", example = "/auth/login")
    private String url;

    @Schema(description = "결과 메시지", example = "회원가입을 성공했습니다.")
    private String message;
}
