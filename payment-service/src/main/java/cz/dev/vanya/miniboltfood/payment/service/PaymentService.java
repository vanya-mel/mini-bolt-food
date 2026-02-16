package cz.dev.vanya.miniboltfood.payment.service;

import cz.dev.vanya.miniboltfood.payment.payload.request.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.payment.payload.response.CreatePaymentResponseDto;

/**
 * Provides application-level operations for payment processing.
 *
 * <p>
 * Responsible for creating and managing payments associated with orders.
 */
public interface PaymentService {

    /**
     * Creates a payment for the given order.
     *
     * <p>
     * If a payment for the order already exists, the existing payment is returned.
     *
     * @param createPaymentRequestDto request payload containing payment details
     * @return payment representation
     */
    CreatePaymentResponseDto createPayment(CreatePaymentRequestDto createPaymentRequestDto);
}
