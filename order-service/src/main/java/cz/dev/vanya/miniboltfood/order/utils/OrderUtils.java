package cz.dev.vanya.miniboltfood.order.utils;

import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderUtils {

    private static final String ORDER_NOT_FOUND_BY_ID_ERROR_MESSAGE = "Order with id [%s] not found.";
    private static final String ORDER_PAYMENT_IN_WRONG_STATE_ERROR_MESSAGE = "Cannot pay order with id [%s]." +
            " Order must be in PENDING_PAYMENT state, but the real state is: %s.";
    private static final String PAYMENT_IN_PROGRESS_ERROR_MESSAGE = "Payment for order [%s] is in progress.";

    public String orderNotFoundByIdErrorMessage(final Long orderId) {
        return ORDER_NOT_FOUND_BY_ID_ERROR_MESSAGE.formatted(orderId);
    }

    public String orderPaymentInWrongStateErrorMessage(final Long orderId, final OrderStatus orderStatus) {
        return ORDER_PAYMENT_IN_WRONG_STATE_ERROR_MESSAGE.formatted(orderId, orderStatus);
    }

    public String orderPaymentInProgressErrorMessage(final Long orderId) {
        return PAYMENT_IN_PROGRESS_ERROR_MESSAGE.formatted(orderId);
    }
}
