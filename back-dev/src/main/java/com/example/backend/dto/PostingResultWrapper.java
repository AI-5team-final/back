package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "채용공고 처리 결과 래퍼 DTO")
public class PostingResultWrapper {

    @Schema(description = "결과 상태", example = "성공", required = true)
    private String result;

    @Schema(description = "MongoDB에서 생성된 ObjectId", example = "605c72ef153207001f77f2f", required = true)
    private String objectId;

    @Schema(description = "채용공고 시작일", example = "2025-03-01", required = false)
    private LocalDate startDay;

    @Schema(description = "채용공고 종료일", example = "2025-03-31", required = false)
    private LocalDate endDay;
}
