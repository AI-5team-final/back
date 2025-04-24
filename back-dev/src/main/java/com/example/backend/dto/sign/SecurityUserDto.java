package com.example.backend.dto.sign;

import com.example.backend.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SecurityUserDto {
    // 이건 주로 Authentication 객체에서
    // 내부적으로 사용자 정보 추출할 때 쓰는 구조니까,
    // Swagger 문서에서는 노출되지 않음
    private Long id;
    private String email;
    private String phone;
    private String role;
    private int age;
    private String name;
    private Boolean tutorial;

    public User toUser(){
        return User.builder()
                .id(id)
                .email(email)
                .phone(phone)
                .role(role)
                .age(age)
                .name(name)
                .tutorial(tutorial)
                .build();

    }

}
