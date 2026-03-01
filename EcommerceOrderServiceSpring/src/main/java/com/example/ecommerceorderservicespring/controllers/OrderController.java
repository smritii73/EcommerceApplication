package com.example.ecommerceorderservicespring.controllers;

import com.example.ecommerceorderservicespring.dto.CreateOrderResponseDTO;
import com.example.ecommerceorderservicespring.dto.OrderRequestDTO;
import com.example.ecommerceorderservicespring.service.IOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final IOrderService orderService;
    public OrderController(IOrderService orderService){
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<CreateOrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderRequestDto) {
        CreateOrderResponseDTO createOrderResponseDTO = this.orderService.createOrder(orderRequestDto);
        return new ResponseEntity<>(createOrderResponseDTO, HttpStatus.CREATED);
    }
}
