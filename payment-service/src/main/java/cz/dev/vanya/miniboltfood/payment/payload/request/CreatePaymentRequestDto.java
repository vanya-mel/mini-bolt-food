package cz.dev.vanya.miniboltfood.payment.payload.request;

import cz.dev.vanya.miniboltfood.payment.domain.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentRequestDto(

        @NotNull
        @Positive
        Long orderId,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal amount,

        @NotNull
        PaymentMethod paymentMethod
) {
}
