package cz.dev.vanya.miniboltfood.order.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * HTTP client configuration for communication with {@code payment-service}.
 *
 * <p>
 * Creates a typed {@link PaymentHttpClient} using Spring's declarative HTTP interface support
 * backed by {@link RestClient}.
 */
@Configuration
public class PaymentHttpClientConfig {

    /**
     * Base URL of {@code payment-service}.
     *
     * <p>
     * Example for local development: {@code http://localhost:8081}
     */
    @Value("${payment-service.base-url:https://configure-me}")
    private String paymentServiceBaseUrl;

    /**
     * Provides a reusable {@link RestClient.Builder}.
     *
     * @return RestClient builder
     */
    @Bean
    RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Builds a {@link RestClient} configured with {@code payment-service} base URL.
     *
     * @param builder RestClient builder
     * @return RestClient instance
     */
    @Bean
    RestClient paymentRestClient(final RestClient.Builder builder) {
        return builder
                .baseUrl(paymentServiceBaseUrl)
                .build();
    }

    /**
     * Creates a typed HTTP client proxy for {@link PaymentHttpClient}.
     *
     * @param paymentRestClient underlying RestClient
     * @return PaymentHttpClient proxy
     */
    @Bean
    PaymentHttpClient paymentHttpClient(final RestClient paymentRestClient) {
        return HttpServiceProxyFactory.builder()
                .exchangeAdapter(RestClientAdapter.create(paymentRestClient))
                .build()
                .createClient(PaymentHttpClient.class);
    }
}
