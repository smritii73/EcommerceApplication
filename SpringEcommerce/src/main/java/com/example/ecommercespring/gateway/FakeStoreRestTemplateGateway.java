package com.example.ecommercespring.gateway;

import com.example.ecommercespring.dto.CategoryDTO;
import com.example.ecommercespring.dto.FakeStoreCategoryResponseDTO;
import com.example.ecommercespring.mappers.GetAllCategoriesMapper;
import okhttp3.Response;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Component("fakeStoreRestTemplateGateway")
public class FakeStoreRestTemplateGateway implements ICategoryGateway {
    private final RestTemplateBuilder restTemplateBuilder;

    FakeStoreRestTemplateGateway(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Override
    public List<CategoryDTO> getAllCategories() throws IOException {
        RestTemplate restTemplate = this.restTemplateBuilder.build();
        String url = "https://fakestoreapi.in/api/products/category";
        ResponseEntity<FakeStoreCategoryResponseDTO> response = restTemplate.getForEntity(url, FakeStoreCategoryResponseDTO.class);
        if (response.getBody() == null)
        {
            throw new IOException("Failed to fetch categories from FakeStore Api");
        }
        return GetAllCategoriesMapper.toCategoryDto(response.getBody());
    }
}