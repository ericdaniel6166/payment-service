package com.example.payment.api;

import com.example.payment.dto.OrderProcessingRequest;
import com.example.payment.dto.OrderProcessingResponse;
import com.example.payment.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentApi {

    PaymentService paymentService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("test");
        return ResponseEntity.ok("test");
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/handle-order-processing-open-feign")
    public ResponseEntity<OrderProcessingResponse> handleOrderProcessingOpenFeign(@RequestBody OrderProcessingRequest request) throws JsonProcessingException {
        log.info("handleOrderProcessingOpenFeign, orderId {}", request.getOrderId());
//        Thread.sleep(1000L * 60 * 60);
        return ResponseEntity.ok(paymentService.handleOrderProcessingOpenFeign(request));
    }
}
