package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PK로 사용

    @Column(nullable = false, unique = true)
    private String email;  // Unique constraint만 줘서 고유하게 사용

    @Column(nullable = false)
    private String name;

    @Column
    private int age;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String role;    // "APPLICANT" or "HR"

    @Column(nullable = false)
    private String password;

    @Column
    private Boolean tutorial;

    @Column
    private int credit;

}
