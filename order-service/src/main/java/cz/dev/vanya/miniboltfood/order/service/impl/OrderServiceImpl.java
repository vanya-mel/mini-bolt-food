package cz.dev.vanya.miniboltfood.order.service.impl;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.mapper.OrderEventMapper;
import cz.dev.vanya.miniboltfood.order.mapper.OrderMapper;
import cz.dev.vanya.miniboltfood.order.payload.dto.OrderDto;
import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.repository.OrderRepository;
import cz.dev.vanya.miniboltfood.order.service.OrderCalculationService;
import cz.dev.vanya.miniboltfood.order.service.OrderPaymentService;
import cz.dev.vanya.miniboltfood.order.service.OrderService;
import cz.dev.vanya.miniboltfood.order.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderEventMapper orderEventMapper;
    private final OrderPaymentService orderPaymentService;
    private final OrderCalculationService orderCalculationService;


    @Override
    @Transactional
    public OrderDto createOrder(final CreateOrderRequestDto createOrderRequestDto) {
        final Order order = orderMapper.mapToOrder(createOrderRequestDto);

        final BigDecimal totalAmount = orderCalculationService.calculateOrderPrice(order);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        return saveAndMapOrderDto(order);
    }

    private OrderDto saveAndMapOrderDto(final Order order) {
        final Order savedOrder = orderRepository.save(order);
        return orderMapper.mapToOrderDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(final Long orderId) {
        final Order order = getOrderByIdOrThrow(orderId);
        return orderMapper.mapToOrderDto(order);
    }

    private Order getOrderByIdOrThrow(final Long orderId) {
        return orderRepository.findWithOrderItemsById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, OrderUtils.orderNotFoundByIdErrorMessage(orderId)));
    }

    @Override
    @Transactional
    public OrderDto payOrder(final PayOrderRequestDto payOrderRequestDto, final Long orderId) {
        final Order order = getOrderByIdOrThrow(orderId);

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    OrderUtils.orderPaymentInWrongStateErrorMessage(orderId, order.getOrderStatus()));
        }

        final OrderStatus orderStatus = orderPaymentService.makePayment(payOrderRequestDto, order);
        order.setOrderStatus(orderStatus);

        return saveAndMapOrderDto(order);
    }

    @Override
    @Transactional
    public void processDeliveryAssigned(final DeliveryAssignedEvent deliveryAssignedEvent) {
        final Order order = getOrderByIdOrThrow(deliveryAssignedEvent.orderId());
        if (order.getOrderStatus() != OrderStatus.PAID) {
            processIncorrectDeliveryState(order);
            return;
        }
        order.setOrderStatus(OrderStatus.PENDING_DELIVERY);
        orderEventMapper.decorateOrder(order, deliveryAssignedEvent);

        orderRepository.save(order);
        log.info("Order delivery assigned processed: orderId={}", order.getId());
    }

    private void processIncorrectDeliveryState(final Order order) {
        if (order.getOrderStatus() == OrderStatus.PENDING_DELIVERY || order.getOrderStatus() == OrderStatus.DELIVERED) {
            log.info("Order delivery already processed: orderId={}.", order.getId());
        } else {
            log.error("Trying to assign delivery but order [{}] have incorrect state: state={}.", order.getId(), order.getOrderStatus());
        }
    }

    @Override
    @Transactional
    public OrderDto closeOrder(final Long orderId) {
        final Order order = getOrderByIdOrThrow(orderId);
        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            log.info("Order already delivered: orderId={}.", orderId);
            return orderMapper.mapToOrderDto(order);
        }
        if (order.getOrderStatus() != OrderStatus.PENDING_DELIVERY) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    OrderUtils.orderDeliveryWrongStatusErrorMessage(orderId, order.getOrderStatus()));
        }
        order.setOrderStatus(OrderStatus.DELIVERED);
        final Order deliveredOrder = orderRepository.save(order);
        return orderMapper.mapToOrderDto(deliveredOrder);
    }
}
