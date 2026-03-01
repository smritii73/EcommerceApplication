package com.example.ecommercespring.services;

import com.example.ecommercespring.dto.CategoryDTO;
import com.example.ecommercespring.entity.Category;
import com.example.ecommercespring.mappers.CategoryMapper;
import com.example.ecommercespring.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("categoryService")
public class CategoryService implements ICategoryService{
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO> getAllCategories() throws IOException {
        // We created a list to store all the dto
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        // We use .findAll() func to get all categories from the repo layer. This returns a List.
        for(Category category : this.categoryRepository.findAll()){
            // but returned list contains entity as came from repo layer
            CategoryDTO dto = CategoryMapper.toDto(category);
            // so nw we convert to dto as we have to send back to controller as it understand dto
            categoryDTOList.add(dto);
        }
        return categoryDTOList;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) throws IOException {
        // I have a dto, so i need to save it in the Db. To store in db,i have to give it to repo layer
        // But repo understands entity, and not dto so we convert dto to entity
        Category category = CategoryMapper.toEntity(categoryDTO);
        Category savedCategory = this.categoryRepository.save(category); //entity gets stored in db
        //after saving entity, we return to controller. but controller understands dto and we have entity
        // so convert entity to dto
        return CategoryMapper.toDto(savedCategory);
    }
    @Override
    public CategoryDTO findByName(String name) throws IOException {
        Category category = this.categoryRepository.findByName(name)
                .orElseThrow(()-> new IOException("CATEGORY NOT FOUND WITH NAME:"+name));
        return CategoryMapper.toDto(category);
    }
}
/*
* Controller -> Service
* if service wants to talk to 3rd party api, then it will go to gateway
* but if wants to talk to Db, then will go to repositories
* repositories-> will talk to sql
* gateway ->talks to external api using client library like OkHttp and FakeStoreApi
*
*
* */