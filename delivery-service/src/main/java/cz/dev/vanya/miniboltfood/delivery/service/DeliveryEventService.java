package cz.dev.vanya.miniboltfood.delivery.service;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;

public interface DeliveryEventService {

    void processOrderPaid(OrderPaidEvent orderPaidEvent);
}
