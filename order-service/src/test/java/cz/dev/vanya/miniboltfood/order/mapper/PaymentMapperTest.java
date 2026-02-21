package cz.dev.vanya.miniboltfood.order.mapper;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentMapperTest {

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void mapCreatePaymentRequestDto_mapsOrderAndRequestData() {
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PENDING_PAYMENT);
        final PayOrderRequestDto payOrderRequestDto = OrderServiceObjectProvider.providePayOrderRequestDto();

        final CreatePaymentRequestDto result = paymentMapper.mapCreatePaymentRequestDto(payOrderRequestDto, order);

        assertNotNull(result);
        assertEquals(OrderServiceConstantHolder.ORDER_ID, result.orderId());
        assertEquals(OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT, result.amount());
        assertEquals(OrderServiceConstantHolder.PAYMENT_METHOD_DTO, result.paymentMethod());
    }

    @ParameterizedTest
    @MethodSource("provideStatuses")
    void mapOrderStatus_mapsSupportedStatuses(final PaymentStatusDto paymentStatus, final OrderStatus expectedResult) {
        assertEquals(expectedResult, paymentMapper.mapOrderStatus(paymentStatus));
    }

    private static Stream<Arguments> provideStatuses() {
        return Stream.of(
                Arguments.of(PaymentStatusDto.PAYMENT_SUCCEEDED, OrderStatus.PAID),
                Arguments.of(PaymentStatusDto.PAYMENT_FAILED, OrderStatus.PAYMENT_FAILED),
                Arguments.of(PaymentStatusDto.REFUNDED, OrderStatus.PAYMENT_FAILED)

        );
    }

    @Test
    void mapOrderStatus_whenInitiated_throwsException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> paymentMapper.mapOrderStatus(PaymentStatusDto.PAYMENT_INITIATED));
    }
}
