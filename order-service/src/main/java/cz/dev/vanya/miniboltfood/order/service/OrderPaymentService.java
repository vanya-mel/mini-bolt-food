package cz.dev.vanya.miniboltfood.order.service;

import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;

/**
 * Handles payment-related operations for orders.
 *
 * <p>
 * Delegates payment processing to payment-service and returns the resulting order status.
 */
public interface OrderPaymentService {

    /**
     * Initiates payment for the given order using the provided payment method.
     *
     * @param payOrderRequestDto payment request containing selected method
     * @param order order entity to be paid
     * @return resulting order status after payment processing
     */
    OrderStatus makePayment(PayOrderRequestDto payOrderRequestDto, Order order);
}
