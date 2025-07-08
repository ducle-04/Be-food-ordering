package com.example.b_food_ordering.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.b_food_ordering.Entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
	// Tìm sản phẩm theo danh mục (category)
    List<Product> findByCategory(String category);

    // Tìm sản phẩm theo tên (gần đúng, không phân biệt hoa thường)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Kiểm tra sự tồn tại của sản phẩm theo tên
    boolean existsByName(String name);
}
