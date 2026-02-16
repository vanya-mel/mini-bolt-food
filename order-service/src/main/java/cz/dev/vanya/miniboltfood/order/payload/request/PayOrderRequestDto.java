package cz.dev.vanya.miniboltfood.order.payload.request;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentMethodDto;
import jakarta.validation.constraints.NotNull;

public record PayOrderRequestDto(
        @NotNull
        PaymentMethodDto paymentMethod
) {
}
