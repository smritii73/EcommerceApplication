package com.example.ecommercespring.mappers;

import com.example.ecommercespring.dto.CategoryDTO;
import com.example.ecommercespring.entity.Category;

public class CategoryMapper {
    public static Category toEntity(CategoryDTO categoryDTO) {
        return Category.builder()
                .name(categoryDTO.getName())
                .build();
    }
    public static CategoryDTO toDto(Category category) {
        return CategoryDTO.builder()
                .name(category.getName())
                .id(category.getId())
                .build();
    }
}
