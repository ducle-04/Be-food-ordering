package com.example.b_food_ordering.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name; // Tên sản phẩm
    
    private String description; // Mô tả sản phẩm
    
    @Column(nullable = false)
    private double originalPrice; // Giá gốc
    
    private double discountedPrice; // Giá sau khi giảm
    
    private double discount; // Phần trăm giảm giá
    
    @ManyToOne
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductType productType; // Loại sản phẩm (ví dụ: "Food", "Drink", "Dessert")
    
    private String img; // Đường dẫn ảnh sản phẩm
    
    @Column(nullable = false)
    private String status; // Trạng thái (ví dụ: "AVAILABLE", "OUT_OF_STOCK", "DISCONTINUED")
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // Danh mục đặc biệt (ví dụ: "FEATURED", "NEW", "BESTSELLER")

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}