package cz.dev.vanya.miniboltfood.order.service.impl;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.external.PaymentHttpClient;
import cz.dev.vanya.miniboltfood.order.mapper.PaymentMapper;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.service.OrderPaymentService;
import cz.dev.vanya.miniboltfood.order.utils.OrderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderPaymentServiceImpl implements OrderPaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentHttpClient paymentHttpClient;

    @Override
    public OrderStatus makePayment(final PayOrderRequestDto payOrderRequestDto, final Order order) {
        final CreatePaymentRequestDto createPaymentRequest = paymentMapper.mapCreatePaymentRequestDto(payOrderRequestDto, order);
        final CreatePaymentResponseDto createPaymentResponseDto = paymentHttpClient.createPayment(createPaymentRequest);

        if (PaymentStatusDto.PAYMENT_INITIATED == createPaymentResponseDto.paymentStatus()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, OrderUtils.orderPaymentInProgressErrorMessage(order.getId()));
        }
        return paymentMapper.mapOrderStatus(createPaymentResponseDto.paymentStatus());
    }
}
