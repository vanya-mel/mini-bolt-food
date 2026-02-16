package cz.dev.vanya.miniboltfood.order.messaging;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for {@link DeliveryAssignedEvent}.
 *
 * <p>
 * Updates order state and enriches order details (courier name, ETA) after delivery assignment.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryAssignedEventConsumer {

    private final OrderService orderService;

    /**
     * Processes incoming {@link DeliveryAssignedEvent} messages.
     *
     * @param deliveryAssignedEventConsumerRecord Kafka record containing delivery assignment data
     */
    @KafkaListener(topics = "${delivery-assigned-topic}")
    public void listen(final ConsumerRecord<Long, DeliveryAssignedEvent> deliveryAssignedEventConsumerRecord) {
        log.info("Delivery assigned event received for order [id={}].", deliveryAssignedEventConsumerRecord.key());
        orderService.processDeliveryAssigned(deliveryAssignedEventConsumerRecord.value());
    }
}
