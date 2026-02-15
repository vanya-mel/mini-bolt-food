package cz.dev.vanya.miniboltfood.order.repository;

import cz.dev.vanya.miniboltfood.order.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findWithOrderItemsById(Long id);
}
