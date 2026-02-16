package cz.dev.vanya.miniboltfood.payment.repository;

import cz.dev.vanya.miniboltfood.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);
}
