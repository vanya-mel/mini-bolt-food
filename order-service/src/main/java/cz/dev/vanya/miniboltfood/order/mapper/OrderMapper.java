package cz.dev.vanya.miniboltfood.order.mapper;

import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderItem;
import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.payload.dto.OrderDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between API DTOs and the order domain model.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface OrderMapper {

    /**
     * Maps the create request DTO to a new {@link Order} entity.
     */
    Order mapToOrder(CreateOrderRequestDto createOrderRequestDto);

    /**
     * Links {@link OrderItem} entities to their parent {@link Order} after mapping.
     */
    @AfterMapping
    default void linkOrderItems(@MappingTarget Order order) {
        if (order.getOrderItems() == null) {
            return;
        }
        order.getOrderItems()
                .forEach(orderItem -> orderItem.setOrder(order));
    }

    /**
     * Maps an {@link Order} entity to the response DTO.
     */
    OrderDto mapToOrderDto(Order order);
}
