package cz.dev.vanya.miniboltfood.order.messaging;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.mapper.OrderEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes {@link OrderPaidEvent} to Kafka.
 *
 * <p>
 * The event is published only when a payment has been successfully completed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidEventClient {

    @Value("${order-paid-topic:configure-me-topic}")
    private String orderPaidTopic;

    private final OrderEventMapper orderEventMapper;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

    /**
     * Sends an {@link OrderPaidEvent} for the given order if the payment status is successful.
     *
     * <p>
     * Uses {@code orderId} as the Kafka message key to keep all events for the same order in the same partition.
     *
     * @param order order entity
     * @param createPaymentResponseDto payment response returned by payment-service
     */
    public void sendOrderPaidEvent(final Order order, final CreatePaymentResponseDto createPaymentResponseDto) {
        if (createPaymentResponseDto.paymentStatus() != PaymentStatusDto.PAYMENT_SUCCEEDED) {
            log.warn("Skipping OrderPaidEvent publish. Payment status={}.", createPaymentResponseDto.paymentStatus());
            return;
        }
        final OrderPaidEvent orderPaidEvent = orderEventMapper.toOrderPaidEvent(order, createPaymentResponseDto);

        kafkaTemplate.send(orderPaidTopic, order.getId(), orderPaidEvent)
                .thenAccept(result ->
                        log.info("Order paid event [id={}] sent to topic {}.", order.getId(), orderPaidTopic)
                );
    }
}
