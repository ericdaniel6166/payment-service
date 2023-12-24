package com.example.payment.service.impl;

import com.example.payment.dto.OrderProcessingRequest;
import com.example.payment.dto.OrderProcessingResponse;
import com.example.payment.enums.OrderStatus;
import com.example.payment.enums.PaymentStatus;
import com.example.payment.integration.kafka.config.KafkaProducerProperties;
import com.example.payment.integration.kafka.event.OrderPaymentProcessingEvent;
import com.example.payment.integration.kafka.event.OrderProcessingEvent;
import com.example.payment.model.Payment;
import com.example.payment.model.PaymentStatusHistory;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.repository.PaymentStatusHistoryRepository;
import com.example.payment.service.PaymentService;
import com.example.springbootmicroservicesframework.integration.kafka.event.Event;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

    PaymentRepository paymentRepository;
    PaymentStatusHistoryRepository paymentStatusHistoryRepository;
    KafkaTemplate<String, Object> kafkaTemplate;
    KafkaProducerProperties kafkaProducerProperties;
    ObjectMapper objectMapper;

    @Transactional
    @Override
    public OrderProcessingResponse handleOrderProcessingOpenFeign(OrderProcessingRequest request) throws JsonProcessingException {
        var totalAmount = request.getItemList().stream()
                .map(item -> item.getProductPrice().multiply(BigDecimal.valueOf(item.getOrderQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var paymentDetail = objectMapper.writeValueAsString(request);
        var payment = Payment.builder()
                .orderId(request.getOrderId())
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
        return OrderProcessingResponse.builder()
                .totalAmount(totalAmount)
                .accountNumber(request.getAccountNumber())
                .orderId(request.getOrderId())
                .orderStatus(OrderStatus.PAYMENT_PROCESSING)
                .build();
    }

    @Transactional
    @Override
    public void handleOrderProcessingEvent(OrderProcessingEvent event) throws JsonProcessingException {
        var totalAmount = event.getItemList().stream()
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

