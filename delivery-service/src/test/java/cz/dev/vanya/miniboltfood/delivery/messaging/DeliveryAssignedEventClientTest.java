package cz.dev.vanya.miniboltfood.delivery.messaging;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import cz.dev.vanya.miniboltfood.delivery.mapper.DeliveryMapper;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceConstantHolder;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryAssignedEventClientTest {

    @Mock
    private DeliveryMapper deliveryMapper;
    @Mock
    private KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;

    @InjectMocks
    private DeliveryAssignedEventClient deliveryAssignedEventClient;

    @Test
    void sendDeliveryAssignedEvent_mapsAndSendsToConfiguredTopic() {
        // given
        final Delivery savedDelivery = DeliveryServiceObjectProvider.provideSavedDelivery();
        final DeliveryAssignedEvent deliveryAssignedEvent = DeliveryServiceObjectProvider.provideDeliveryAssignedEvent();
        ReflectionTestUtils.setField(
                deliveryAssignedEventClient,
                "deliveryAssignedTopic",
                DeliveryServiceConstantHolder.DELIVERY_ASSIGNED_TOPIC
        );

        when(deliveryMapper.mapToDeliveryAssignedEvent(savedDelivery)).thenReturn(deliveryAssignedEvent);
        when(kafkaTemplate.send(
                DeliveryServiceConstantHolder.DELIVERY_ASSIGNED_TOPIC,
                DeliveryServiceConstantHolder.ORDER_ID,
                deliveryAssignedEvent
        )).thenReturn(CompletableFuture.completedFuture(null));

        // when
        deliveryAssignedEventClient.sendDeliveryAssignedEvent(savedDelivery);

        // then
        verify(deliveryMapper).mapToDeliveryAssignedEvent(savedDelivery);
        verify(kafkaTemplate).send(
                DeliveryServiceConstantHolder.DELIVERY_ASSIGNED_TOPIC,
                DeliveryServiceConstantHolder.ORDER_ID,
                deliveryAssignedEvent
        );
    }
}
