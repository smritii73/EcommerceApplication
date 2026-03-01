package com.example.ecommerceorderservicespring.dto;

import com.example.ecommerceorderservicespring.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderResponseDTO {
    private Long orderId;
    private OrderStatus orderStatus;
}
