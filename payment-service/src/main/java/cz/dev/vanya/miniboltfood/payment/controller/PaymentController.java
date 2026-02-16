package cz.dev.vanya.miniboltfood.payment.controller;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Tag(
        name = "Payment Controller",
        description = "Provides API for payment operations."
)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Create a payment for an order.")
    @PostMapping
    public ResponseEntity<CreatePaymentResponseDto> createPayment(@RequestBody @Valid CreatePaymentRequestDto request) {
        log.info("Received request: paymentRequest={}.", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }
}
