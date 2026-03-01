package com.example.ecommerceorderservicespring.repository;

import com.example.ecommerceorderservicespring.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
