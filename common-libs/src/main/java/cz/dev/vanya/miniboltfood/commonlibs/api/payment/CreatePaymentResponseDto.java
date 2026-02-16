package cz.dev.vanya.miniboltfood.commonlibs.api.payment;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentMethodDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;

import java.math.BigDecimal;

public record CreatePaymentResponseDto(
        Long id,
        Long orderId,
        BigDecimal amount,
        PaymentMethodDto paymentMethod,
        PaymentStatusDto paymentStatus
) {
}
