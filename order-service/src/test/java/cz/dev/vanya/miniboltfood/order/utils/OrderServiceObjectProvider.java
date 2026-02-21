package cz.dev.vanya.miniboltfood.order.utils;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderItem;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.payload.dto.OrderDto;
import cz.dev.vanya.miniboltfood.order.payload.dto.OrderItemDto;
import cz.dev.vanya.miniboltfood.order.payload.dto.enums.OrderStatusDto;
import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderItemRequestDto;
import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class OrderServiceObjectProvider {

    public CreateOrderRequestDto provideCreateOrderRequestDto() {
        return new CreateOrderRequestDto(
                OrderServiceConstantHolder.CUSTOMER_ID,
                OrderServiceConstantHolder.DESTINATION_ADDRESS,
                List.of(
                        provideCreateOrderItemRequestDto(),
                        provideSecondCreateOrderItemRequestDto()
                )
        );
    }

    public CreateOrderItemRequestDto provideCreateOrderItemRequestDto() {
        return new CreateOrderItemRequestDto(
                OrderServiceConstantHolder.ITEM_ID,
                OrderServiceConstantHolder.QUANTITY,
                OrderServiceConstantHolder.ITEM_NAME
        );
    }

    public CreateOrderItemRequestDto provideSecondCreateOrderItemRequestDto() {
        return new CreateOrderItemRequestDto(
                OrderServiceConstantHolder.SECOND_ITEM_ID,
                OrderServiceConstantHolder.SECOND_QUANTITY,
                OrderServiceConstantHolder.SECOND_ITEM_NAME
        );
    }

    public OrderItem provideOrderItem() {
        return new OrderItem(
                OrderServiceConstantHolder.ORDER_ITEM_ID,
                OrderServiceConstantHolder.ITEM_ID,
                OrderServiceConstantHolder.ITEM_NAME,
                OrderServiceConstantHolder.QUANTITY,
                OrderServiceConstantHolder.ITEM_PRICE,
                null
        );
    }

    public OrderItem provideSecondOrderItem() {
        return new OrderItem(
                OrderServiceConstantHolder.SECOND_ORDER_ITEM_ID,
                OrderServiceConstantHolder.SECOND_ITEM_ID,
                OrderServiceConstantHolder.SECOND_ITEM_NAME,
                OrderServiceConstantHolder.SECOND_QUANTITY,
                OrderServiceConstantHolder.SECOND_ITEM_PRICE,
                null
        );
    }

    public Order provideOrder(final OrderStatus orderStatus) {
        final List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(provideOrderItem());
        orderItems.add(provideSecondOrderItem());

        final Order order = new Order(
                OrderServiceConstantHolder.ORDER_ID,
                OrderServiceConstantHolder.CUSTOMER_ID,
                OrderServiceConstantHolder.DESTINATION_ADDRESS,
                null,
                OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT,
                null,
                orderStatus,
                orderItems
        );
        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        return order;
    }

    public Order provideOrderWithoutId(final OrderStatus orderStatus) {
        final Order order = provideOrder(orderStatus);
        order.setId(null);
        order.getOrderItems().forEach(orderItem -> orderItem.setId(null));
        return order;
    }

    public OrderDto provideOrderDto(final OrderStatusDto orderStatusDto) {
        return new OrderDto(
                OrderServiceConstantHolder.ORDER_ID,
                OrderServiceConstantHolder.CUSTOMER_ID,
                OrderServiceConstantHolder.DESTINATION_ADDRESS,
                OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT,
                OrderServiceConstantHolder.COURIER_NAME,
                OrderServiceConstantHolder.ETA_MINUTES,
                orderStatusDto,
                List.of(
                        provideOrderItemDto(),
                        provideSecondOrderItemDto()
                )
        );
    }

    public OrderItemDto provideOrderItemDto() {
        return new OrderItemDto(
                OrderServiceConstantHolder.ORDER_ITEM_ID,
                OrderServiceConstantHolder.ITEM_ID,
                OrderServiceConstantHolder.ITEM_NAME,
                OrderServiceConstantHolder.QUANTITY,
                OrderServiceConstantHolder.ITEM_PRICE
        );
    }

    public OrderItemDto provideSecondOrderItemDto() {
        return new OrderItemDto(
                OrderServiceConstantHolder.SECOND_ORDER_ITEM_ID,
                OrderServiceConstantHolder.SECOND_ITEM_ID,
                OrderServiceConstantHolder.SECOND_ITEM_NAME,
                OrderServiceConstantHolder.SECOND_QUANTITY,
                OrderServiceConstantHolder.SECOND_ITEM_PRICE
        );
    }

    public PayOrderRequestDto providePayOrderRequestDto() {
        return new PayOrderRequestDto(OrderServiceConstantHolder.PAYMENT_METHOD_DTO);
    }

    public CreatePaymentResponseDto provideCreatePaymentResponseDto(final PaymentStatusDto paymentStatusDto) {
        return provideCreatePaymentResponseDto(paymentStatusDto, OrderServiceConstantHolder.ORDER_ID);
    }

    public CreatePaymentResponseDto provideCreatePaymentResponseDto(final PaymentStatusDto paymentStatusDto, final Long orderId) {
        return new CreatePaymentResponseDto(
                OrderServiceConstantHolder.PAYMENT_ID,
                orderId,
                OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT,
                OrderServiceConstantHolder.PAYMENT_METHOD_DTO,
                paymentStatusDto
        );
    }

    public DeliveryAssignedEvent provideDeliveryAssignedEvent() {
        return provideDeliveryAssignedEvent(
                OrderServiceConstantHolder.ORDER_ID,
                OrderServiceConstantHolder.COURIER_NAME,
                OrderServiceConstantHolder.ETA_MINUTES
        );
    }

    public DeliveryAssignedEvent provideDeliveryAssignedEvent(final Long orderId,
                                                              final String courierName,
                                                              final Integer etaMinutes) {
        return new DeliveryAssignedEvent(
                orderId,
                courierName,
                etaMinutes
        );
    }
}
