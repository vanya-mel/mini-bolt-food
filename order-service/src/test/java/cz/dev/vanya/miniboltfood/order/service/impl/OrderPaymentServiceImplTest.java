package cz.dev.vanya.miniboltfood.order.service.impl;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.external.PaymentHttpClient;
import cz.dev.vanya.miniboltfood.order.mapper.PaymentMapper;
import cz.dev.vanya.miniboltfood.order.messaging.OrderPaidEventClient;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPaymentServiceImplTest {

    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PaymentHttpClient paymentHttpClient;
    @Mock
    private OrderPaidEventClient orderPaidEventClient;

    @InjectMocks
    private OrderPaymentServiceImpl orderPaymentService;

    @Test
    void makePayment_whenPaymentInitiated_throwsConflict() {
        // given
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PENDING_PAYMENT);
        final PayOrderRequestDto payOrderRequestDto = OrderServiceObjectProvider.providePayOrderRequestDto();
        final CreatePaymentRequestDto createPaymentRequestDto = new CreatePaymentRequestDto(
                order.getId(),
                order.getTotalAmount(),
                payOrderRequestDto.paymentMethod()
        );
        final CreatePaymentResponseDto paymentResponseDto =
                OrderServiceObjectProvider.provideCreatePaymentResponseDto(PaymentStatusDto.PAYMENT_INITIATED);

        when(paymentMapper.mapCreatePaymentRequestDto(payOrderRequestDto, order)).thenReturn(createPaymentRequestDto);
        when(paymentHttpClient.createPayment(createPaymentRequestDto)).thenReturn(paymentResponseDto);

        // when, then
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> orderPaymentService.makePayment(payOrderRequestDto, order))
                .satisfies(throwable -> {
                    assertEquals(HttpStatus.CONFLICT, throwable.getStatusCode());
                    assertThat(throwable.getReason()).contains(order.getId().toString());
                });

        verify(orderPaidEventClient, never()).sendOrderPaidEvent(order, paymentResponseDto);
    }

    @ParameterizedTest
    @MethodSource("provideStatuses")
    void makePayment_whenPaymentFinished_mapsStatusAndDelegatesEventPublish(final PaymentStatusDto paymentStatus,
                                                                            final OrderStatus orderStatus) {
        // given
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PENDING_PAYMENT);
        final PayOrderRequestDto payOrderRequestDto = OrderServiceObjectProvider.providePayOrderRequestDto();
        final CreatePaymentRequestDto createPaymentRequestDto = new CreatePaymentRequestDto(
                order.getId(),
                order.getTotalAmount(),
                payOrderRequestDto.paymentMethod()
        );
        final CreatePaymentResponseDto paymentResponseDto =
                OrderServiceObjectProvider.provideCreatePaymentResponseDto(paymentStatus);

        when(paymentMapper.mapCreatePaymentRequestDto(payOrderRequestDto, order)).thenReturn(createPaymentRequestDto);
        when(paymentHttpClient.createPayment(createPaymentRequestDto)).thenReturn(paymentResponseDto);
        when(paymentMapper.mapOrderStatus(paymentStatus)).thenReturn(orderStatus);

        // when
        final OrderStatus result = orderPaymentService.makePayment(payOrderRequestDto, order);

        // then
        assertEquals(orderStatus, result);
        verify(orderPaidEventClient).sendOrderPaidEvent(order, paymentResponseDto);
    }

    private static Stream<Arguments> provideStatuses() {
        return Stream.of(
                Arguments.of(PaymentStatusDto.PAYMENT_FAILED, OrderStatus.PAYMENT_FAILED),
                Arguments.of(PaymentStatusDto.PAYMENT_SUCCEEDED, OrderStatus.PAID)
        );
    }
}
