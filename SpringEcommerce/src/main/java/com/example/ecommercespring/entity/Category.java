package com.example.ecommercespring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Category extends BaseEntity{
    @Column(nullable = false,unique = true)
    private String name;

    // 1 category has many products
    // List doesnt mean that you are trying to store list of products inside category table
    // Telling JPA, relationship already onwned by prod entity so just read from there
    @OneToMany(mappedBy = "category" , fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products;

}