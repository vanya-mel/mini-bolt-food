package cz.dev.vanya.miniboltfood.order.service;

import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.payload.response.OrderDto;

/**
 * Provides application-level operations for working with orders.
 *
 * <p>
 * Responsible for creating new orders and retrieving existing order details.
 */
public interface OrderService {

    /**
     * Creates a new order along with its items, calculates the total amount and initializes the order status.
     *
     * @param createOrderRequestDto request payload containing customer data and order items
     * @return created order representation
     */
    OrderDto createOrder(CreateOrderRequestDto createOrderRequestDto);

    /**
     * Retrieves an order by its unique identifier, including its order items.
     *
     * @param orderId order identifier
     * @return order representation
     */
    OrderDto getOrderById(Long orderId);
}
