package cz.dev.vanya.miniboltfood.order.service.impl;

import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderItem;
import cz.dev.vanya.miniboltfood.order.service.OrderItemService;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCalculationServiceImplTest {

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderCalculationServiceImpl orderCalculationService;

    @Test
    void calculateOrderPrice_setsPriceAtPurchaseAndReturnsTotal() {
        final Order order = OrderServiceObjectProvider.provideOrder(OrderServiceConstantHolder.ORDER_STATUS_PENDING_PAYMENT);

        when(orderItemService.getOrderItemCurrentPrice(OrderServiceConstantHolder.ITEM_ID))
                .thenReturn(OrderServiceConstantHolder.ITEM_PRICE);
        when(orderItemService.getOrderItemCurrentPrice(OrderServiceConstantHolder.SECOND_ITEM_ID))
                .thenReturn(OrderServiceConstantHolder.SECOND_ITEM_PRICE);

        final BigDecimal result = orderCalculationService.calculateOrderPrice(order);

        assertEquals(OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT, result);
        assertThat(order.getOrderItems())
                .extracting(OrderItem::getPriceAtPurchase)
                .containsExactly(OrderServiceConstantHolder.ITEM_PRICE, OrderServiceConstantHolder.SECOND_ITEM_PRICE);

        verify(orderItemService).getOrderItemCurrentPrice(OrderServiceConstantHolder.ITEM_ID);
        verify(orderItemService).getOrderItemCurrentPrice(OrderServiceConstantHolder.SECOND_ITEM_ID);
    }
}
