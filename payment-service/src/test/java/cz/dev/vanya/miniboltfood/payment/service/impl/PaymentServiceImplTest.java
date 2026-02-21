package cz.dev.vanya.miniboltfood.payment.service.impl;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentStatus;
import cz.dev.vanya.miniboltfood.payment.mapper.PaymentMapper;
import cz.dev.vanya.miniboltfood.payment.repository.PaymentRepository;
import cz.dev.vanya.miniboltfood.payment.service.PaymentProcessService;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentServiceConstantHolder;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentServiceObjectProvider;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentProcessService paymentProcessService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void createPayment_whenPaymentAlreadyExists_returnsExistingMappedResponse() {
        // given
        final CreatePaymentRequestDto request = PaymentServiceObjectProvider.provideCreatePaymentRequestDto();
        final Payment existingPayment = PaymentServiceObjectProvider.providePayment();
        final CreatePaymentResponseDto response = PaymentServiceObjectProvider.provideCreatePaymentResponseDto();

        when(paymentRepository.findByOrderId(PaymentServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(existingPayment));
        when(paymentMapper.mapToCreatePaymentResponseDto(existingPayment)).thenReturn(response);

        // when
        final CreatePaymentResponseDto result = paymentService.createPayment(request);

        // then
        assertEquals(response, result);

        verify(paymentRepository).findByOrderId(PaymentServiceConstantHolder.ORDER_ID);
        verify(paymentMapper).mapToCreatePaymentResponseDto(existingPayment);
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentMapper, never()).mapToPayment(request);
        verify(paymentProcessService, never()).processPayment(any(Payment.class));
    }

    @Test
    void createPayment_whenPaymentDoesNotExist_savesProcessesAndReturnsMappedResponse() {
        // given
        final CreatePaymentRequestDto request = PaymentServiceObjectProvider.provideCreatePaymentRequestDto();
        final Payment payment = PaymentServiceObjectProvider.providePaymentWithoutId();
        final CreatePaymentResponseDto response = PaymentServiceObjectProvider.provideCreatePaymentResponseDto();

        when(paymentRepository.findByOrderId(PaymentServiceConstantHolder.ORDER_ID)).thenReturn(Optional.empty());
        when(paymentMapper.mapToPayment(request)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentProcessService.processPayment(any(Payment.class))).thenAnswer(invocation -> {
            final Payment paymentToProcess = invocation.getArgument(0, Payment.class);
            assumeThat(paymentToProcess.getPaymentStatus()).isEqualTo(PaymentStatus.PAYMENT_INITIATED);
            return PaymentStatus.PAYMENT_SUCCEEDED;
        });
        when(paymentMapper.mapToCreatePaymentResponseDto(payment)).thenReturn(response);

        // when
        final CreatePaymentResponseDto result = paymentService.createPayment(request);

        // then
        assertEquals(response, result);

        verify(paymentRepository).findByOrderId(PaymentServiceConstantHolder.ORDER_ID);
        verify(paymentMapper).mapToPayment(request);
        verify(paymentRepository, times(2)).save(payment);
        verify(paymentProcessService).processPayment(payment);
        verify(paymentMapper).mapToCreatePaymentResponseDto(payment);
    }

    @Test
    void createPayment_whenConcurrentInsertHappens_returnsLoadedPayment() {
        // given
        final CreatePaymentRequestDto request = PaymentServiceObjectProvider.provideCreatePaymentRequestDto();
        final Payment payment = PaymentServiceObjectProvider.providePaymentWithoutId();
        final Payment concurrentlyCreatedPayment = PaymentServiceObjectProvider.providePayment();
        final CreatePaymentResponseDto response = PaymentServiceObjectProvider.provideCreatePaymentResponseDto();

        when(paymentRepository.findByOrderId(PaymentServiceConstantHolder.ORDER_ID))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(concurrentlyCreatedPayment));
        when(paymentMapper.mapToPayment(request)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key value violates unique constraint."));
        when(paymentMapper.mapToCreatePaymentResponseDto(concurrentlyCreatedPayment)).thenReturn(response);

        // when
        final CreatePaymentResponseDto result = paymentService.createPayment(request);

        // then
        assertEquals(response, result);

        verify(paymentMapper).mapToCreatePaymentResponseDto(concurrentlyCreatedPayment);
        verify(paymentProcessService, never()).processPayment(any(Payment.class));
    }

    @Test
    void createPayment_whenConcurrentInsertHappensAndLookupFails_throwsInternalServerError() {
        // given
        final CreatePaymentRequestDto request = PaymentServiceObjectProvider.provideCreatePaymentRequestDto();
        final Payment payment = PaymentServiceObjectProvider.providePaymentWithoutId();

        when(paymentRepository.findByOrderId(PaymentServiceConstantHolder.ORDER_ID))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty());
        when(paymentMapper.mapToPayment(request)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key value violates unique constraint."));

        // when, then
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> paymentService.createPayment(request))
                .satisfies(throwable -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getStatusCode());
                    assertEquals(PaymentUtils.PAYMENT_CONCURRENTLY_CREATED_ERROR_MESSAGE, throwable.getReason());
                });
    }
}
