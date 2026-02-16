package cz.dev.vanya.miniboltfood.payment.service;

import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentStatus;

/**
 * Handles payment processing logic.
 *
 * <p>
 * In a real-world system, this service would typically integrate with
 * an external payment gateway or provider.
 */
public interface PaymentProcessService {

    /**
     * Processes the payment and determines its final status.
     *
     * @param payment payment to be processed
     * @return resulting payment status
     */
    PaymentStatus processPayment(Payment payment);
}
