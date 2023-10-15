package com.example.payment.service.impl;

import com.example.payment.enums.PaymentStatus;
import com.example.payment.integration.kafka.config.KafkaProducerProperties;
import com.example.payment.integration.kafka.event.OrderPaymentProcessingEvent;
import com.example.payment.integration.kafka.event.OrderProcessingEvent;
import com.example.payment.model.Payment;
import com.example.payment.model.PaymentStatusHistory;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.repository.PaymentStatusHistoryRepository;
import com.example.payment.service.PaymentService;
import com.example.springbootmicroservicesframework.kafka.event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentServiceImpl implements PaymentService {

    final PaymentRepository paymentRepository;
    final PaymentStatusHistoryRepository paymentStatusHistoryRepository;
    final KafkaTemplate<String, Object> kafkaTemplate;
    final KafkaProducerProperties kafkaProducerProperties;
    final ObjectMapper objectMapper;

    @Transactional
    @Override
    public void handleOrderProcessingEvent(OrderProcessingEvent event) throws JsonProcessingException {
        var totalAmount = event.getOrderProcessingItemList().stream()
                .map(item -> item.getProductPrice().multiply(BigDecimal.valueOf(item.getOrderQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var paymentDetail = objectMapper.writeValueAsString(event);
        var payment = Payment.builder()
                .orderId(event.getOrderId())
                .status(PaymentStatus.PAYMENT_PROCESSING.name())
                .totalAmount(totalAmount)
                .paymentDetail(paymentDetail)
                .build();
        paymentRepository.saveAndFlush(payment);
        paymentStatusHistoryRepository.saveAndFlush(PaymentStatusHistory.builder()
                .paymentId(payment.getId())
                .paymentDetail(paymentDetail)
                .status(PaymentStatus.PAYMENT_PROCESSING.name())
                .build());


        var paymentProcessingEvent = OrderPaymentProcessingEvent.builder()
                .accountNumber(event.getAccountNumber())
                .orderId(event.getOrderId())
                .totalAmount(totalAmount)
                .build();
        log.info("send paymentProcessingEvent {}", paymentProcessingEvent);
        kafkaTemplate.send(kafkaProducerProperties.getPaymentProcessingTopicName(), Event.builder()
                .payload(paymentProcessingEvent)
                .build());

    }
}

