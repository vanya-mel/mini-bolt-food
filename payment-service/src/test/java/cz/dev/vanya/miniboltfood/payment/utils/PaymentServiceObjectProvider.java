package cz.dev.vanya.miniboltfood.payment.utils;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentServiceObjectProvider {

    public CreatePaymentRequestDto provideCreatePaymentRequestDto() {
        return new CreatePaymentRequestDto(
                PaymentServiceConstantHolder.ORDER_ID,
                PaymentServiceConstantHolder.AMOUNT,
                PaymentServiceConstantHolder.PAYMENT_METHOD_DTO
        );
    }

    public Payment providePayment() {
        return new Payment(
                PaymentServiceConstantHolder.PAYMENT_ID,
                PaymentServiceConstantHolder.ORDER_ID,
                PaymentServiceConstantHolder.AMOUNT,
                PaymentServiceConstantHolder.PAYMENT_STATUS,
                PaymentServiceConstantHolder.PAYMENT_METHOD
        );
    }

    public Payment providePaymentWithoutId() {
        return new Payment(
                null,
                PaymentServiceConstantHolder.ORDER_ID,
                PaymentServiceConstantHolder.AMOUNT,
                null,
                PaymentServiceConstantHolder.PAYMENT_METHOD
        );
    }

    public CreatePaymentResponseDto provideCreatePaymentResponseDto() {
        return new CreatePaymentResponseDto(
                PaymentServiceConstantHolder.PAYMENT_ID,
                PaymentServiceConstantHolder.ORDER_ID,
                PaymentServiceConstantHolder.AMOUNT,
                PaymentServiceConstantHolder.PAYMENT_METHOD_DTO,
                PaymentServiceConstantHolder.PAYMENT_STATUS_DTO
        );
    }
}
