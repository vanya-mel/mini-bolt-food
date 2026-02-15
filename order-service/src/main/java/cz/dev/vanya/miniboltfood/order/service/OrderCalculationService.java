package cz.dev.vanya.miniboltfood.order.service;

import cz.dev.vanya.miniboltfood.order.domain.Order;

import java.math.BigDecimal;

/**
 * Calculates derived order values based on order items.
 */
public interface OrderCalculationService {

    /**
     * Calculates the total order amount and may enrich order items with purchase-time prices.
     *
     * @param order order to calculate price for
     * @return total order amount
     */
    BigDecimal calculateOrderPrice(Order order);
}
