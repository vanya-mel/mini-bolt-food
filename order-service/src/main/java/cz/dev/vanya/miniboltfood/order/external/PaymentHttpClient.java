package cz.dev.vanya.miniboltfood.order.external;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/api/v1/payments"
)
public interface PaymentHttpClient {

    @PostExchange
    CreatePaymentResponseDto createPayment(@RequestBody CreatePaymentRequestDto request);
}
