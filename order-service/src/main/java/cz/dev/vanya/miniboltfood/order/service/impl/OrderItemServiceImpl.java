package cz.dev.vanya.miniboltfood.order.service.impl;

import cz.dev.vanya.miniboltfood.order.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    @Override
    public BigDecimal getOrderItemCurrentPrice(final Long itemId) {
        // Simulating logic when we load/calculate its current price by itemId.
        return BigDecimal
                .valueOf(ThreadLocalRandom.current().nextDouble(100, 5000))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
