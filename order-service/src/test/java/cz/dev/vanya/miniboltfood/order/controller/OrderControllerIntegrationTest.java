package cz.dev.vanya.miniboltfood.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.order.config.PaymentClientTestConfig;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.external.PaymentHttpClient;
import cz.dev.vanya.miniboltfood.order.handler.GlobalOrderServiceExceptionHandler;
import cz.dev.vanya.miniboltfood.order.payload.dto.OrderDto;
import cz.dev.vanya.miniboltfood.order.payload.dto.enums.OrderStatusDto;
import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.repository.OrderRepository;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceEndpointUtils;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Import(PaymentClientTestConfig.class)
@EmbeddedKafka(
        partitions = 1,
        topics = {OrderServiceConstantHolder.ORDER_PAID_TOPIC, OrderServiceConstantHolder.DELIVERY_ASSIGNED_TOPIC}
)
class OrderControllerIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MockMvc mockMvc;
    private Consumer<Long, byte[]> orderPaidEventConsumer;

    @Autowired
    private OrderController orderController;

    @Autowired
    private GlobalOrderServiceExceptionHandler globalOrderServiceExceptionHandler;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private PaymentHttpClient paymentHttpClient;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(globalOrderServiceExceptionHandler)
                .build();
        orderRepository.deleteAll();
        reset(paymentHttpClient);
        orderPaidEventConsumer = createOrderPaidEventConsumer();
    }

    @AfterEach
    void tearDown() {
        if (orderPaidEventConsumer != null) {
            orderPaidEventConsumer.close();
        }
    }

    @Test
    void createOrder_thenGetById_returnsPersistedOrder() throws Exception {
        final CreateOrderRequestDto createOrderRequestDto = OrderServiceObjectProvider.provideCreateOrderRequestDto();

        final MvcResult createResult = mockMvc.perform(post(OrderServiceEndpointUtils.CREATE_ORDER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(createOrderRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.customerId").value(OrderServiceConstantHolder.CUSTOMER_ID))
                .andExpect(jsonPath("$.destinationAddress").value(OrderServiceConstantHolder.DESTINATION_ADDRESS))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatusDto.PENDING_PAYMENT.name()))
                .andExpect(jsonPath("$.orderItems.length()").value(2))
                .andReturn();

        final OrderDto createdOrder =
                OBJECT_MAPPER.readValue(createResult.getResponse().getContentAsByteArray(), OrderDto.class);
        assertThat(createdOrder.id()).isNotNull();
        assertThat(orderRepository.findWithOrderItemsById(createdOrder.id())).isPresent();

        mockMvc.perform(get(OrderServiceEndpointUtils.GET_ORDER_ENDPOINT, createdOrder.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdOrder.id()))
                .andExpect(jsonPath("$.customerId").value(OrderServiceConstantHolder.CUSTOMER_ID))
                .andExpect(jsonPath("$.destinationAddress").value(OrderServiceConstantHolder.DESTINATION_ADDRESS))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatusDto.PENDING_PAYMENT.name()))
                .andExpect(jsonPath("$.orderItems.length()").value(2));
    }

    @Test
    void payOrder_callsPaymentServiceAndPublishesOrderPaidEvent() throws Exception {
        final Order order = orderRepository.save(OrderServiceObjectProvider.provideOrderWithoutId(OrderStatus.PENDING_PAYMENT));
        final PayOrderRequestDto payOrderRequestDto = OrderServiceObjectProvider.providePayOrderRequestDto();

        final CreatePaymentResponseDto paymentResponseDto =
                OrderServiceObjectProvider.provideCreatePaymentResponseDto(PaymentStatusDto.PAYMENT_SUCCEEDED, order.getId());
        when(paymentHttpClient.createPayment(any())).thenReturn(paymentResponseDto);

        mockMvc.perform(post(OrderServiceEndpointUtils.PAY_ORDER_ENDPOINT, order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(payOrderRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatusDto.PAID.name()));

        final List<OrderPaidEvent> orderPaidEvents = pollOrderPaidEventsForOrder(order.getId(), 1, Duration.ofSeconds(10));
        assertThat(orderPaidEvents).hasSize(1);
        assertEquals(order.getId(), orderPaidEvents.getFirst().orderId());

        final Order paidOrder = orderRepository.findWithOrderItemsById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.PAID, paidOrder.getOrderStatus());
    }

    @Test
    void closeOrder_whenPendingDelivery_thenMarksOrderAsDelivered() throws Exception {
        final Order order = orderRepository.save(OrderServiceObjectProvider.provideOrderWithoutId(OrderStatus.PENDING_DELIVERY));

        mockMvc.perform(post(OrderServiceEndpointUtils.DELIVERY_ORDER_ENDPOINT, order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.orderStatus").value(OrderStatusDto.DELIVERED.name()));

        final Order deliveredOrder = orderRepository.findWithOrderItemsById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.DELIVERED, deliveredOrder.getOrderStatus());
    }

    private Consumer<Long, byte[]> createOrderPaidEventConsumer() {
        final Map<String, Object> props = KafkaTestUtils.consumerProps(
                embeddedKafkaBroker,
                "order-controller-it-group-" + UUID.randomUUID(),
                false
        );
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final Consumer<Long, byte[]> consumer = new DefaultKafkaConsumerFactory<>(
                props,
                new LongDeserializer(),
                new ByteArrayDeserializer()
        ).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, OrderServiceConstantHolder.ORDER_PAID_TOPIC);
        return consumer;
    }

    private List<OrderPaidEvent> pollOrderPaidEventsForOrder(final Long orderId,
                                                             final int expectedCount,
                                                             final Duration timeout) {
        final long deadline = System.nanoTime() + timeout.toNanos();
        final List<OrderPaidEvent> matchedEvents = new ArrayList<>();

        while (System.nanoTime() < deadline && matchedEvents.size() < expectedCount) {
            final ConsumerRecords<Long, byte[]> records = orderPaidEventConsumer.poll(Duration.ofMillis(200));
            records.forEach(consumerRecord -> {
                final OrderPaidEvent event = deserializeOrderPaidEvent(consumerRecord.value());
                if (event != null && orderId.equals(event.orderId())) {
                    matchedEvents.add(event);
                }
            });
        }
        return matchedEvents;
    }

    private OrderPaidEvent deserializeOrderPaidEvent(final byte[] value) {
        if (value == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(value, OrderPaidEvent.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize OrderPaidEvent from Kafka payload.", ex);
        }
    }
}
