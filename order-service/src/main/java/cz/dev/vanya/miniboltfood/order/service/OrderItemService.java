package cz.dev.vanya.miniboltfood.order.service;

import java.math.BigDecimal;

/**
 * Provides access to item pricing information.
 *
 * <p>
 * In a real-world system this would typically query a catalog/pricing service.
 */
public interface OrderItemService {

    /**
     * Returns the current price for the given item identifier.
     *
     * @param itemId item identifier
     * @return current item price
     */
    BigDecimal getOrderItemCurrentPrice(Long itemId);
}
