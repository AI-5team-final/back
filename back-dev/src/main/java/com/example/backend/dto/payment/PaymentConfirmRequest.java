package com.example.backend.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Toss 결제 승인 요청 DTO")
public class PaymentConfirmRequest {

    @Schema(description = "Toss에서 받은 결제 고유 키", example = "tviva20250404003245L9j04")
    private String paymentKey;

    @Schema(description = "결제 요청 시 사용한 주문 ID", example = "credit_f6ff27e8-5c05-47a1-a1a3-52a186f03269")
    private String orderId;

    @Schema(description = "충전 금액 (원 단위)", example = "10000")
    private int amount;
}