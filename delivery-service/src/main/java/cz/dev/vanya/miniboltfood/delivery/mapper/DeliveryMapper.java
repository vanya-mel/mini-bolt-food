package cz.dev.vanya.miniboltfood.delivery.mapper;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface DeliveryMapper {

    DeliveryAssignedEvent mapToDeliveryAssignedEvent(Delivery delivery);
}
