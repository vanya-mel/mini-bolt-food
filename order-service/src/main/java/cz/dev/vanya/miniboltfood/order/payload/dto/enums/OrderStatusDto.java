package cz.dev.vanya.miniboltfood.order.payload.dto.enums;

/**
 * DTO enum represents the lifecycle state of an order.
 */
public enum OrderStatusDto {
    /**
     * The order has been created and is waiting for the payment to be processed.
     */
    PENDING_PAYMENT,
    /**
     * The order has been successfully paid.
     */
    PAID,
    /**
     * Final state – the payment has failed or was not completed.
     */
    PAYMENT_FAILED,
    /**
     * The order has been paid and is waiting to be delivered.
     */
    PENDING_DELIVERY,
    /**
     * Final state – the order has been successfully delivered to the customer.
     */
    DELIVERED
}
