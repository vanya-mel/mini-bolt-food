package cz.dev.vanya.miniboltfood.delivery.service.impl;

import cz.dev.vanya.miniboltfood.delivery.domain.Delivery;
import cz.dev.vanya.miniboltfood.delivery.repository.DeliveryRepository;
import cz.dev.vanya.miniboltfood.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private static final Faker FAKER = new Faker();

    private final DeliveryRepository deliveryRepository;

    @Override
    public Delivery assignDelivery(final Long orderId) {
        final Delivery delivery = new Delivery();

        delivery.setOrderId(orderId);
        delivery.setCourierName(FAKER.name().fullName());
        delivery.setEtaMinutes(ThreadLocalRandom.current().nextInt(10, 60));

        return deliveryRepository.save(delivery);
    }

    @Override
    public Optional<Delivery> findByOrderId(final Long orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }
}
