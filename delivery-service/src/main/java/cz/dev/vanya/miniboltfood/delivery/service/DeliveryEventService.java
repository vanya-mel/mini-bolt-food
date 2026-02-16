package cz.dev.vanya.miniboltfood.delivery.service;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;

/**
 * Handles processing of delivery-related incoming events.
 */
public interface DeliveryEventService {

    /**
     * Processes {@link OrderPaidEvent} and triggers delivery assignment when applicable.
     *
     * @param orderPaidEvent event containing information about a successfully paid order
     */
    void processOrderPaid(OrderPaidEvent orderPaidEvent);
}
