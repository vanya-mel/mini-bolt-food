package cz.dev.vanya.miniboltfood.delivery.service.impl;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import cz.dev.vanya.miniboltfood.delivery.messaging.DeliveryAssignedEventClient;
import cz.dev.vanya.miniboltfood.delivery.service.DeliveryEventService;
import cz.dev.vanya.miniboltfood.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryEventServiceImpl implements DeliveryEventService {

    private final DeliveryService deliveryService;
    private final DeliveryAssignedEventClient deliveryAssignedEventClient;

    @Override
    public void processOrderPaid(final OrderPaidEvent orderPaidEvent) {
        final Long orderId = orderPaidEvent.orderId();
        final Optional<Delivery> delivery = deliveryService.findByOrderId(orderId);
        if (delivery.isPresent()) {
            log.info("Order delivery was already assigned. The delivery id={}.", delivery.get().getId());
            return;
        }

        try {
            final Delivery assignedDelivery = deliveryService.assignDelivery(orderId);
            deliveryAssignedEventClient.sendDeliveryAssignedEvent(assignedDelivery);
        } catch (DataIntegrityViolationException e) {
            log.error("Order delivery was already assigned concurrently. The delivery id={}.", orderId);
        }
    }
}
