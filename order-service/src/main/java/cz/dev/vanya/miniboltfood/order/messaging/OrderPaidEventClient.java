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

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidEventClient {

    @Value("${order-paid-topic:configure-me-topic}")
    private String orderPaidTopic;

    private final OrderEventMapper orderEventMapper;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

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
