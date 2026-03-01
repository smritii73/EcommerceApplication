package com.example.ecommercespring.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductWithCategoryDTO {
    private String image;
    private String color;
    private int price;
    private String description;
    private int discount;
    private String model;
    private Long id;
    private String title;
    private Long categoryId;
    private String brand;
    private boolean popular;
    private CategoryDTO category;
}
// in this dto, we will keep product dto + category dto