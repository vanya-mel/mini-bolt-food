package cz.dev.vanya.miniboltfood.delivery.messaging;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.delivery.service.DeliveryEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidEventConsumer {

    private final DeliveryEventService deliveryEventService;

    @KafkaListener(topics = "${order-paid-topic}")
    public void listen(final OrderPaidEvent orderPaidEvent) {
        log.info("Received order paid event for id={}.", orderPaidEvent.orderId());
        deliveryEventService.processOrderPaid(orderPaidEvent);
    }
}
