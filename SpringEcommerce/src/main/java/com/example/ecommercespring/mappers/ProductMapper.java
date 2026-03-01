package com.example.ecommercespring.mappers;

import com.example.ecommercespring.dto.ProductDTO;
import com.example.ecommercespring.dto.ProductWithCategoryDTO;
import com.example.ecommercespring.entity.Category;
import com.example.ecommercespring.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductMapper {
    public static ProductDTO toDto(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .image(product.getImage())
                .color(product.getColor())
                .price(product.getPrice())
                .description(product.getDescription())
                .discount(product.getDiscount() )
                .model(product.getModel())
                .title(product.getTitle())
                .categoryId(product.getCategory().getId())
                .brand(product.getBrand())
                .popular(product.isPopular())
                .build();
    }

    public static Product toEntity(ProductDTO productDTO, Category category) {
        return Product.builder()
                .image(productDTO.getImage())
                .color(productDTO.getColor())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .discount(productDTO.getDiscount())
                .model(productDTO.getModel())
                .title(productDTO.getTitle())
                .category(category)
                .brand(productDTO.getBrand())
                .popular(productDTO.isPopular())
                .build();
    }
    public static List<ProductDTO> toDtoList(List<Product> products) {
        List<ProductDTO> dtos = new ArrayList<>();
        for(Product product : products){
            ProductDTO dto = toDto(product);
            dtos.add(dto);
        }
        return dtos;
    }

    public static ProductWithCategoryDTO toProductWithCategoryDTO(Product product){
        // this func helps convert product entity to product with category dto
        return ProductWithCategoryDTO.builder()
                .id(product.getId())
                .image(product.getImage())
                .color(product.getColor())
                .price(product.getPrice())
                .description(product.getDescription())
                .discount(product.getDiscount() )
                .model(product.getModel())
                .title(product.getTitle())
                .categoryId(product.getCategory().getId())
                .brand(product.getBrand())
                .popular(product.isPopular())
                // every product entity has a category entity in it. we get category entity inside of a product entity
                // using .getCategory() getter of that product entity
                // and we convert that category entity to category dto using category mapper
                .category(CategoryMapper.toDto(product.getCategory()))
                .build();

    }


}
