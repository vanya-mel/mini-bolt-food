package cz.dev.vanya.miniboltfood.payment.domain;

/**
 * Represents supported payment methods.
 */
public enum PaymentMethod {
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
