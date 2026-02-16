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

/**
 * Maps order domain objects to messaging events and vice versa.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface OrderEventMapper {

    /**
     * Creates an {@link OrderPaidEvent} from an order and successful payment result.
     *
     * @param order order entity
     * @param createPaymentResponseDto payment response returned by payment-service
     * @return order paid event
     */
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "amount", source = "order.totalAmount")
    @Mapping(target = "paymentId", source = "createPaymentResponseDto.id")
    @Mapping(target = "paymentMethod", source = "createPaymentResponseDto.paymentMethod")
    OrderPaidEvent toOrderPaidEvent(Order order, CreatePaymentResponseDto createPaymentResponseDto);


    /**
     * Enriches an {@link Order} with delivery assignment data from {@link DeliveryAssignedEvent}.
     *
     * <p>
     * Uses {@link NullValuePropertyMappingStrategy#IGNORE} to avoid overwriting existing values with {@code null}.
     *
     * @param order target order entity to update
     * @param deliveryAssignedEvent delivery assignment event
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void decorateOrder(@MappingTarget Order order, DeliveryAssignedEvent deliveryAssignedEvent);
}
