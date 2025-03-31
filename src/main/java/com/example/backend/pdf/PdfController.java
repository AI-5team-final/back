package com.example.backend.pdf;

import com.example.backend.dto.PdfResponseDTO;
import com.example.backend.dto.SecurityUserDto;
import com.example.backend.entity.User;
import com.example.backend.swagger.PdfControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfController implements PdfControllerDocs {

    private final PdfService pdfService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSinglePdf(@RequestParam("file") MultipartFile file,
                                                  @AuthenticationPrincipal SecurityUserDto authenticatedUser) {
        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }
        try {
            Long userId = authenticatedUser.getId();
            String result = pdfService.handlePdfUpload(file, userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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


    // TODO: 응답형태 페이징객체 논의 필요.
    @GetMapping("/list")
    public ResponseEntity<PdfResponseDTO> getPdf(@AuthenticationPrincipal SecurityUserDto authenticatedUser) {
        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = authenticatedUser.getId();
        PdfResponseDTO response = pdfService.getUserPdfs(userId);

        // front에는 이런 형식으로 response 됩니다.
        /**
         *
         * {
         *   "userId": 3,
         *   "pdfs": [
         *     {
         *       "id": 1,
         *       "pdfFileName": "3a5b-1234.pdf",
         *       "mongoObjectId": "6605a2...",
         *       "uploadedAt": "2025-03-29T10:15:30"
         *     },
         *     {
         *       "id": 2,
         *       "pdfFileName": "7b2d-abc.pdf",
         *       "mongoObjectId": "6605a3...",
         *       "uploadedAt": "2025-03-29T11:25:42"
         *     }
         *   ]
         * }
         *
         * **/
        return ResponseEntity.ok(response);
    }

}