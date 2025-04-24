package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채용공고 매칭 결과 DTO")
public class PostingResponseDTO {

    @Schema(description = "전체 매칭 점수", example = "85", required = true)
    private int total_score;

    @Schema(description = "이력서 매칭 점수", example = "90", required = true)
    private int resume_score;

    @Schema(description = "자기소개서 매칭 점수", example = "80", required = true)
    private int selfintro_score;

    @Schema(description = "1차 평가 의견", example = "지원자는 기술적으로 매우 적합합니다.", required = true)
    private String opinion1;

    @Schema(description = "매칭 결과 요약", example = "이 후보자는 전반적으로 요구사항을 충족하며, 해당 직무에 적합한 경험을 가지고 있습니다.", required = true)
    private String summary;

    @Schema(description = "이력서 평가 내용", example = "이력서는 매우 강력하며, 특히 Python 관련 경험이 뛰어납니다.", required = true)
    private String eval_resume;

    @Schema(description = "자기소개서 평가 내용", example = "자기소개서는 경험을 잘 요약하고 있으며, 직무에 대한 열정이 잘 드러나 있습니다.", required = true)
    private String eval_selfintro;

    @Schema(description = "지원자의 이름", example = "김민수", required = true)
    private String name;

    @Schema(description = "채용공고 시작일", example = "2025-03-01", required = false)
    private LocalDate startDay;

    @Schema(description = "채용공고 종료일", example = "2025-03-31", required = false)
    private LocalDate endDay;

    @Schema(description = "채용공고 파일 URI", example = "/files/abc123xyz.pdf", required = true)
    private String uri;
}
