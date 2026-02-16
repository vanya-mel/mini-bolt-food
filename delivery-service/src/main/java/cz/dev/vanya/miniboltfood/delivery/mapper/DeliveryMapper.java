package cz.dev.vanya.miniboltfood.delivery.mapper;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Maps delivery domain objects to messaging events.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface DeliveryMapper {

    /**
     * Maps a {@link Delivery} entity to a {@link DeliveryAssignedEvent} message.
     *
     * @param delivery delivery entity
     * @return delivery assigned event
     */
    DeliveryAssignedEvent mapToDeliveryAssignedEvent(Delivery delivery);
}
