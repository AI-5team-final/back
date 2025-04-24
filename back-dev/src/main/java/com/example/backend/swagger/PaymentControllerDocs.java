package com.example.backend.swagger;

import com.example.backend.dto.payment.CreditResponseDTO;
import com.example.backend.dto.payment.PaymentConfirmRequest;
import com.example.backend.dto.payment.PaymentResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Tag(name = "Payment", description = "결제 및 크레딧 관련 API")
public interface PaymentControllerDocs {

    @Operation(
            summary = "결제 승인 및 크레딧 적립",
            description = "Toss 결제 완료 후 해당 정보를 바탕으로 유저에게 크레딧을 적립합니다.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentConfirmRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 성공 및 크레딧 적립 완료",
                            content = @Content(schema = @Schema(implementation = PaymentResultResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Toss 결제 오류 또는 중복 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    ResponseEntity<PaymentResultResponse> confirmPayment(
            @RequestBody PaymentConfirmRequest request,
            @Parameter(description = "Bearer 액세스 토큰", required = true, example = "Bearer eyJhbGciOi...") String authHeader
    );

    @Operation(
            summary = "크레딧 사용 (500 크레딧 차감)",
            description = "사용자가 특정 서비스 이용을 위해 500 크레딧을 차감합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "크레딧 차감 성공",
                            content = @Content(schema = @Schema(implementation = CreditResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "잔액 부족"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    ResponseEntity<CreditResponseDTO> useCredit(
            @Parameter(description = "Bearer 액세스 토큰", required = true, example = "Bearer eyJhbGciOi...") String authHeader
    );

    @Operation(
            summary = "크레딧 롤백",
            description = "직전 사용한 500 크레딧을 복구합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "롤백 성공",
                            content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "롤백 실패")
            }
    )
    ResponseEntity<String> rollbackCredit(
            @Parameter(description = "Bearer 액세스 토큰", required = true, example = "Bearer eyJhbGciOi...") String authHeader
    );

    @Operation(
            summary = "결제/크레딧 사용 내역 조회",
            description = "유저의 결제 및 크레딧 사용 이력을 페이지 기반으로 조회합니다. 가입 리워드(WELCOME) 항목도 마지막에 포함됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(
                                    description = "히스토리 응답 예시",
                                    example = "{\n" +
                                            "  \"content\": [...],\n" +
                                            "  \"totalElements\": 6,\n" +
                                            "  \"totalPages\": 2,\n" +
                                            "  \"page\": 0\n" +
                                            "}"
                            )))
            }
    )
    ResponseEntity<Map<String, Object>> getPaymentHistory(
            @Parameter(description = "Bearer 액세스 토큰", required = true, example = "Bearer eyJhbGciOi...") String authHeader,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") int page,
            @Parameter(description = "페이지 크기", example = "5") int size
    );
}
