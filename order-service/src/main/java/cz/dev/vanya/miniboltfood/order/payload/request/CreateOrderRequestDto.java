package cz.dev.vanya.miniboltfood.order.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateOrderRequestDto(

        @NotNull
        @Positive
        Long customerId,

        @NotBlank
        String destinationAddress,

        @NotEmpty
        List<@Valid CreateOrderItemRequestDto> orderItems
) {
}
