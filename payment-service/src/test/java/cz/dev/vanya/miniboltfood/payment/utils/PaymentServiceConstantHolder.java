package cz.dev.vanya.miniboltfood.payment.utils;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentMethodDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentMethod;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentStatus;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class PaymentServiceConstantHolder {

    public static final Long PAYMENT_ID = 11L;
    public static final Long ORDER_ID = 197L;

    public static final BigDecimal AMOUNT = new BigDecimal("26.90");

    public static final PaymentMethod PAYMENT_METHOD = PaymentMethod.CARD;
    public static final PaymentMethodDto PAYMENT_METHOD_DTO = PaymentMethodDto.CARD;

    public static final PaymentStatus PAYMENT_STATUS = PaymentStatus.PAYMENT_SUCCEEDED;
    public static final PaymentStatusDto PAYMENT_STATUS_DTO = PaymentStatusDto.PAYMENT_SUCCEEDED;

}
