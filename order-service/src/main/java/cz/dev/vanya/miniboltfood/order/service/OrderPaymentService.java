package cz.dev.vanya.miniboltfood.order.service;

import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;

public interface OrderPaymentService {

    OrderStatus makePayment(PayOrderRequestDto payOrderRequestDto, Order order);
}
