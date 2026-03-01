package com.example.ecommercespring.mappers;

import com.example.ecommercespring.dto.CategoryDTO;
import com.example.ecommercespring.dto.FakeStoreCategoryResponseDTO;

import java.util.List;

public class GetAllCategoriesMapper {
    public static FakeStoreCategoryResponseDTO getFakeStoreCategoryResponseDTO() {
        return null;
    }
    public static List<CategoryDTO> toCategoryDto(FakeStoreCategoryResponseDTO dto) {
        return dto.getCategories().stream()
                .map(category-> CategoryDTO.builder()
                        .name(category)
                        .build())
                .toList();
    }
}