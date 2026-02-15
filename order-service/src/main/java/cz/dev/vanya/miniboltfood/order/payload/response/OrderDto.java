package cz.dev.vanya.miniboltfood.order.payload.response;

import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        Long id,
        Long customerId,
        String destinationAddress,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        List<OrderItemDto> orderItems
) {
}
