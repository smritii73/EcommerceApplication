package com.example.ecommercespring.controllers;


import com.example.ecommercespring.dto.CategoryDTO;
import com.example.ecommercespring.services.ICategoryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ICategoryService categoryService;

    public CategoryController(@Qualifier("categoryService") ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    // ? is a generic method signature
    public ResponseEntity<?> getAllCategories(@RequestParam(required = false) String name) throws IOException {
        if(name!= null && !name.isEmpty()) {
            CategoryDTO categoryDto= this.categoryService.findByName(name);
            return ResponseEntity.ok().body(categoryDto);
        }
        else {
            List<CategoryDTO> result= this.categoryService.getAllCategories();
            return ResponseEntity.ok().body(result);
        }
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) throws IOException {
        CategoryDTO created = this.categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body(created);
    }
}
