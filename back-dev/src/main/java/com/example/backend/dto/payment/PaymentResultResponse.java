package com.example.backend.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "결제 처리 결과 응답 DTO")
public class PaymentResultResponse {

    @Schema(description = "결제 처리 메시지", example = "결제 완료 및 크레딧 적립 완료!")
    private String message;

    @Schema(description = "적립된 총 크레딧", example = "10000")
    private int credit;

    @Schema(description = "영수증 URL (없을 수도 있음)", example = "https://dashboard.tosspayments.com/receipt/example")
    private String receiptUrl;
}
