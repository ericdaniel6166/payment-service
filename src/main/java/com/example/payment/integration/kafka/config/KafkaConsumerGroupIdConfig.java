package com.example.payment.integration.kafka.config;

import com.example.springbootmicroservicesframework.integration.kafka.config.KafkaConsumerConfig;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConsumerGroupIdConfig {

    final KafkaConsumerConfig kafkaConsumerConfig;

    final KafkaConsumerProperties kafkaConsumerProperties;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> orderProcessingKafkaListenerContainerFactory() {
        return kafkaConsumerConfig.kafkaListenerContainerFactory(kafkaConsumerProperties.getOrderProcessingGroupId());
    }

}
