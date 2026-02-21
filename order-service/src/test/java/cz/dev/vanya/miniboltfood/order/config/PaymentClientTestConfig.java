package cz.dev.vanya.miniboltfood.order.config;

import cz.dev.vanya.miniboltfood.order.external.PaymentHttpClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class PaymentClientTestConfig {

    @Bean
    @Primary
    PaymentHttpClient paymentHttpClientMock() {
        return mock(PaymentHttpClient.class);
    }
}
