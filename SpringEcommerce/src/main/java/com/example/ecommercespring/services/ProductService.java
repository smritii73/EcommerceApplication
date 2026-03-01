package com.example.ecommercespring.services;

import com.example.ecommercespring.dto.ProductDTO;
import com.example.ecommercespring.dto.ProductWithCategoryDTO;
import com.example.ecommercespring.entity.Category;
import com.example.ecommercespring.entity.Product;
import com.example.ecommercespring.exception.ProductNotFoundException;
import com.example.ecommercespring.mappers.ProductMapper;
import com.example.ecommercespring.repository.CategoryRepository;
import com.example.ecommercespring.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("productService")
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public ProductDTO getProductById(Long id) throws IOException {
        //.findById returns an entity enclosed in Optional<Entity>
        //Optional ensures that if null is also returned, then we have to handle it separately
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
        //If null encountered, exception will be thrown
        ProductDTO productDTO = ProductMapper.toDto(product);
        return productDTO;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) throws IOException {
        // Now we have a dto, we have to save it so we pass it to the repo, and repo will save it in the db.
        //but repo understands entity. so convert dto to entity.
        //so we use prod mapper to convert dto to entity but to convert to entity, we also need category.
        // we HAVE TO create category entity of that productdto using findbyid() of our productdto category id
        Long CategoryId = productDTO.getCategoryId();
        Category category = this.categoryRepository.findById(CategoryId)
                .orElseThrow(() -> new IOException("Category not found"));
        // now we have category entity,so we convert productdto to productentity using productmapper
        Product product = ProductMapper.toEntity(productDTO,category);
        Product save = this.productRepository.save(product);  // saves entity in the Database
        //now go back to controller so convert entity to dto
        return ProductMapper.toDto(save);
    }
    @Override
    public List<ProductDTO> findExpensiveProducts(double minPrice) throws IOException{
        List<Product> listOfExpensiveProducts = this.productRepository.findExpensiveProducts(minPrice);
//        List<ProductDTO> productDTOS = new ArrayList<>();
//        for(Product i : listOfExpensiveProducts){
//            ProductDTO productDTO = ProductMapper.toDto(i);
//            productDTOS.add(productDTO);
//        }
        List<ProductDTO> productDTOS = ProductMapper.toDtoList(listOfExpensiveProducts);
        return productDTOS;
    }

    @Override
    public List<ProductDTO> searchByBrandAndMinPrice(String brand, double minPrice) throws IOException {
        return ProductMapper.toDtoList(this.productRepository.searchByBrandAndMinPrice(brand, minPrice));
    }

    @Override
    public ProductWithCategoryDTO getProductWithCategoryById(Long id) throws IOException {
        // we firstly get the product using the id and convert product entity to product with category dto
        Product product = this.productRepository.findById(id)
                .orElseThrow(()-> new IOException("Product not found"));
        return ProductMapper.toProductWithCategoryDTO(product);
    }

    @Override
    public List<ProductDTO> searchProductWithKeywordInNameAndDescription(String keyword) throws IOException {
       return ProductMapper.toDtoList(this.productRepository.searchFullText(keyword));
    }
    //   api/products?keyword=nike -> this url

    @Override
    public List<ProductDTO> getAllProductsOfACategory(Long categoryId) throws IOException {
        return ProductMapper.toDtoList(this.productRepository.getAllProductsOfACategory(categoryId));
    }

}