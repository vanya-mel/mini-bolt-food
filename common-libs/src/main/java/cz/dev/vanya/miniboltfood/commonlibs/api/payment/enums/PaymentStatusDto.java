package cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums;

/**
 * DTO enum represents the lifecycle state of a payment.
 */
public enum PaymentStatusDto {
    /**
     * The payment has been created and is awaiting processing.
     */
    PAYMENT_INITIATED,
    /**
     * The payment has been successfully completed.
     */
    PAYMENT_SUCCEEDED,
    /**
     * The payment processing failed.
     */
    PAYMENT_FAILED,
    /**
     * The payment has been refunded.
     */
    REFUNDED
}
