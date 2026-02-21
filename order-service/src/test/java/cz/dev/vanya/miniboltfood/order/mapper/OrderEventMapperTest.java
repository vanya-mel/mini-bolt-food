package cz.dev.vanya.miniboltfood.order.mapper;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.DeliveryAssignedEvent;
import cz.dev.vanya.miniboltfood.commonlibs.messaging.event.OrderPaidEvent;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderEventMapperTest {

    private final OrderEventMapper orderEventMapper = Mappers.getMapper(OrderEventMapper.class);

    @Test
    void toOrderPaidEvent_mapsOrderAndPaymentData() {
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PAID);
        final CreatePaymentResponseDto paymentResponseDto =
                OrderServiceObjectProvider.provideCreatePaymentResponseDto(OrderServiceConstantHolder.PAYMENT_STATUS_DTO);

        final OrderPaidEvent result = orderEventMapper.toOrderPaidEvent(order, paymentResponseDto);

        assertNotNull(result);
        assertEquals(OrderServiceConstantHolder.ORDER_ID, result.orderId());
        assertEquals(OrderServiceConstantHolder.PAYMENT_ID, result.paymentId());
        assertEquals(OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT, result.amount());
        assertEquals(OrderServiceConstantHolder.PAYMENT_METHOD_DTO, result.paymentMethod());
    }

    @Test
    void decorateOrder_updatesDeliveryFields() {
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PAID);
        final DeliveryAssignedEvent deliveryAssignedEvent = OrderServiceObjectProvider.provideDeliveryAssignedEvent();

        orderEventMapper.decorateOrder(order, deliveryAssignedEvent);

        assertEquals(OrderServiceConstantHolder.COURIER_NAME, order.getCourierName());
        assertEquals(OrderServiceConstantHolder.ETA_MINUTES, order.getEtaMinutes());
    }

    @Test
    void decorateOrder_whenEventFieldsAreNull_keepsExistingValues() {
        final Order order = OrderServiceObjectProvider.provideOrder(OrderStatus.PAID);
        assumeThat(order.getCourierName()).isNull();
        assumeThat(order.getEtaMinutes()).isNull();
        order.setCourierName(OrderServiceConstantHolder.COURIER_NAME);
        order.setEtaMinutes(OrderServiceConstantHolder.ETA_MINUTES);

        orderEventMapper.decorateOrder(order, new DeliveryAssignedEvent(OrderServiceConstantHolder.ORDER_ID, null, null));

        assertThat(order.getCourierName()).isEqualTo(OrderServiceConstantHolder.COURIER_NAME);
        assertThat(order.getEtaMinutes()).isEqualTo(OrderServiceConstantHolder.ETA_MINUTES);
    }
}
