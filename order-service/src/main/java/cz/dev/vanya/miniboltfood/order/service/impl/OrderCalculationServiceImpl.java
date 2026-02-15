package cz.dev.vanya.miniboltfood.order.service.impl;

import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.service.OrderCalculationService;
import cz.dev.vanya.miniboltfood.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderCalculationServiceImpl implements OrderCalculationService {

    private final OrderItemService orderItemService;

    @Override
    public BigDecimal calculateOrderPrice(final Order order) {
        return order.getOrderItems().stream()
                .peek(orderItem -> {
                    final BigDecimal itemPurchasePrice = orderItemService.getOrderItemCurrentPrice(orderItem.getItemId());
                    orderItem.setPriceAtPurchase(itemPurchasePrice);
                })
                .map(orderItem -> orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
