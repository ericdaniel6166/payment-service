package com.example.payment.service;

import com.example.payment.integration.kafka.event.OrderProcessingEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface PaymentService {

    void handleOrderProcessingEvent(OrderProcessingEvent event) throws JsonProcessingException;
}
