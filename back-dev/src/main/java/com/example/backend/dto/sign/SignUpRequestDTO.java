package com.example.backend.dto.sign;

import com.example.backend.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.Getter;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class SignUpRequestDTO {
    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "securePassword123")
    private String password;

    @Schema(description = "비밀번호 확인", example = "securePassword123")
    private String confirmPassword;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "나이", example = "28")
    private int age;

    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "회원 구분 (APPLICANT or HR)", example = "APPLICANT")
    private String role;

    @Schema(description = "회사명 (HR 전용)", example = "ABC Corp")
    private String companyName;

    @Schema(description = "사업자등록번호 (HR 전용)", example = "123-45-67890")
    private String businessNumber;

    public User toUser(BCryptPasswordEncoder bCryptPasswordEncoder) {
        String finalName = role.equals("HR") ? companyName : name;
        String finalPhone = role.equals("HR") ? businessNumber : phone;

        return User.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .name(finalName)
                .phone(finalPhone)
                .age(age)
                .role(role)
                .build();
    }
}

