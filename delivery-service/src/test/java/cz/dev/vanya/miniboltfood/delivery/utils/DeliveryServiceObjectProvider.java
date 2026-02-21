package cz.dev.vanya.miniboltfood.delivery.utils;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DeliveryServiceObjectProvider {

    public Delivery provideSavedDelivery() {
        final Delivery delivery = new Delivery();

        delivery.setId(DeliveryServiceConstantHolder.DELIVERY_ID);
        delivery.setOrderId(DeliveryServiceConstantHolder.ORDER_ID);
        delivery.setEtaMinutes(DeliveryServiceConstantHolder.ETA_MINUTES);
        delivery.setCourierName(DeliveryServiceConstantHolder.COURIER_NAME);

        return delivery;
    }

    public OrderPaidEvent provideOrderPaidEvent() {
        return provideOrderPaidEvent(DeliveryServiceConstantHolder.ORDER_ID);
    }

    public OrderPaidEvent provideOrderPaidEvent(final Long orderId) {
        return new OrderPaidEvent(
                orderId,
                DeliveryServiceConstantHolder.PAYMENT_ID,
                DeliveryServiceConstantHolder.PAYMENT_AMOUNT,
                DeliveryServiceConstantHolder.PAYMENT_METHOD
        );
    }

    public DeliveryAssignedEvent provideDeliveryAssignedEvent() {
        return new DeliveryAssignedEvent(
                DeliveryServiceConstantHolder.ORDER_ID,
                DeliveryServiceConstantHolder.COURIER_NAME,
                DeliveryServiceConstantHolder.ETA_MINUTES
        );
    }
}
