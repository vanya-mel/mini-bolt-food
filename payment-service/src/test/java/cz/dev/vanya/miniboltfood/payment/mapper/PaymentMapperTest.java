package cz.dev.vanya.miniboltfood.payment.mapper;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentServiceConstantHolder;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentMapperTest {

    private final PaymentMapper paymentMapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void mapToPayment_mapsRequestFields() {
        final CreatePaymentRequestDto request = PaymentServiceObjectProvider.provideCreatePaymentRequestDto();

        final Payment result = paymentMapper.mapToPayment(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getPaymentStatus());
        assertEquals(PaymentServiceConstantHolder.AMOUNT, result.getAmount());
        assertEquals(PaymentServiceConstantHolder.ORDER_ID, result.getOrderId());
        assertEquals(PaymentServiceConstantHolder.PAYMENT_METHOD, result.getPaymentMethod());
    }

    @Test
    void mapToCreatePaymentResponseDto_mapsPaymentFields() {
        final Payment payment = PaymentServiceObjectProvider.providePayment();

        final CreatePaymentResponseDto result = paymentMapper.mapToCreatePaymentResponseDto(payment);

        assertNotNull(result);
        assertEquals(PaymentServiceConstantHolder.PAYMENT_ID, result.id());
        assertEquals(PaymentServiceConstantHolder.ORDER_ID, result.orderId());
        assertEquals(PaymentServiceConstantHolder.AMOUNT, result.amount());
        assertEquals(PaymentServiceConstantHolder.PAYMENT_METHOD_DTO, result.paymentMethod());
        assertEquals(PaymentServiceConstantHolder.PAYMENT_STATUS_DTO, result.paymentStatus());
    }

    @Test
    void mapToCreatePaymentResponseDto_whenSourceIsNull_returnsNull() {
        final CreatePaymentResponseDto result = paymentMapper.mapToCreatePaymentResponseDto(null);

        assertNull(result);
    }
}
