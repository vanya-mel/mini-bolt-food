package cz.dev.vanya.miniboltfood.order.payload.response;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long itemId,
        String itemName,
        Integer quantity,
        BigDecimal priceAtPurchase
) {

}
