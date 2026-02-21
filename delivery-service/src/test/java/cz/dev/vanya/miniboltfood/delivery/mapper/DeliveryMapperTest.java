package cz.dev.vanya.miniboltfood.delivery.mapper;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceConstantHolder;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DeliveryMapperTest {

    private final DeliveryMapper deliveryMapper = Mappers.getMapper(DeliveryMapper.class);

    @Test
    void mapToDeliveryAssignedEvent_mapsAllRelevantFields() {
        final Delivery savedDelivery = DeliveryServiceObjectProvider.provideSavedDelivery();

        final DeliveryAssignedEvent result = deliveryMapper.mapToDeliveryAssignedEvent(savedDelivery);

        assertNotNull(result);
        assertEquals(DeliveryServiceConstantHolder.ORDER_ID, result.orderId());
        assertEquals(DeliveryServiceConstantHolder.COURIER_NAME, result.courierName());
        assertEquals(DeliveryServiceConstantHolder.ETA_MINUTES, result.etaMinutes());
    }

    @Test
    void mapToDeliveryAssignedEvent_whenSourceIsNull_returnsNull() {
        final DeliveryAssignedEvent result = deliveryMapper.mapToDeliveryAssignedEvent(null);

        assertNull(result);
    }
}
