package com.example.ecommercespring.repository;

import com.example.ecommercespring.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    //now if we want some custom query like findCategoryByName() then we have to define the name
    // jpa is smart enough to understand it
    //now the moment we write somethin like this, jpa will automatically understand
    // this is relying on method naming
    Optional<Category> findByName(String name);
    
}

