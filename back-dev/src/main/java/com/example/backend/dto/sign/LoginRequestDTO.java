package com.example.backend.dto.sign;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDTO {

    @Schema(description = "사용자 이메일 또는 ID", example = "user@example.com")
    private String username;

    @Schema(description = "비밀번호", example = "password123")
    private String password;

    @Schema(description = "회원 구분 (APPLICANT or HR)", example = "APPLICANT")
    private String role;
}
