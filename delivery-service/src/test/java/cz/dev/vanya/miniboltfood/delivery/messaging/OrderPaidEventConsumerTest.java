package cz.dev.vanya.miniboltfood.delivery.messaging;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.delivery.service.DeliveryEventService;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderPaidEventConsumerTest {

    @Mock
    private DeliveryEventService deliveryEventService;

    @InjectMocks
    private OrderPaidEventConsumer orderPaidEventConsumer;

    @Test
    void listen_delegatesToDeliveryEventService() {
        final OrderPaidEvent orderPaidEvent = DeliveryServiceObjectProvider.provideOrderPaidEvent();

        orderPaidEventConsumer.listen(orderPaidEvent);

        verify(deliveryEventService).processOrderPaid(orderPaidEvent);
    }
}
