package cz.dev.vanya.miniboltfood.payment.payload.response;

import cz.dev.vanya.miniboltfood.payment.domain.PaymentMethod;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentStatus;

import java.math.BigDecimal;

public record CreatePaymentResponseDto(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus
) {
}
