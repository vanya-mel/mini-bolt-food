package cz.dev.vanya.miniboltfood.delivery.service;

import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;

import java.util.Optional;

public interface DeliveryService {

    Delivery assignDelivery(Long orderId);

    Optional<Delivery> findByOrderId(Long orderId);
}
