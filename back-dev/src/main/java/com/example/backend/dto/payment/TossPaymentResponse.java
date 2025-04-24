package com.example.backend.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Toss 결제 응답 객체 (Toss API로부터 받은 응답)")
public class TossPaymentResponse {

    @Schema(description = "Toss 결제 키", example = "tviva20250404003245L9j04")
    private String paymentKey;

    @Schema(description = "주문 ID", example = "credit_f6ff27e8-5c05-47a1-a1a3-52a186f03269")
    private String orderId;

    @Schema(description = "결제 상태", example = "DONE")
    private String status;

    @Schema(description = "결제 총액", example = "10000")
    private int totalAmount;

    @Schema(description = "결제 수단", example = "카드")
    private String method;

    @Schema(description = "결제 승인 시간", example = "2025-03-29T18:30:12+09:00")
    private String approvedAt;

    @Schema(description = "영수증 정보")
    private Receipt receipt;

    @Data
    @Schema(description = "Toss 결제 영수증 정보")
    public static class Receipt {
        @Schema(description = "영수증 URL", example = "https://dashboard.tosspayments.com/receipt/example")
        private String url;
    }
}