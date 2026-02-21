package cz.dev.vanya.miniboltfood.order.messaging;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.mapper.OrderEventMapper;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPaidEventClientTest {

    @Mock
    private OrderEventMapper orderEventMapper;
    @Mock
    private KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;

    @InjectMocks
    private OrderPaidEventClient orderPaidEventClient;

    @Test
    void sendOrderPaidEvent_whenPaymentSucceeded_mapsAndSendsEvent() {
        final Order order = OrderServiceObjectProvider.provideOrder(OrderServiceConstantHolder.ORDER_STATUS_PAID);
        final CreatePaymentResponseDto paymentResponseDto =
                OrderServiceObjectProvider.provideCreatePaymentResponseDto(PaymentStatusDto.PAYMENT_SUCCEEDED);
        final OrderPaidEvent orderPaidEvent = new OrderPaidEvent(
                OrderServiceConstantHolder.ORDER_ID,
                OrderServiceConstantHolder.PAYMENT_ID,
                OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT,
                OrderServiceConstantHolder.PAYMENT_METHOD_DTO
        );
        ReflectionTestUtils.setField(orderPaidEventClient, "orderPaidTopic", OrderServiceConstantHolder.ORDER_PAID_TOPIC);

        when(orderEventMapper.toOrderPaidEvent(order, paymentResponseDto)).thenReturn(orderPaidEvent);
        when(kafkaTemplate.send(
                OrderServiceConstantHolder.ORDER_PAID_TOPIC,
                OrderServiceConstantHolder.ORDER_ID,
                orderPaidEvent
        )).thenReturn(CompletableFuture.completedFuture(null));

        orderPaidEventClient.sendOrderPaidEvent(order, paymentResponseDto);

        verify(orderEventMapper).toOrderPaidEvent(order, paymentResponseDto);
        verify(kafkaTemplate).send(
                OrderServiceConstantHolder.ORDER_PAID_TOPIC,
                OrderServiceConstantHolder.ORDER_ID,
                orderPaidEvent
        );
    }

    @Test
    void sendOrderPaidEvent_whenPaymentNotSucceeded_skipsPublish() {
        final Order order = OrderServiceObjectProvider.provideOrder(OrderServiceConstantHolder.ORDER_STATUS_PAYMENT_FAILED);
        final CreatePaymentResponseDto paymentResponseDto =
                OrderServiceObjectProvider.provideCreatePaymentResponseDto(PaymentStatusDto.PAYMENT_FAILED);

        orderPaidEventClient.sendOrderPaidEvent(order, paymentResponseDto);

        verify(orderEventMapper, never()).toOrderPaidEvent(order, paymentResponseDto);
        verify(kafkaTemplate, never()).send(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any());
    }
}
