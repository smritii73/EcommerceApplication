package com.example.ecommerceorderservicespring.service;

import com.example.ecommerceorderservicespring.dto.CreateOrderResponseDTO;
import com.example.ecommerceorderservicespring.dto.OrderRequestDTO;

public interface IOrderService {
    public CreateOrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO);
}