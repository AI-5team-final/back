package com.example.backend.payment;

import com.example.backend.entity.PaymentHistory;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findAllByUserOrderByApprovedAtDesc(User user);

}
