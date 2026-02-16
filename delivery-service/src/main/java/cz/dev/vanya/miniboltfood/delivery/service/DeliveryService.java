package cz.dev.vanya.miniboltfood.delivery.service;

import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;

import java.util.Optional;

/**
 * Provides delivery-related operations.
 *
 * <p>
 * Responsible for assigning a delivery to an order and retrieving delivery information.
 */
public interface DeliveryService {

    /**
     * Assigns a delivery to the given order.
     *
     * @param orderId unique identifier of the order
     * @return created and persisted delivery
     */
    Delivery assignDelivery(Long orderId);

    /**
     * Finds a delivery by the related order identifier.
     *
     * @param orderId unique identifier of the order
     * @return delivery if present
     */
    Optional<Delivery> findByOrderId(Long orderId);
}
