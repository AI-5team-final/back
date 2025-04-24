package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 업로드 PDF 목록 응답 DTO")
public class PdfResponseDTO {

    @Schema(description = "사용자 ID", example = "3")
    private Long userId;

    @Schema(description = "업로드한 PDF 리스트")
    private List<PdfInfo> pdfs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "PDF 파일 정보")
    public static class PdfInfo {

        @Schema(description = "PDF 고유 ID", example = "1")
        private Long id;

        @Schema(description = "저장된 PDF 파일 이름", example = "7b2d-abc.pdf")
        private String pdfFileName;

        @Schema(description = "MongoDB ObjectId", example = "6605a2b1cde3f2134c123456")
        private String mongoObjectId;

        @Schema(description = "업로드 시각", example = "2025-03-29T10:15:30")
        private LocalDateTime uploadedAt;

        @Schema(description = "S3에 저장된 PDF 파일의 URI", example = "/files/c3bb5efb-ff71-4a08-971d-89c3cc331f78.pdf")
        private String s3Uri;  // S3 URI

        @Schema(description = "PDF의 presigned URL", example = "https://s3.amazonaws.com/rezoombucket-v2/files/c3bb5efb-ff71-4a08-971d-89c3cc331f78.pdf?AWSAccessKeyId=AKIA...&Expires=1622641721&Signature=...")
        private String presignedUrl;  // Presigned URL
    }
}
