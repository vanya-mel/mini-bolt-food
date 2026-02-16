package cz.dev.vanya.miniboltfood.commonlibs.messaging.event;

public record DeliveryAssignedEvent(
        Long orderId,
        String courierName,
        Integer etaMinutes
) { }
