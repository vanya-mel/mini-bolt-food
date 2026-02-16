package cz.dev.vanya.miniboltfood.payment.service.impl;

import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentStatus;
import cz.dev.vanya.miniboltfood.payment.mapper.PaymentMapper;
import cz.dev.vanya.miniboltfood.payment.payload.request.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.payment.payload.response.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.payment.repository.PaymentRepository;
import cz.dev.vanya.miniboltfood.payment.service.PaymentProcessService;
import cz.dev.vanya.miniboltfood.payment.service.PaymentService;
import cz.dev.vanya.miniboltfood.payment.utils.PaymentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentProcessService paymentProcessService;

    @Override
    @Transactional
    public CreatePaymentResponseDto createPayment(final CreatePaymentRequestDto createPaymentRequestDto) {
        final Optional<Payment> orderPayment = paymentRepository.findByOrderId(createPaymentRequestDto.orderId());
        if (orderPayment.isPresent()) {
            log.info("Payment already exists for orderId={}.", createPaymentRequestDto.orderId());
            return paymentMapper.mapToCreatePaymentResponseDto(orderPayment.get());
        }

        try {
            return saveAndProcessPayment(createPaymentRequestDto);
        } catch (DataIntegrityViolationException e) {
            // Handle concurrent requests (data race condition): unique constraint on order_id may be hit for the same order.
            return paymentRepository.findByOrderId(createPaymentRequestDto.orderId())
                    .map(paymentMapper::mapToCreatePaymentResponseDto)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            PaymentUtils.PAYMENT_CONCURRENTLY_CREATED_ERROR_MESSAGE
                    ));
        }
    }

    private CreatePaymentResponseDto saveAndProcessPayment(final CreatePaymentRequestDto createPaymentRequestDto) {
        final Payment payment = paymentMapper.mapToPayment(createPaymentRequestDto);
        payment.setPaymentStatus(PaymentStatus.PAYMENT_INITIATED);

        final Payment createdPayment = paymentRepository.save(payment);

        final PaymentStatus paymentStatus = paymentProcessService.processPayment(createdPayment);
        createdPayment.setPaymentStatus(paymentStatus);

        final Payment processedPayment = paymentRepository.save(createdPayment);

        return paymentMapper.mapToCreatePaymentResponseDto(processedPayment);
    }
}
