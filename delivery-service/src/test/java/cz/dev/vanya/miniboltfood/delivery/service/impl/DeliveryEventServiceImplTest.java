package cz.dev.vanya.miniboltfood.delivery.service.impl;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import cz.dev.vanya.miniboltfood.delivery.messaging.DeliveryAssignedEventClient;
import cz.dev.vanya.miniboltfood.delivery.service.DeliveryService;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceConstantHolder;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryEventServiceImplTest {

    @Mock
    private DeliveryService deliveryService;
    @Mock
    private DeliveryAssignedEventClient deliveryAssignedEventClient;

    @InjectMocks
    private DeliveryEventServiceImpl deliveryEventService;

    @Test
    void processOrderPaid_whenDeliveryAlreadyAssigned_doesNothing() {
        final Delivery savedDelivery = DeliveryServiceObjectProvider.provideSavedDelivery();
        final OrderPaidEvent orderPaidEvent = DeliveryServiceObjectProvider.provideOrderPaidEvent();

        when(deliveryService.findByOrderId(DeliveryServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(savedDelivery));

        deliveryEventService.processOrderPaid(orderPaidEvent);

        verify(deliveryService).findByOrderId(DeliveryServiceConstantHolder.ORDER_ID);
        verify(deliveryService, never()).assignDelivery(DeliveryServiceConstantHolder.ORDER_ID);
        verifyNoInteractions(deliveryAssignedEventClient);
    }

    @Test
    void processOrderPaid_whenDeliveryDoesNotExist_assignsAndPublishesEvent() {
        final Delivery savedDelivery = DeliveryServiceObjectProvider.provideSavedDelivery();
        final OrderPaidEvent orderPaidEvent = DeliveryServiceObjectProvider.provideOrderPaidEvent();

        when(deliveryService.findByOrderId(DeliveryServiceConstantHolder.ORDER_ID)).thenReturn(Optional.empty());
        when(deliveryService.assignDelivery(DeliveryServiceConstantHolder.ORDER_ID)).thenReturn(savedDelivery);

        deliveryEventService.processOrderPaid(orderPaidEvent);

        verify(deliveryService).findByOrderId(DeliveryServiceConstantHolder.ORDER_ID);
        verify(deliveryService).assignDelivery(DeliveryServiceConstantHolder.ORDER_ID);
        verify(deliveryAssignedEventClient).sendDeliveryAssignedEvent(savedDelivery);
    }

    @Test
    void processOrderPaid_whenAssignDeliveryFailsWithConcurrentConflict_skipsEventPublish() {
        final OrderPaidEvent orderPaidEvent = DeliveryServiceObjectProvider.provideOrderPaidEvent();

        when(deliveryService.findByOrderId(DeliveryServiceConstantHolder.ORDER_ID)).thenReturn(Optional.empty());
        when(deliveryService.assignDelivery(DeliveryServiceConstantHolder.ORDER_ID))
                .thenThrow(new DataIntegrityViolationException("Duplicate delivery for order."));

        deliveryEventService.processOrderPaid(orderPaidEvent);

        verify(deliveryService).findByOrderId(DeliveryServiceConstantHolder.ORDER_ID);
        verify(deliveryService).assignDelivery(DeliveryServiceConstantHolder.ORDER_ID);
        verifyNoInteractions(deliveryAssignedEventClient);
    }
}
