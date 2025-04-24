package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "상태 응답 DTO (공통 응답 형식)")
public class StatusResponseDTO {

    @Schema(description = "상태 코드 (예: 200, 401, 500 등)", example = "200")
    private Integer status;

    @Schema(description = "응답 데이터", example = "{ \"result\": true }")
    private Object data;

    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI...")
    private String token;

    public StatusResponseDTO(Integer status) {
        this.status = status;
    }

    public static StatusResponseDTO addStatus(Integer status) {
        return new StatusResponseDTO(status);
    }
}
