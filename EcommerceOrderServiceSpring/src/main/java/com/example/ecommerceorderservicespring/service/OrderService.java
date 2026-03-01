package com.example.ecommerceorderservicespring.service;

import com.example.ecommerceorderservicespring.clients.ProductServiceClient;
import com.example.ecommerceorderservicespring.dto.CreateOrderResponseDTO;
import com.example.ecommerceorderservicespring.dto.OrderItemRequestDTO;
import com.example.ecommerceorderservicespring.dto.OrderRequestDTO;
import com.example.ecommerceorderservicespring.dto.ProductDTO;
import com.example.ecommerceorderservicespring.entity.Order;
import com.example.ecommerceorderservicespring.entity.OrderItem;
import com.example.ecommerceorderservicespring.mapper.OrderItemRequestMapper;
import com.example.ecommerceorderservicespring.mapper.OrderMapper;
import com.example.ecommerceorderservicespring.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    public OrderService(OrderRepository orderRepository, ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
    }

    @Override
    public CreateOrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        // we need to firstly persist the order in the order table > OrderRequestDTO > OrderEntity
        Order order = OrderMapper.toEntity(orderRequestDTO);
        List<OrderItem> items = new ArrayList<>();
        for(OrderItemRequestDTO orderItemRequestDTO : orderRequestDTO.getItems()) {
            ProductDTO product = this.productServiceClient.getProductById(orderItemRequestDTO.getProductId());
            double pricePerUnit = product.getPrice();
            double totalPrice = pricePerUnit * orderItemRequestDTO.getQuantity();
            OrderItem item = OrderItemRequestMapper.orderItemRequestDtoToOrderItemEntity(
                    orderItemRequestDTO,
                    order,
                    pricePerUnit,
                    totalPrice
            );
            items.add(item);
        }
        order.setItems(items);
        Order savedOrder = this.orderRepository.save(order);
        return OrderMapper.toCreateOrderResponseDTO(savedOrder);
    }
}