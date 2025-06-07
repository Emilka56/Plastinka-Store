package org.example.plastinka2.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.plastinka2.converters.DateConverter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isActive;
    private BigDecimal totalPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name="order_address",
            joinColumns=@JoinColumn(name="order_id"),
            inverseJoinColumns = @JoinColumn(name="address_id")
    )
    private List<Address> addresses;

    @OneToMany(mappedBy = "order")
    private List<SingleOrder> singleOrders;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Cart cart;

    @Transient
    private DateConverter dateConverter = new DateConverter();

    public String getFormattedCreatedAt() {
        return dateConverter.convert(createdAt);
    }

    public String getFormattedDeliveryTime() {
        return deliveryTime != null ? dateConverter.convert(deliveryTime) : "";
    }
}
