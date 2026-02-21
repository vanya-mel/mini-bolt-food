package cz.dev.vanya.miniboltfood.order.service.impl;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.mapper.OrderEventMapper;
import cz.dev.vanya.miniboltfood.order.mapper.OrderMapper;
import cz.dev.vanya.miniboltfood.order.payload.dto.OrderDto;
import cz.dev.vanya.miniboltfood.order.payload.dto.enums.OrderStatusDto;
import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.repository.OrderRepository;
import cz.dev.vanya.miniboltfood.order.service.OrderCalculationService;
import cz.dev.vanya.miniboltfood.order.service.OrderPaymentService;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderEventMapper orderEventMapper;
    @Mock
    private OrderPaymentService orderPaymentService;
    @Mock
    private OrderCalculationService orderCalculationService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_setsInitialStatusAndPriceAndReturnsMappedDto() {
        // given
        final CreateOrderRequestDto request = OrderServiceObjectProvider.provideCreateOrderRequestDto();
        final Order order = OrderServiceObjectProvider.provideOrderWithoutId(null);
        final Order savedOrder = OrderServiceObjectProvider.provideOrder(OrderStatus.PENDING_PAYMENT);
        final OrderDto orderDto = OrderServiceObjectProvider.provideOrderDto(OrderStatusDto.PENDING_PAYMENT);

        when(orderMapper.mapToOrder(request)).thenReturn(order);
        when(orderCalculationService.calculateOrderPrice(order)).thenReturn(OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(orderMapper.mapToOrderDto(savedOrder)).thenReturn(orderDto);

        // when
        final OrderDto result = orderService.createOrder(request);

        // then
        assertEquals(orderDto, result);
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getOrderStatus());
        assertEquals(OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT, order.getTotalAmount());
    }

    @Test
    void getOrderById_whenOrderExists_returnsMappedDto() {
        // given
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PENDING_PAYMENT);
        final OrderDto orderDto = OrderServiceObjectProvider.provideOrderDto(OrderStatusDto.PENDING_PAYMENT);

        when(orderRepository.findWithOrderItemsById(OrderServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(order));
        when(orderMapper.mapToOrderDto(order)).thenReturn(orderDto);

        // when
        final OrderDto result = orderService.getOrderById(OrderServiceConstantHolder.ORDER_ID);

        // then
        assertEquals(orderDto, result);
    }

    @Test
    void getOrderById_whenOrderMissing_throwsNotFound() {
        // given
        when(orderRepository.findWithOrderItemsById(OrderServiceConstantHolder.ORDER_ID)).thenReturn(Optional.empty());

        // when, then
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> orderService.getOrderById(OrderServiceConstantHolder.ORDER_ID))
                .satisfies(throwable -> {
                    assertEquals(HttpStatus.NOT_FOUND, throwable.getStatusCode());
                    assertThat(throwable.getReason()).contains(OrderServiceConstantHolder.ORDER_ID.toString());
                });
    }

    @Test
    void payOrder_whenOrderInWrongState_throwsConflict() {
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.DELIVERED);
        final PayOrderRequestDto payOrderRequestDto = OrderServiceObjectProvider.providePayOrderRequestDto();

        when(orderRepository.findWithOrderItemsById(OrderServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(order));

        // when, then
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> orderService.payOrder(payOrderRequestDto, OrderServiceConstantHolder.ORDER_ID))
                .satisfies(throwable -> {
                    assertEquals(HttpStatus.CONFLICT, throwable.getStatusCode());
                    assertThat(throwable.getReason()).contains(order.getId().toString(),
                            order.getOrderStatus().toString());
                });

        verify(orderPaymentService, never()).makePayment(payOrderRequestDto, order);
    }

    @Test
    void payOrder_whenOrderPendingPayment_updatesStatusAndReturnsDto() {
        // given
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PENDING_PAYMENT);
        final PayOrderRequestDto payOrderRequestDto = OrderServiceObjectProvider.providePayOrderRequestDto();
        final OrderDto paidOrderDto = OrderServiceObjectProvider.provideOrderDto(OrderStatusDto.PAID);

        when(orderRepository.findWithOrderItemsById(OrderServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(order));
        when(orderPaymentService.makePayment(payOrderRequestDto, order)).thenReturn(OrderStatus.PAID);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.mapToOrderDto(order)).thenReturn(paidOrderDto);

        // when
        final OrderDto result = orderService.payOrder(payOrderRequestDto, OrderServiceConstantHolder.ORDER_ID);

        // then
        assertEquals(paidOrderDto, result);
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
    }

    @Test
    void processDeliveryAssigned_whenOrderPaid_decoratesAndSavesOrder() {
        // given
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PAID);
        final DeliveryAssignedEvent deliveryAssignedEvent = OrderServiceObjectProvider.provideDeliveryAssignedEvent();

        when(orderRepository.findWithOrderItemsById(OrderServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(order));

        // when
        orderService.processDeliveryAssigned(deliveryAssignedEvent);

        // then
        assertEquals(OrderStatus.PENDING_DELIVERY, order.getOrderStatus());

        verify(orderEventMapper).decorateOrder(order, deliveryAssignedEvent);
        verify(orderRepository).save(order);
    }

    @Test
    void processDeliveryAssigned_whenOrderNotPaid_skipsSave() {
        // given
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.DELIVERED);
        final DeliveryAssignedEvent deliveryAssignedEvent = OrderServiceObjectProvider.provideDeliveryAssignedEvent();

        when(orderRepository.findWithOrderItemsById(OrderServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(order));

        // when
        orderService.processDeliveryAssigned(deliveryAssignedEvent);

        // the
        verify(orderEventMapper, never()).decorateOrder(order, deliveryAssignedEvent);
        verify(orderRepository, never()).save(order);
    }

    @Test
    void closeOrder_whenAlreadyDelivered_returnsExistingMappedOrder() {
        // given
        final Order deliveredOrder = OrderServiceObjectProvider.provideOrder(OrderStatus.DELIVERED);
        final OrderDto deliveredOrderDto = OrderServiceObjectProvider.provideOrderDto(OrderStatusDto.DELIVERED);

        when(orderRepository.findWithOrderItemsById(OrderServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(deliveredOrder));
        when(orderMapper.mapToOrderDto(deliveredOrder)).thenReturn(deliveredOrderDto);

        // when
        final OrderDto result = orderService.closeOrder(OrderServiceConstantHolder.ORDER_ID);

        // then
        assertEquals(deliveredOrderDto, result);

        verify(orderRepository, never()).save(deliveredOrder);
    }

    @Test
    void closeOrder_whenWrongState_throwsConflict() {
        // given
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PAID);

        when(orderRepository.findWithOrderItemsById(OrderServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(order));

        // when, then
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> orderService.closeOrder(OrderServiceConstantHolder.ORDER_ID))
                .satisfies(throwable -> {
                    assertEquals(HttpStatus.CONFLICT, throwable.getStatusCode());
                    assertThat(throwable.getReason()).contains(order.getId().toString(),
                            order.getOrderStatus().toString());
                });
    }

    @Test
    void closeOrder_whenPendingDelivery_marksDeliveredAndSaves() {
        // given
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PENDING_DELIVERY);
        final OrderDto deliveredOrderDto = OrderServiceObjectProvider.provideOrderDto(OrderStatusDto.DELIVERED);

        when(orderRepository.findWithOrderItemsById(OrderServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.mapToOrderDto(order)).thenReturn(deliveredOrderDto);

        // when
        final OrderDto result = orderService.closeOrder(OrderServiceConstantHolder.ORDER_ID);

        // then
        assertEquals(deliveredOrderDto, result);
        assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());

        verify(orderRepository).save(order);
    }
}
