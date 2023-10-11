package com.example.payment.repository;

import com.example.payment.model.PaymentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistory, Long> {

}
