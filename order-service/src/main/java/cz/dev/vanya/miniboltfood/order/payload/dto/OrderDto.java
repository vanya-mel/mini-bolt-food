package cz.dev.vanya.miniboltfood.order.payload.dto;

import cz.dev.vanya.miniboltfood.order.payload.dto.enums.OrderStatusDto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        Long id,
        Long customerId,
        String destinationAddress,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatusDto orderStatus,
        List<OrderItemDto> orderItems
) {
}
