package cz.dev.vanya.miniboltfood.order.mapper;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface OrderEventMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "amount", source = "order.totalAmount")
    @Mapping(target = "paymentId", source = "createPaymentResponseDto.id")
    @Mapping(target = "paymentMethod", source = "createPaymentResponseDto.paymentMethod")
    OrderPaidEvent toOrderPaidEvent(Order order, CreatePaymentResponseDto createPaymentResponseDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void decorateOrder(@MappingTarget Order order, DeliveryAssignedEvent deliveryAssignedEvent);
}
