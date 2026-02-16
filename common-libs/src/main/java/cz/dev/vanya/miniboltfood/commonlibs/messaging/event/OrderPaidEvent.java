package cz.dev.vanya.miniboltfood.commonlibs.messaging.event;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentMethodDto;

import java.math.BigDecimal;

public record OrderPaidEvent(
        Long orderId,
        Long paymentId,
        BigDecimal amount,
        PaymentMethodDto paymentMethod
) { }
