package cz.dev.vanya.miniboltfood.delivery.messaging;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.delivery.service.DeliveryEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for {@link OrderPaidEvent}.
 *
 * <p>
 * Reacts to successful payments by triggering delivery assignment logic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidEventConsumer {

    private final DeliveryEventService deliveryEventService;

    /**
     * Processes incoming {@link OrderPaidEvent} messages.
     *
     * @param orderPaidEvent event published when an order has been successfully paid
     */
    @KafkaListener(topics = "${order-paid-topic}")
    public void listen(final OrderPaidEvent orderPaidEvent) {
        log.info("Received order paid event for id={}.", orderPaidEvent.orderId());
        deliveryEventService.processOrderPaid(orderPaidEvent);
    }
}
