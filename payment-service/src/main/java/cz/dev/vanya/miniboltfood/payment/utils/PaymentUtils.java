package cz.dev.vanya.miniboltfood.payment.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentUtils {

    public static final String PAYMENT_CONCURRENTLY_CREATED_ERROR_MESSAGE = "Payment was created concurrently but could not be loaded.";
}
