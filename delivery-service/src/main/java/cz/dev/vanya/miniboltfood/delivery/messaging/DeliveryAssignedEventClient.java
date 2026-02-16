package cz.dev.vanya.miniboltfood.delivery.messaging;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import cz.dev.vanya.miniboltfood.delivery.mapper.DeliveryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryAssignedEventClient {

    @Value("${delivery-assigned-topic:configure-me-topic}")
    private String deliveryAssignedTopic;

    private final DeliveryMapper deliveryMapper;
    private final KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;

    public void sendDeliveryAssignedEvent(final Delivery delivery) {
        final DeliveryAssignedEvent deliveryAssignedEvent = deliveryMapper.mapToDeliveryAssignedEvent(delivery);

        kafkaTemplate.send(deliveryAssignedTopic, deliveryAssignedEvent.orderId(), deliveryAssignedEvent)
                .thenAccept(result ->
                        log.info("Delivery assigned event sent: deliveryId={}.", delivery.getId())
                );
    }
}
