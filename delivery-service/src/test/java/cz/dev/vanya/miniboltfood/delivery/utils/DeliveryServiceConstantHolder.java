package cz.dev.vanya.miniboltfood.delivery.utils;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentMethodDto;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class DeliveryServiceConstantHolder {

    public static final Long ORDER_ID = 197L;
    public static final Long NEW_ORDER_ID = 198L;
    public static final Long DELIVERY_ID = 1L;
    public static final Long PAYMENT_ID = 7L;

    public static final Integer ETA_MINUTES = 42;

    public static final String COURIER_NAME = "Mr. Dusty D'Amore";
    public static final String DELIVERY_ASSIGNED_TOPIC = "delivery.events.v1";

    public static final BigDecimal PAYMENT_AMOUNT = new BigDecimal("26.90");
    public static final PaymentMethodDto PAYMENT_METHOD = PaymentMethodDto.CARD;

}
