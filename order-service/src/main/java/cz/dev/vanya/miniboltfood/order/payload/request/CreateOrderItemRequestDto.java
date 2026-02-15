package cz.dev.vanya.miniboltfood.order.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderItemRequestDto(

        @NotNull
        @Positive
        Long itemId,

        @NotNull
        @Positive
        Integer quantity,

        String itemName
) {
}
