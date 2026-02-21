package cz.dev.vanya.miniboltfood.order.integration;

import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.repository.OrderRepository;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {OrderServiceConstantHolder.ORDER_PAID_TOPIC, OrderServiceConstantHolder.DELIVERY_ASSIGNED_TOPIC}
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class OrderServiceKafkaIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void whenDeliveryAssignedEventReceivedForPaidOrder_thenOrderBecomesPendingDeliveryAndIsDecorated() throws Exception {
        final Order paidOrder = orderRepository.save(OrderServiceObjectProvider.provideOrderWithoutId(OrderStatus.PAID));
        final DeliveryAssignedEvent deliveryAssignedEvent = OrderServiceObjectProvider.provideDeliveryAssignedEvent(
                paidOrder.getId(),
                OrderServiceConstantHolder.UPDATED_COURIER_NAME,
                OrderServiceConstantHolder.UPDATED_ETA_MINUTES
        );

        kafkaTemplate.send(OrderServiceConstantHolder.DELIVERY_ASSIGNED_TOPIC, paidOrder.getId(), deliveryAssignedEvent).get();

        final Optional<Order> processedOrder = waitForOrder(
                paidOrder.getId(),
                Duration.ofSeconds(10),
                order -> order.getOrderStatus() == OrderStatus.PENDING_DELIVERY
                        && OrderServiceConstantHolder.UPDATED_COURIER_NAME.equals(order.getCourierName())
                        && OrderServiceConstantHolder.UPDATED_ETA_MINUTES.equals(order.getEtaMinutes())
        );

        assertThat(processedOrder).isPresent();
    }

    @Test
    void whenDeliveryAssignedEventReceivedForNonPaidOrder_thenOrderStateDoesNotChange() throws Exception {
        final Order pendingPaymentOrder = orderRepository.save(OrderServiceObjectProvider.provideOrderWithoutId(OrderStatus.PENDING_PAYMENT));
        final DeliveryAssignedEvent deliveryAssignedEvent = OrderServiceObjectProvider.provideDeliveryAssignedEvent(
                pendingPaymentOrder.getId(),
                OrderServiceConstantHolder.UPDATED_COURIER_NAME,
                OrderServiceConstantHolder.UPDATED_ETA_MINUTES
        );

        kafkaTemplate.send(OrderServiceConstantHolder.DELIVERY_ASSIGNED_TOPIC, pendingPaymentOrder.getId(), deliveryAssignedEvent).get();

        final Optional<Order> unchangedOrder = waitForOrder(
                pendingPaymentOrder.getId(),
                Duration.ofSeconds(5),
                order -> order.getOrderStatus() == OrderStatus.PENDING_PAYMENT
                        && order.getCourierName() == null
                        && order.getEtaMinutes() == null
        );

        assertThat(unchangedOrder).isPresent();
    }

    private Optional<Order> waitForOrder(final Long orderId,
                                         final Duration timeout,
                                         final Predicate<Order> condition) throws InterruptedException {
        final long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            final Optional<Order> orderOptional = orderRepository.findWithOrderItemsById(orderId);
            if (orderOptional.isPresent() && condition.test(orderOptional.get())) {
                return orderOptional;
            }
            Thread.sleep(100);
        }
        return Optional.empty();
    }
}
