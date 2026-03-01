package com.example.ecommerceorderservicespring.clients;

import com.example.ecommerceorderservicespring.dto.ProductDTO;
import jakarta.persistence.Column;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
@Component
public class ProductServiceClient {
    private final RestTemplate restTemplate;
    public ProductServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public ProductDTO getProductById(Long productId){
        String url = "http://ECOMMERCESPRING/api/products/" + productId;
        //this is not a url so RestTemplateBuilder will not understand it so we donot use RestTemplateBuilder
        ResponseEntity<ProductDTO> response = restTemplate.getForEntity(url, ProductDTO.class);
        return response.getBody();
    }
    //Do we need API gateway?
    // No.We need gateway when interacting with 3rd party api/service
    // When our service is communicating w other service or in microservice, we dont need gateway
}
