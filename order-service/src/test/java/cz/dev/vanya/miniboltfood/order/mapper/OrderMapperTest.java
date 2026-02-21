package cz.dev.vanya.miniboltfood.order.mapper;

import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.payload.dto.OrderDto;
import cz.dev.vanya.miniboltfood.order.payload.dto.enums.OrderStatusDto;
import cz.dev.vanya.miniboltfood.order.payload.request.CreateOrderRequestDto;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceConstantHolder;
import cz.dev.vanya.miniboltfood.order.utils.OrderServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void mapToOrder_mapsRequestAndLinksOrderItems() {
        final CreateOrderRequestDto request = OrderServiceObjectProvider.provideCreateOrderRequestDto();

        final Order result = orderMapper.mapToOrder(request);

        assertNotNull(result);
        assertEquals(OrderServiceConstantHolder.CUSTOMER_ID, result.getCustomerId());
        assertEquals(OrderServiceConstantHolder.DESTINATION_ADDRESS, result.getDestinationAddress());
        assertThat(result.getOrderItems()).hasSize(2);
        assertThat(result.getOrderItems()).allSatisfy(orderItem -> assertThat(orderItem.getOrder()).isSameAs(result));
    }

    @Test
    void mapToOrderDto_mapsDomainToDto() {
        final Order order = OrderServiceObjectProvider.provideOrder(OrderServiceConstantHolder.ORDER_STATUS_PENDING_PAYMENT);
        order.setCourierName(OrderServiceConstantHolder.COURIER_NAME);
        order.setEtaMinutes(OrderServiceConstantHolder.ETA_MINUTES);

        final OrderDto result = orderMapper.mapToOrderDto(order);

        assertNotNull(result);
        assertEquals(OrderServiceConstantHolder.ORDER_ID, result.id());
        assertEquals(OrderServiceConstantHolder.CUSTOMER_ID, result.customerId());
        assertEquals(OrderServiceConstantHolder.DESTINATION_ADDRESS, result.destinationAddress());
        assertEquals(OrderServiceConstantHolder.ORDER_TOTAL_AMOUNT, result.totalAmount());
        assertEquals(OrderStatusDto.PENDING_PAYMENT, result.orderStatus());
        assertThat(result.orderItems()).hasSize(2);
    }
}
