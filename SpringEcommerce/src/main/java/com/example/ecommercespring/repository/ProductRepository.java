package com.example.ecommercespring.repository;

import com.example.ecommercespring.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProductRepository extends JpaRepository<Product,Long> {
    //find all expensive products > find all products which have price > minPrice
    // for that we have to use @Query
    // this is a custom query method using Hql -> Hibernate Query Language
    // here we have to explicitly define query logic in hql(not sql)
    // here in hql we deal w entities which is Product entity and not product table
    @Query("SELECT p from Product p WHERE p.price>:minPrice")
    List<Product> findExpensiveProducts(@Param("minPrice") double minPrice);

    //for more complex queries such as
    // if we want to apply limit, regex matching or ILike
    //for such cases we cannot use Hql
    // eg. we search for products whose name or description has a word "keyword"
    // but we dont know the exact name of the procut, for such cases,
    // we have to make a query
    //nativeQuery = true as it is a raw sql query
    @Query(value="Select * FROM product WHERE MATCH(title, description) AGAINST (:keyword)", nativeQuery = true)
    List<Product> searchFullText(@Param("keyword") String keyword);

    //we want to find products by brand and minPrice
    // /api/products?minPrice=5000&brand=Nike
    @Query("SELECT p from Product p WHERE p.price>:minPrice AND p.brand = :brand")
    List<Product> searchByBrandAndMinPrice(
            @Param("brand") String brand,
            @Param("minPrice") double minPrice
    );

    //we want to get all products of a specific category
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> getAllProductsOfACategory(
            @Param("categoryId") Long categoryId
    );

}