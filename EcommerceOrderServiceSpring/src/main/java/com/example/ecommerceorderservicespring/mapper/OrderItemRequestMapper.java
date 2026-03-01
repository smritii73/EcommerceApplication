package com.example.ecommerceorderservicespring.mapper;

import com.example.ecommerceorderservicespring.dto.OrderItemRequestDTO;
import com.example.ecommerceorderservicespring.entity.Order;
import com.example.ecommerceorderservicespring.entity.OrderItem;

public class OrderItemRequestMapper {
    public static OrderItem orderItemRequestDtoToOrderItemEntity(OrderItemRequestDTO orderItemRequestDto, Order order, double pricePerUnit, double totalPrice) {
        return OrderItem.builder()
                .productId(orderItemRequestDto.getProductId())
                .quantity(orderItemRequestDto.getQuantity())
                .pricePerUnit(pricePerUnit)
                .totalPrice(totalPrice)
                .order(order)
                .build();
    }
}
