package cz.dev.vanya.miniboltfood.order.service;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.order.payload.dto.OrderDto;
import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;

/**
 * Provides application-level operations for working with orders.
 *
 * <p>
 * Responsible for creating new orders, retrieving existing order details and processing order lifecycle transitions
 * (payment, delivery assignment, delivery completion).
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

    /**
     * Initiates payment processing for the given order.
     *
     * @param payOrderRequestDto request payload containing payment method
     * @param orderId            order identifier
     * @return updated order representation
     */
    OrderDto payOrder(PayOrderRequestDto payOrderRequestDto, Long orderId);

    /**
     * Processes a {@link DeliveryAssignedEvent} and updates the related order with delivery details.
     *
     * @param deliveryAssignedEvent delivery assignment event
     */
    void processDeliveryAssigned(DeliveryAssignedEvent deliveryAssignedEvent);

    /**
     * Marks an order as delivered (final step of the order lifecycle).
     *
     * @param orderId order identifier
     * @return updated order representation
     */
    OrderDto closeOrder(Long orderId);
}
