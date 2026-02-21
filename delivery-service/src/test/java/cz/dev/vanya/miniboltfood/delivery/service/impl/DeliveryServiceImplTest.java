package cz.dev.vanya.miniboltfood.delivery.service.impl;

import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import cz.dev.vanya.miniboltfood.delivery.repository.DeliveryRepository;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceConstantHolder;
import cz.dev.vanya.miniboltfood.delivery.utils.DeliveryServiceObjectProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    @Test
    void assignDelivery_populatesFields_andSaves() {
        when(deliveryRepository.save(any(Delivery.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Delivery.class));

        final Delivery result = deliveryService.assignDelivery(DeliveryServiceConstantHolder.ORDER_ID);

        final ArgumentCaptor<Delivery> captor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(captor.capture());
        final Delivery savedDelivery = captor.getValue();

        assertThat(savedDelivery.getCourierName()).isNotBlank();
        assertThat(savedDelivery.getEtaMinutes()).isBetween(10, 59);
        assertThat(savedDelivery.getOrderId()).isEqualTo(DeliveryServiceConstantHolder.ORDER_ID);

        assertThat(result).isSameAs(savedDelivery);
    }

    @Test
    void findByOrderId_delegatesToRepository() {
        final Delivery delivery = DeliveryServiceObjectProvider.provideSavedDelivery();
        when(deliveryRepository.findByOrderId(DeliveryServiceConstantHolder.ORDER_ID)).thenReturn(Optional.of(delivery));

        final Optional<Delivery> result = deliveryService.findByOrderId(DeliveryServiceConstantHolder.ORDER_ID);

        assertThat(result).containsSame(delivery);

        verify(deliveryRepository).findByOrderId(DeliveryServiceConstantHolder.ORDER_ID);
    }
}
