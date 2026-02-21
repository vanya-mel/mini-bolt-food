package cz.dev.vanya.miniboltfood.delivery.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import cz.dev.vanya.miniboltfood.delivery.repository.DeliveryRepository;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceConstantHolder;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceObjectProvider;
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
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@EmbeddedKafka(
        partitions = 1,
        topics = {"orders.events.it.v1", "delivery.events.it.v1"}
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DeliveryServiceKafkaIntegrationTest {

    private static final String ORDER_PAID_TOPIC = "orders.events.it.v1";
    private static final String DELIVERY_ASSIGNED_TOPIC = "delivery.events.it.v1";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Long, byte[]> deliveryAssignedEventConsumer;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAll();
        deliveryAssignedEventConsumer = createDeliveryAssignedEventConsumer();
    }

    @AfterEach
    void tearDown() {
        if (deliveryAssignedEventConsumer != null) {
            deliveryAssignedEventConsumer.close();
        }
    }

    @Test
    void whenOrderPaidEventReceived_thenDeliveryIsPersistedAndDeliveryAssignedEventPublished() throws Exception {
        final Long orderId = DeliveryServiceConstantHolder.ORDER_ID;
        final OrderPaidEvent orderPaidEvent = DeliveryServiceObjectProvider.provideOrderPaidEvent();

        kafkaTemplate.send(ORDER_PAID_TOPIC, orderId, orderPaidEvent).get();

        final List<DeliveryAssignedEvent> deliveryAssignedEvents =
                pollDeliveryAssignedEventsForOrder(orderId, 1, Duration.ofSeconds(10));
        final Optional<Delivery> savedDelivery = pollUntilDeliveryPersisted(orderId, Duration.ofSeconds(10));

        assertThat(deliveryAssignedEvents).hasSize(1);
        final DeliveryAssignedEvent deliveryAssignedEvent = deliveryAssignedEvents.getFirst();
        assertThat(savedDelivery).isPresent();
        assertEquals(orderId, savedDelivery.get().getOrderId());
        assertThat(savedDelivery.get().getCourierName()).isNotBlank();
        assertThat(savedDelivery.get().getEtaMinutes()).isBetween(10, 59);

        assertEquals(orderId, deliveryAssignedEvent.orderId());
        assertEquals(savedDelivery.get().getCourierName(), deliveryAssignedEvent.courierName());
        assertEquals(savedDelivery.get().getEtaMinutes(), deliveryAssignedEvent.etaMinutes());
    }

    @Test
    void whenSameOrderPaidEventProcessedTwice_thenOnlyOneDeliveryIsStoredAndOneEventIsPublished() throws Exception {
        final Long orderId = DeliveryServiceConstantHolder.NEW_ORDER_ID;
        final OrderPaidEvent firstEvent = DeliveryServiceObjectProvider.provideOrderPaidEvent(orderId);
        final OrderPaidEvent secondEvent = DeliveryServiceObjectProvider.provideOrderPaidEvent(orderId);

        kafkaTemplate.send(ORDER_PAID_TOPIC, orderId, firstEvent).get();
        kafkaTemplate.send(ORDER_PAID_TOPIC, orderId, secondEvent).get();

        final List<DeliveryAssignedEvent> deliveryAssignedEvents =
                pollDeliveryAssignedEventsForOrder(orderId, 1, Duration.ofSeconds(10));
        final List<DeliveryAssignedEvent> additionalEvents =
                pollDeliveryAssignedEventsForOrder(orderId, 1, Duration.ofSeconds(3));
        final Optional<Delivery> savedDelivery = pollUntilDeliveryPersisted(orderId, Duration.ofSeconds(10));

        assertThat(deliveryAssignedEvents).hasSize(1);
        assertThat(savedDelivery).isPresent();
        assertEquals(1, deliveryRepository.count());

        assertThat(additionalEvents).isEmpty();
    }

    private Consumer<Long, byte[]> createDeliveryAssignedEventConsumer() {
        final Map<String, Object> props = KafkaTestUtils.consumerProps(
                embeddedKafkaBroker,
                "delivery-it-group-" + UUID.randomUUID(),
                false
        );
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final Consumer<Long, byte[]> consumer = new DefaultKafkaConsumerFactory<>(
                props,
                new LongDeserializer(),
                new ByteArrayDeserializer()
        ).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, DELIVERY_ASSIGNED_TOPIC);
        return consumer;
    }

    /**
     * Polls repository until delivery for given order appears or timeout is reached.
     */
    private Optional<Delivery> pollUntilDeliveryPersisted(final Long orderId, final Duration timeout) throws InterruptedException {
        final long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            final Optional<Delivery> delivery = deliveryRepository.findByOrderId(orderId);
            if (delivery.isPresent()) {
                return delivery;
            }
            Thread.sleep(100);
        }
        return Optional.empty();
    }

    /**
     * Polls Kafka and returns events for a specific order id.
     * Stops early once expectedCount is collected or timeout is reached.
     */
    private List<DeliveryAssignedEvent> pollDeliveryAssignedEventsForOrder(final Long orderId,
                                                                           final int expectedCount,
                                                                           final Duration timeout) {
        final long deadline = System.nanoTime() + timeout.toNanos();
        final List<DeliveryAssignedEvent> matchedEvents = new ArrayList<>();

        while (System.nanoTime() < deadline && matchedEvents.size() < expectedCount) {
            final ConsumerRecords<Long, byte[]> records =
                    deliveryAssignedEventConsumer.poll(Duration.ofMillis(200));
            records.forEach(consumerRecord -> {
                final DeliveryAssignedEvent event = deserializeDeliveryAssignedEvent(consumerRecord.value());
                if (event != null && orderId.equals(event.orderId())) {
                    matchedEvents.add(event);
                }
            });
        }

        return matchedEvents;
    }

    private DeliveryAssignedEvent deserializeDeliveryAssignedEvent(final byte[] value) {
        if (value == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(value, DeliveryAssignedEvent.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize DeliveryAssignedEvent from Kafka payload.", ex);
        }
    }
}
