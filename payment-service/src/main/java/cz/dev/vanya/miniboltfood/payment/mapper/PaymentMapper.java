package cz.dev.vanya.miniboltfood.payment.mapper;

import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentRequestDto;
import cz.dev.vanya.miniboltfood.commonlibs.api.payment.CreatePaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between payment API DTOs and the payment domain model.
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface PaymentMapper {

    /**
     * Maps a create payment request DTO to a {@link Payment} entity.
     *
     * @param request payment creation request
     * @return payment entity
     */
    Payment mapToPayment(CreatePaymentRequestDto request);


    /**
     * Maps a {@link Payment} entity to create payment response DTO.
     *
     * @param payment payment entity
     * @return response DTO
     */
    CreatePaymentResponseDto mapToCreatePaymentResponseDto(Payment payment);
}
