package cz.dev.vanya.miniboltfood.order.service.impl;

import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.mapper.OrderMapper;
import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.payload.response.OrderDto;
import cz.dev.vanya.miniboltfood.order.repository.OrderRepository;
import cz.dev.vanya.miniboltfood.order.service.OrderCalculationService;
import cz.dev.vanya.miniboltfood.order.service.OrderService;
import cz.dev.vanya.miniboltfood.order.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderCalculationService orderCalculationService;


    @Override
    @Transactional
    public OrderDto createOrder(final CreateOrderRequestDto createOrderRequestDto) {
        final Order order = orderMapper.mapToOrder(createOrderRequestDto);

        final BigDecimal totalAmount = orderCalculationService.calculateOrderPrice(order);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        final Order savedOrder = orderRepository.save(order);
        return orderMapper.mapToOrderDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(final Long orderId) {
        return orderRepository.findWithOrderItemsById(orderId)
                .map(orderMapper::mapToOrderDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, OrderUtils.orderNotFoundByIdErrorMessage(orderId)));
    }
}
