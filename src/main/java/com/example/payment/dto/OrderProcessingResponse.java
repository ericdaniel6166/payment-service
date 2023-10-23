package com.example.payment.dto;

import com.example.payment.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderProcessingResponse {

    Long orderId;
    String accountNumber;
    BigDecimal totalAmount;
    OrderStatus orderStatus;

}
