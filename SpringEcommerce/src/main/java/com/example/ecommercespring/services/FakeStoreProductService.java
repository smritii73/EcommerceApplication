package com.example.ecommercespring.services;

import com.example.ecommercespring.dto.ProductDTO;
import com.example.ecommercespring.dto.ProductWithCategoryDTO;
import com.example.ecommercespring.gateway.FakeStoreProductGateway;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FakeStoreProductService implements IProductService{
    private final FakeStoreProductGateway fakeStoreProductGateway;

    public FakeStoreProductService(FakeStoreProductGateway fakeStoreProductGateway) {
        this.fakeStoreProductGateway = fakeStoreProductGateway;
    }

    @Override
    public ProductDTO getProductById(Long id) throws IOException {
        ProductDTO response = this.fakeStoreProductGateway.getProductById(id);
        return response;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) throws IOException {
        return null;
    }

    @Override
    public List<ProductDTO> findExpensiveProducts(double minPrice) throws IOException {
        return List.of();
    }
    public List<ProductDTO> searchByBrandAndMinPrice(String brand, double minPrice) throws IOException{
        return List.of();
    }

    @Override
    public ProductWithCategoryDTO getProductWithCategoryById(Long id) throws IOException {
        return null;
    }

    @Override
    public List<ProductDTO> searchProductWithKeywordInNameAndDescription(String keyword) throws IOException {
        return List.of();
    }

    @Override
    public List<ProductDTO> getAllProductsOfACategory(Long categoryId) throws IOException {
        return List.of();
    }

}
