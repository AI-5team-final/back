package com.example.backend.pdf;

import com.example.backend.dto.*;
import com.example.backend.dto.sign.SecurityUserDto;
//import com.example.backend.swagger.PdfControllerDocs;
import com.example.backend.dto.AgentFeedbackDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfController  {
//public class PdfController implements PdfControllerDocs {

    private final PdfService pdfService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadSinglePdf(
            @RequestParam(value = "startDay", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDay,
            @RequestParam(value = "endDay", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDay,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal SecurityUserDto authenticatedUser) {

        if (authenticatedUser == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "인증 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            Long userId = authenticatedUser.getId();
            String role = authenticatedUser.getRole();
            String result = pdfService.handlePdfUpload(file, userId, role, startDay, endDay);

            Map<String, String> response = new HashMap<>();
            response.put("message", result);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deletePdfById(
            @RequestBody Map<String, Long> request,
            @AuthenticationPrincipal SecurityUserDto authenticatedUser
    ) {
        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }
        Long userId = authenticatedUser.getId();
        Long pdfId = request.get("pdfId");
        if (pdfId == null) {
            return ResponseEntity.badRequest().body("PDF ID가 없습니다");
        }

        String result = pdfService.deletePdfById(pdfId, userId);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/EtoC") //이력서 to 채용
    public ResponseEntity<List<PostingResponseDTO>> resume2posting(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal SecurityUserDto authenticatedUser) {

        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<PostingResponseDTO> results = pdfService.resume2posting(file);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/CtoE")
    public ResponseEntity<List<ResumeResponseDTO>> matchJobPosting(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal SecurityUserDto user
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ResumeResponseDTO> results = pdfService.posting2resume(file);
        return ResponseEntity.ok(results);
    }
    @PostMapping("/reEpo") // resumeEndPosting
    public ResponseEntity<List<OneToneDTO>> uploadResumeAndPosting(
            @AuthenticationPrincipal SecurityUserDto user,
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("posting") MultipartFile posting) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            List<OneToneDTO> result = pdfService.matchResumeAndPosting(resume, posting);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/agent")
    public ResponseEntity<AgentFeedbackDTO> analyzeWithAgent(@RequestBody AgentRequestDTO dto) {

        try {
            AgentFeedbackDTO feedback = pdfService.analyzeWithAgent(
                    dto.getResume_eval(),
                    dto.getSelfintro_eval(),
                    dto.getResume_score(),
                    dto.getSelfintro_score(),
                    dto.getSelfIntroFeedback()
            );
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AgentFeedbackDTO.builder()
                            .type("error")
                            .message("AI 분석 실패")
                            .gapText("")
                            .planText("")
                            .selfIntroFeedback("")
                            .build());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<PdfResponseDTO> getPdf(@AuthenticationPrincipal SecurityUserDto authenticatedUser) {
        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = authenticatedUser.getId();
        PdfResponseDTO response = pdfService.getUserPdfs(userId);

        return ResponseEntity.ok(response);
    }
}