package com.example.ecommerceorderservicespring.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {
    private Long productId;
    private int quantity;
}
