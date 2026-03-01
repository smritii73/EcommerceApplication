package com.example.ecommercespring.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class FakeStoreProductResponseDTO{
	private ProductDTO product;
	private String message;
	private String status;

}