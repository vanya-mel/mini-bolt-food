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

/**
 * Maps between order/payment API DTOs and order domain objects.
 *
 * <p>
 * Used by order-service to create payment requests and interpret payment results.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PaymentMapper {

    /**
     * Builds a payment creation request for the given order and payment method.
     *
     * @param payOrderRequestDto request payload containing payment method
     * @param order order entity
     * @return payment creation request
     */
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "amount", source = "order.totalAmount")
    @Mapping(target = "paymentMethod", source = "payOrderRequestDto.paymentMethod")
    CreatePaymentRequestDto mapCreatePaymentRequestDto(PayOrderRequestDto payOrderRequestDto, Order order);

    /**
     * Maps payment status returned by payment-service to order status used by order-service.
     *
     * @param paymentStatusDto payment status from payment-service
     * @return corresponding order status
     */
    @ValueMapping(target = "PAID", source = "PAYMENT_SUCCEEDED")
    @ValueMapping(target = "PAYMENT_FAILED", source = "PAYMENT_FAILED")
    @ValueMapping(target = "PAYMENT_FAILED", source = "REFUNDED")
    @ValueMapping(target = MappingConstants.THROW_EXCEPTION, source = MappingConstants.ANY_REMAINING)
    OrderStatus mapOrderStatus(PaymentStatusDto paymentStatusDto);
}
