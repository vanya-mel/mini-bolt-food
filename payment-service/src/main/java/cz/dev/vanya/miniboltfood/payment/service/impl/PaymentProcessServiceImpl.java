package cz.dev.vanya.miniboltfood.payment.service.impl;

import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentMethod;
import cz.dev.vanya.miniboltfood.payment.domain.PaymentStatus;
import cz.dev.vanya.miniboltfood.payment.service.PaymentProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PaymentProcessServiceImpl implements PaymentProcessService {

    // A random simulation of successfully payment probability.
    private static final Map<PaymentMethod, Integer> PAYMENT_PROBABILITY_MAP = Map.of(
            PaymentMethod.CARD, 98,
            PaymentMethod.QR, 80,
            PaymentMethod.BANK_TRANSFER, 45
    );


    @Override
    public PaymentStatus processPayment(final Payment payment) {
        final int successfullyPaymentProbability = ThreadLocalRandom.current().nextInt(0, 100);

        return successfullyPaymentProbability < PAYMENT_PROBABILITY_MAP.getOrDefault(payment.getPaymentMethod(), 0)
                ? PaymentStatus.PAYMENT_SUCCEEDED
                : PaymentStatus.PAYMENT_FAILED;
    }
}
