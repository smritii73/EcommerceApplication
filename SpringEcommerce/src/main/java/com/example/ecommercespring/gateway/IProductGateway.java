package com.example.ecommercespring.gateway;

import com.example.ecommercespring.dto.ProductDTO;

import java.io.IOException;

public interface IProductGateway {
    ProductDTO getProductById(Long id) throws IOException;
}
