package com.example.ecommerceorderservicespring.entity;

import com.example.ecommerceorderservicespring.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    private Long userId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //sets the data inthe table with whom it has a relationship with. in our case -> OrderITem table
    private List<OrderItem> items;
}
