package com.example.ecommerceorderservicespring.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class AppConfig {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
// adding LoadBalanced on a RestTemplate basically enables client side balancing
// Its now smart enough to intercept the local service names and resolve them into actual instance urls from Eureka

// Eureka client > ProductService
// Suppose ProductService running on multiple IP's [10.0.0.12:8000, 10.0.0.13:8000, ...]
// As the ProductService is not a DNS name,
// @LoadBalanced annotation contacts Eureka > fetches the available instance > picks 1
// RestTemplate.getForObject("http://10.0.0.13:8000/api/products/1", ProductDTO.class)

