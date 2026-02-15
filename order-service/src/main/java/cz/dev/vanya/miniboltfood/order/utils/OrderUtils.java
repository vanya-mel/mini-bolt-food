package cz.dev.vanya.miniboltfood.order.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderUtils {

    private static final String ORDER_NOT_FOUND_BY_ID_ERROR_MESSAGE = "Order with id [%s] not found.";

    public String orderNotFoundByIdErrorMessage(final Long orderId) {
        return ORDER_NOT_FOUND_BY_ID_ERROR_MESSAGE.formatted(orderId);
    }
}
