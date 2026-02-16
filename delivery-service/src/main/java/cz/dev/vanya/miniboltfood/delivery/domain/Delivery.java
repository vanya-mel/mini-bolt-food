package cz.dev.vanya.miniboltfood.delivery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deliveries")
public class Delivery {

    @Id
    @SequenceGenerator(
            name = "deliveries_seq",
            sequenceName = "deliveries_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deliveries_seq")
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "courier_name", nullable = false)
    private String courierName;

    @Column(name = "eta_minutes", nullable = false)
    private Integer etaMinutes;
}
