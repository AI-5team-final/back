package com.example.backend.payment;

import com.example.backend.config.jwt.JwtUtil;
import com.example.backend.dto.payment.PaymentConfirmRequest;
import com.example.backend.dto.payment.TossPaymentResponse;
import com.example.backend.entity.PaymentHistory;
import com.example.backend.entity.User;
import com.example.backend.user.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    public Map<String, Object> processPaymentConfirmation(PaymentConfirmRequest request, String accessToken) throws JsonProcessingException {
        // 1. Toss에 결제 승인 요청
        HttpHeaders headers = new HttpHeaders();
        String encodedKey = Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());
        headers.set("Authorization", "Basic " + encodedKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", request.getPaymentKey());
        body.put("orderId", request.getOrderId());
        body.put("amount", request.getAmount());

        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TossPaymentResponse> response = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/confirm",
                httpRequest,
                TossPaymentResponse.class
        );


        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Toss 결제 승인 요청 실패");
        }

        // 2. Toss 응답을 DTO로 파싱
        TossPaymentResponse tossResponse = response.getBody();
        System.out.println("tossResponse = " + tossResponse);

        String status = tossResponse.getStatus(); // ex: "DONE", "WAITING_FOR_DEPOSIT"
        String method = tossResponse.getMethod(); // ex: "카드", "가상계좌"
        String approvedAtStr = tossResponse.getApprovedAt();

        // 3. 사용자 정보 추출 (JWT)
        String email = jwtUtil.getUid(accessToken); // Bearer 제거 필요)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));


        // 4. 가상계좌 입금 대기 상태 처리 (approvedAt 없음)
        System.out.println("method = " + method);
        if (status != null && status.equals("WAITING_FOR_DEPOSIT") &&
                method != null && method.contains("가상계좌")) {
            return Map.of(
                    "status", status,
                    "message", "⏳ 가상계좌가 발급되었습니다. 입금 후 결제가 완료됩니다.",
                    "receiptUrl", tossResponse.getReceipt().getUrl(),
                    "credit", user.getCredit() // 아직 증가 안 됨
            );
        }

        // 5. 승인된 결제가 아니면 막기
        if (approvedAtStr == null) {
            throw new IllegalStateException("❌ 결제가 승인되지 않았습니다.");
        }

        // 6. 승인 완료된 경우만 저장 처리
        int amount = tossResponse.getTotalAmount();
        OffsetDateTime approvedAt = OffsetDateTime.parse(tossResponse.getApprovedAt());
        LocalDateTime localDateTime = approvedAt.toLocalDateTime();

        user.setCredit(user.getCredit() + amount);
        userRepository.save(user);

        PaymentHistory history = PaymentHistory.builder()
                .paymentKey(tossResponse.getPaymentKey())
                .orderId(tossResponse.getOrderId())
                .amount(amount)
                .method(tossResponse.getMethod())
                .receiptUrl(tossResponse.getReceipt().getUrl())
                .approvedAt(localDateTime)
                .user(user)
                .type("CHARGE")
                .build();

        paymentHistoryRepository.save(history);

        return Map.of(
                "status", status,
                "message", "결제가 승인되었습니다.",
                "receiptUrl", tossResponse.getReceipt().getUrl(),
                "credit", user.getCredit()
        );
    }

    public List<PaymentHistory> getHistoryForUser(User user) {
        return paymentHistoryRepository.findAllByUserOrderByApprovedAtDesc(user);
    }

    public void saveUseHistory(User user, int amount) {
        PaymentHistory history = PaymentHistory.builder()
                .amount(amount)
                .approvedAt(LocalDateTime.now())
                .user(user)
                .type("USE")
                .build();

        paymentHistoryRepository.save(history);
    }
}