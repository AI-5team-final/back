package com.example.backend.swagger;


import com.example.backend.dto.PdfResponseDTO;
import com.example.backend.dto.PostingResponseDTO;
import com.example.backend.dto.ResumeResponseDTO;
import com.example.backend.dto.sign.SecurityUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "PDF", description = "PDF 분석 및 매칭 관련 API")
public interface PdfControllerDocs {

//    @Operation(
//            summary = "PDF 업로드",
//            description = "사용자가 PDF 파일을 업로드하고, 필요시 시작일(startDay)과 종료일(endDay)을 설정하여 파일을 처리합니다.",
//            requestBody = @RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "multipart/form-data",
//                            schema = @Schema(type = "object", format = "binary", implementation = MultipartFile.class))
//            ),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "업로드 성공",
//                            content = @Content(schema = @Schema(implementation = String.class))),
//                    @ApiResponse(responseCode = "400", description = "PDF 파일 형식 오류"),
//                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
//                    @ApiResponse(responseCode = "500", description = "서버 오류")
//            }
//    )
//    ResponseEntity<String> uploadSinglePdf(
//            @Parameter(description = "시작 날짜", example = "2025-03-01")
//            @RequestParam(value = "startDay", required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate startDay,
//
//            @Parameter(description = "끝 날짜", example = "2025-03-31")
//            @RequestParam(value = "endDay", required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate endDay,
//
//            @Parameter(description = "업로드할 PDF 파일", required = true)
//            @RequestParam("file") MultipartFile file,
//
//            @Parameter(hidden = true) SecurityUserDto authenticatedUser
//    );
//
//    @Operation(
//            summary = "PDF 삭제",
//            description = "PDF ID를 통해 업로드된 파일을 삭제합니다.",
//            requestBody = @RequestBody(
//                    required = true,
//                    content = @Content(schema = @Schema(
//                            description = "삭제할 PDF의 ID",
//                            example = "{\"pdfId\": 3}"
//                    ))
//            ),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
//                    @ApiResponse(responseCode = "400", description = "PDF ID 누락"),
//                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
//            }
//    )
//    ResponseEntity<String> deletePdfById(
//            @RequestBody Map<String, Long> request,
//            @Parameter(hidden = true) SecurityUserDto authenticatedUser
//    );
//
//    @Operation(
//            summary = "이력서 → 채용공고 매칭",
//            description = "이력서 PDF를 업로드하여 적합한 채용공고 리스트를 반환합니다.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "매칭 성공",
//                            content = @Content(schema = @Schema(implementation = PostingResponseDTO.class))),
//                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
//            }
//    )
//    ResponseEntity<List<PostingResponseDTO>> resume2posting(
//            @Parameter(description = "이력서 PDF 파일", required = true)
//            MultipartFile file,
//            @Parameter(hidden = true) SecurityUserDto authenticatedUser
//    );
//
//    @Operation(
//            summary = "채용공고 → 이력서 매칭",
//            description = "채용공고 PDF를 업로드하여 적합한 이력서 리스트를 반환합니다.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "매칭 성공",
//                            content = @Content(schema = @Schema(implementation = ResumeResponseDTO.class))),
//                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
//            }
//    )
//    ResponseEntity<List<ResumeResponseDTO>> matchJobPosting(
//            @Parameter(description = "채용공고 PDF 파일", required = true)
//            MultipartFile file,
//            @Parameter(hidden = true) SecurityUserDto user
//    );
//
//
//    @Operation(
//            summary = "이력서 + 채용공고 동시 업로드 매칭",
//            description = "이력서와 채용공고를 동시에 업로드하여 1:1 매칭 결과를 반환합니다.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "매칭 성공",
//                            content = @Content(schema = @Schema(implementation = OneEoneDTO.class))),
//                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
//                    @ApiResponse(responseCode = "500", description = "매칭 실패")
//            }
//    )
//    ResponseEntity<List<OneEoneDTO>> uploadMultipleFiles(
//            @Parameter(hidden = true) SecurityUserDto user,
//            @Parameter(description = "이력서 PDF 파일", required = true) MultipartFile file1,
//            @Parameter(description = "채용공고 PDF 파일", required = true) MultipartFile file2
//    );
//
//    @Operation(
//            summary = "GPT 평가 결과 기반 AI 에이전트 피드백",
//            description = "GPT 평가 결과(JSON 문자열)를 AI 에이전트가 분석하여 추가 피드백을 제공합니다.",
//            requestBody = @RequestBody(
//                    required = true,
//                    content = @Content(schema = @Schema(
//                            type = "string",
//                            description = "GPT 평가 결과 문자열 (JSON 형식)",
//                            example = "{ \"total_score\": 88.5, \"summary\": \"백엔드 직무에 적합합니다.\" }"
//                    ))
//            ),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "AI 피드백 생성 성공",
//                            content = @Content(schema = @Schema(implementation = String.class))),
//                    @ApiResponse(responseCode = "500", description = "AI 분석 실패")
//            }
//    )
//    ResponseEntity<String> analyzeWithAgent(
//            @RequestBody String evaluationResult
//    );
//
//    @Operation(
//            summary = "사용자 PDF 리스트 조회",
//            description = "인증된 사용자가 업로드한 PDF 목록을 조회합니다.",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "조회 성공",
//                            content = @Content(schema = @Schema(implementation = PdfResponseDTO.class))),
//                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
//            }
//    )
//    ResponseEntity<PdfResponseDTO> getPdf(
//            @Parameter(hidden = true) SecurityUserDto authenticatedUser
//    );
}
