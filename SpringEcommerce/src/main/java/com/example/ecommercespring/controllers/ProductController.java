package com.example.ecommercespring.controllers;

import com.example.ecommercespring.dto.ProductDTO;
import com.example.ecommercespring.dto.ProductWithCategoryDTO;
import com.example.ecommercespring.entity.Product;
import com.example.ecommercespring.services.FakeStoreProductService;
import com.example.ecommercespring.services.IProductService;
import com.example.ecommercespring.services.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final IProductService productService;
    public ProductController(@Qualifier("productService") IProductService concreteClass) { //this is a class constructor because same name as class name
        this.productService = concreteClass;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) throws IOException {
        ProductDTO response = this.productService.getProductById(id);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) throws IOException {
        ProductDTO response = this.productService.createProduct(productDTO);
        return ResponseEntity.ok().body(response);
    }

    // This is a default url which is api/products and we can add any number of requestparams in it.
    @GetMapping
    public ResponseEntity<List<ProductDTO>> findProductsUsingFilters(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId
    ) throws IOException{
        if(categoryId != null){
            List<ProductDTO> allProductsOfACategory = this.productService.getAllProductsOfACategory(categoryId);
            return ResponseEntity.ok().body(allProductsOfACategory);
        }

        else if(keyword !=null && !keyword.isEmpty()){
            List<ProductDTO> productKeywordAndDescription = this.productService.searchProductWithKeywordInNameAndDescription(keyword);
            return ResponseEntity.ok().body(productKeywordAndDescription);
        }

        else if(brand !=null && !brand.isEmpty()){
            List<ProductDTO> products = this.productService.searchByBrandAndMinPrice(brand, minPrice);
            return ResponseEntity.ok().body(products);
        }

        else {
            List<ProductDTO> listOfExpensiveProducts = this.productService.findExpensiveProducts(minPrice);
            return ResponseEntity.ok().body(listOfExpensiveProducts);
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ProductWithCategoryDTO> getProductWithCategoryById(@PathVariable Long id) throws IOException {
        ProductWithCategoryDTO getProductWithCategoryById = this.productService.getProductWithCategoryById(id);
        return ResponseEntity.ok().body(getProductWithCategoryById);
    }

}

// in the url, if there's ?abc = xyz, then abc is a @RequestParam