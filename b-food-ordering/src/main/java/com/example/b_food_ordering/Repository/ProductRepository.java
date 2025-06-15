package com.example.b_food_ordering.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.b_food_ordering.Entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
}
