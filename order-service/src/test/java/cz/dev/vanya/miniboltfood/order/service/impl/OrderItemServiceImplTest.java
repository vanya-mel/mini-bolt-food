package cz.dev.vanya.miniboltfood.order.service.impl;

import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class OrderItemServiceImplTest {

    private final OrderItemServiceImpl orderItemService = new OrderItemServiceImpl();

    @Test
    void getOrderItemCurrentPrice_roundsToTwoDecimals() {
        final ThreadLocalRandom threadLocalRandom = mock(ThreadLocalRandom.class);
        when(threadLocalRandom.nextDouble(100, 5000)).thenReturn(OrderServiceConstantHolder.ITEM_PRICE.doubleValue());

        try (MockedStatic<ThreadLocalRandom> mockedStatic = mockStatic(ThreadLocalRandom.class)) {
            mockedStatic.when(ThreadLocalRandom::current).thenReturn(threadLocalRandom);

            final BigDecimal result = orderItemService.getOrderItemCurrentPrice(1L);

            assertEquals(OrderServiceConstantHolder.ITEM_PRICE, result);
        }
    }
}
