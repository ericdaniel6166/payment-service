package com.example.payment.integration.kafka;

import com.example.payment.integration.kafka.event.OrderProcessingEvent;
import com.example.payment.service.PaymentService;
import com.example.springbootmicroservicesframework.integration.kafka.event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KafkaConsumer {

    final ModelMapper modelMapper;
    final PaymentService paymentService;


    @KafkaListener(topics = "${spring.kafka.consumers.order-processing.topic-name}",
            groupId = "${spring.kafka.consumers.order-processing.group-id}",
            containerFactory = "orderProcessingKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.consumers.order-processing.properties.concurrency}"
    )
    public void handleOrderProcessing(Event kafkaEvent) throws JsonProcessingException {
        var orderProcessingEvent = modelMapper.map(kafkaEvent.getPayload(), OrderProcessingEvent.class);
        log.info("handle orderProcessingEvent {}", orderProcessingEvent);
        paymentService.handleOrderProcessingEvent(orderProcessingEvent);
    }

}
