package cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums;

/**
 * DTO enum represents supported payment methods on the requests.
 */
public enum PaymentMethodDto {
    /**
     * Payment performed using a payment card.
     */
    CARD,
    /**
     * Payment performed using a QR code.
     */
    QR,
    /**
     * Payment performed via bank transfer.
     */
    BANK_TRANSFER
}
