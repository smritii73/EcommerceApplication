package com.example.ecommercespring.services;

import com.example.ecommercespring.dto.ProductDTO;
import com.example.ecommercespring.dto.ProductWithCategoryDTO;
import com.example.ecommercespring.entity.Product;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    ProductDTO getProductById(Long id) throws IOException;
    ProductDTO createProduct(ProductDTO productDTO) throws IOException;
    List<ProductDTO> findExpensiveProducts(double minPrice) throws IOException;
    List<ProductDTO> searchByBrandAndMinPrice(String brand,double minPrice) throws IOException;
    ProductWithCategoryDTO getProductWithCategoryById(Long id) throws IOException;
    List<ProductDTO> searchProductWithKeywordInNameAndDescription(String keyword) throws IOException;
    List<ProductDTO> getAllProductsOfACategory(Long categoryId) throws IOException;
}
