package com.example.ecommerceorderservicespring.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orderItems")
public class OrderItem extends BaseEntity {
    private long productId;
    private int quantity;
    private double pricePerUnit;
    private double totalPrice;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="order_id")
    private Order order;
}
