package cz.dev.vanya.miniboltfood.order.messaging;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.order.service.OrderService;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryAssignedEventConsumerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private DeliveryAssignedEventConsumer deliveryAssignedEventConsumer;

    @Test
    void listen_delegatesToOrderService() {
        final DeliveryAssignedEvent deliveryAssignedEvent = OrderServiceObjectProvider.provideDeliveryAssignedEvent();
        final ConsumerRecord<Long, DeliveryAssignedEvent> consumerRecord = new ConsumerRecord<>(
                OrderServiceConstantHolder.DELIVERY_ASSIGNED_TOPIC,
                0,
                0L,
                OrderServiceConstantHolder.ORDER_ID,
                deliveryAssignedEvent
        );

        deliveryAssignedEventConsumer.listen(consumerRecord);

        verify(orderService).processDeliveryAssigned(deliveryAssignedEvent);
    }
}
