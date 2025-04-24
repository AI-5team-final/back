package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "이력서 처리 결과 래퍼 DTO")
public class ResumeResultWrapper {

    @Schema(description = "처리 결과 상태", example = "성공", required = true)
    private String result;

    @Schema(description = "MongoDB에서 생성된 ObjectId", example = "605c72ef153207001f77f2f", required = true)
    private String objectId;
}
