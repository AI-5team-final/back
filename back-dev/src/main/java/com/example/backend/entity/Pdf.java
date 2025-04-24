package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "pdf_file_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String pdfUri;

    @Column(name = "pdf_file_name")
    private String pdfFileName;

    @Column(name = "mongo_object_id")
    private String mongoObjectId;

    @Column
    private LocalDateTime uploadedAt;
}
