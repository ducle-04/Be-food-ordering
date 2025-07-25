package com.example.b_food_ordering.Repository;

import com.example.b_food_ordering.Entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
    Optional<ProductType> findByName(String name);
}