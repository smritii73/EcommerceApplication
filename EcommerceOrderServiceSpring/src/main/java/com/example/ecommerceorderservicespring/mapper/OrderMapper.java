package com.example.ecommerceorderservicespring.mapper;

import com.example.ecommerceorderservicespring.dto.CreateOrderResponseDTO;
import com.example.ecommerceorderservicespring.dto.OrderRequestDTO;
import com.example.ecommerceorderservicespring.entity.Order;
import com.example.ecommerceorderservicespring.enums.OrderStatus;

public class OrderMapper {
    public static Order toEntity(OrderRequestDTO orderRequestDTO) {
        return Order.builder()
                .userId(orderRequestDTO.getUserId())
                .orderStatus(OrderStatus.PENDING)
                .build();
    }
    public static CreateOrderResponseDTO toCreateOrderResponseDTO(Order order) {
        return CreateOrderResponseDTO.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
