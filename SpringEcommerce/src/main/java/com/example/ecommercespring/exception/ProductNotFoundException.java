package com.example.ecommercespring.exception;

public class ProductNotFoundException extends RuntimeException {
    // We extend runtime exception as it is an unchecked exception
    public ProductNotFoundException(String message) {
        super(message);
    }
}
//Why extends RuntimeException?
// Because it marks it an unchecked exception and benefit of it is callers arent forced to declare a catch it

