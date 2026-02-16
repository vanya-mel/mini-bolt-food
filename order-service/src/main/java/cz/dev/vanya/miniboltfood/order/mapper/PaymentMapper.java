package cz.dev.vanya.miniboltfood.order.mapper;

import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.enums.PaymentStatusDto;
import cz.dev.vanya.miniboltfood.order.domain.Order;
import cz.dev.vanya.miniboltfood.order.domain.OrderStatus;
import cz.dev.vanya.miniboltfood.order.payload.request.PayOrderRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ValueMapping;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PaymentMapper {

    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "amount", source = "order.totalAmount")
    @Mapping(target = "paymentMethod", source = "payOrderRequestDto.paymentMethod")
    CreatePaymentRequestDto mapCreatePaymentRequestDto(PayOrderRequestDto payOrderRequestDto, Order order, Long orderId);


    @ValueMapping(target = "PAID", source = "PAYMENT_SUCCEEDED")
    @ValueMapping(target = "PAYMENT_FAILED", source = "PAYMENT_FAILED")
    @ValueMapping(target = "PAYMENT_FAILED", source = "REFUNDED")
    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = MappingConstants.ANY_REMAINING)
    OrderStatus mapOrderStatus(PaymentStatusDto paymentStatusDto);
}
