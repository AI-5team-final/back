package com.example.backend.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreditResponseDTO {
    @Schema(description = "사용자 ID", example = "42")
    private String userId;

    @Schema(description = "차감된 금액", example = "1000")
    private int amount;

    @Schema(description = "남은 크레딧", example = "9000")
    private int balance;

    @Schema(description = "사용 승인 시각", example = "2025-03-29T17:45:00")
    private LocalDateTime approvedAt;

    @Schema(description = "충전 or 사용", example = "CHARGE")
    private String type; // "CHARGE" or "USE"
}
