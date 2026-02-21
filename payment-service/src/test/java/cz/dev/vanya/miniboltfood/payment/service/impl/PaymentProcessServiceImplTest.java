package cz.dev.vanya.miniboltfood.payment.service.impl;

import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentStatus;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class PaymentProcessServiceImplTest {

    private final PaymentProcessServiceImpl paymentProcessService = new PaymentProcessServiceImpl();

    @Test
    void processPayment_whenCardAndProbabilityBelowThreshold_returnsSucceeded() {
        final Payment payment = PaymentServiceObjectProvider.providePaymentWithoutId();
        final ThreadLocalRandom threadLocalRandom = mock(ThreadLocalRandom.class);
        when(threadLocalRandom.nextInt(0, 100)).thenReturn(97);

        try (MockedStatic<ThreadLocalRandom> mockedStatic = mockStatic(ThreadLocalRandom.class)) {
            mockedStatic.when(ThreadLocalRandom::current).thenReturn(threadLocalRandom);

            final PaymentStatus result = paymentProcessService.processPayment(payment);

            assertEquals(PaymentStatus.PAYMENT_SUCCEEDED, result);
        }
    }

    @Test
    void processPayment_whenCardAndProbabilityEqualsThreshold_returnsFailed() {
        final Payment payment = PaymentServiceObjectProvider.providePaymentWithoutId();
        final ThreadLocalRandom threadLocalRandom = mock(ThreadLocalRandom.class);
        when(threadLocalRandom.nextInt(0, 100)).thenReturn(98);

        try (MockedStatic<ThreadLocalRandom> mockedStatic = mockStatic(ThreadLocalRandom.class)) {
            mockedStatic.when(ThreadLocalRandom::current).thenReturn(threadLocalRandom);

            final PaymentStatus result = paymentProcessService.processPayment(payment);

            assertEquals(PaymentStatus.PAYMENT_FAILED, result);
        }
    }
}
