package com.example.ecommerceorderservicespring.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private Long userId;
    private List<OrderItemRequestDTO> items;
}
